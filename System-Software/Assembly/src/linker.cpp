#include "../inc/linker.hpp"
#include <iostream>
#include <fstream>
#include <iomanip>
#include <algorithm>

// Initialize the static index
//starts from 1 because 0 is reserved for UNDEF section index
int linker_section_entry::next_idx = 1;


asm_section_entry::asm_section_entry(int idx, const std::string& name, const std::string& file, addr_t base_addr,
                                           size_t section_size, const std::vector<byte>& data)
    : idx(idx), name(name), file_name(file), base_addr(base_addr), section_size(section_size), data(data) {}

void asm_section_entry::print_info(std::ostream& output) const {
    std::ios_base::fmtflags original_flags = output.flags();

    output << std::hex << std::uppercase << std::right;

    output << "===============================\n";
    output << "   Section Entry Information\n";
    output << "===============================\n\n";

    output << "Index            : " << std::setw(4) << std::setfill(' ') << std::dec << idx << "\n";
    output << "Name             : " << name << "\n";
    output << "File name        : " << file_name << "\n";
    output << "Base Address     : 0x" << std::setw(8) << std::setfill('0') << std::hex << base_addr << "\n";
    output << "Section Size     : " << std::setfill(' ') << std::dec << std::setw(4) << section_size << " bytes\n";

    output.flags(original_flags);
}

void asm_section_entry::print_data(std::ostream& output) const {
    // Print the section name as a header
    output << "-------------------------------\n";
    output << "   Data Segment #." << name << "\n";
    output << "-------------------------------\n\n";

    print_byte_vector(data, output, 0, 8);
}

// Initialize static member
linker* linker::instance_ptr = nullptr;

// Get the singleton instance
linker* linker::get_instance() {
    if (instance_ptr == nullptr) {
        instance_ptr = new linker();
    }
    return instance_ptr;
}

// Private constructor for singleton
linker::linker() {
    // Initialize data members if needed
    symbol_entry* zero_symbol = new symbol_entry("", 0, 0, STT_NOTYPE, STB_LOCAL, true);
    linker_symbol_table.push_back(zero_symbol);
}

// Print the initialized values
void linker::print_summary() {
    std::cout << "Output file: " << this->output_file_name << std::endl;
    
    std::cout << "Input files: ";
    for (const auto& file : this->input_files) {
        std::cout << file << " ";
    }
    std::cout << std::endl;
    
    std::cout << "Sections to be placed:" << std::endl;
    for (const auto& section : this->to_be_placed) {
        std::cout << section.first << " at address 0x" << std::hex << section.second << std::endl;
    }
    
    std::cout << "Relocatable: " << (this->is_relocatable ? "Yes" : "No") << std::endl;
    std::cout << "Hex Output: " << (this->is_hex ? "Yes" : "No") << std::endl;
}

int linker::process_cmd_line_args(int argc, char* argv[]) {
    if (argc < 2) {
        std::cerr << "Usage: linker [-o <output_file_name>] [-place=section@address]... [-hex | -relocatable] <input_file1> [<input_file2> ...]\n";
        return -1;  // Error: Incorrect usage
    }

    bool output_file_set = false;  // Flag to check if output file name is set

    std::regex place_section_regex("^-place=([a-zA-Z_][a-zA-Z_0-9]*)@(0[xX][0-9A-Fa-f]+)$");
    std::smatch matched_arguments;  // To store regex match results

    // Iterate over command-line arguments
    for (int i = 1; i < argc; ++i) {
        std::string arg = argv[i];  // Current argument

        // Handle output file option
        if (arg == "-o") {
            if (i + 1 < argc) {    // Ensure next argument exists
                output_file_name = argv[++i];  // Set output file name
                output_file_set = true;  // Mark output file name as set
            } else {
                std::cerr << "Error: Missing output file name after -o option.\n";
                return -1;  // Error: Missing output file name
            }
        }
        // Handle section placement arguments
        else if (std::regex_search(arg, matched_arguments, place_section_regex)) {
            std::string section = matched_arguments.str(1);  // Extract section name
            std::string address = matched_arguments.str(2);  // Extract address
            addr_t section_addr = static_cast<addr_t>(std::stoul(address, nullptr, 16));  // Convert to addr_t
            to_be_placed[section] = section_addr;  // Map section to address
        }
        // Handle hexadecimal output flag
        else if (arg == "-hex") {
            if (is_relocatable) {
                std::cerr << "-hex and -relocatable options cannot both be entered" << std::endl;
                return -1;  // Error: Conflicting options
            }
            is_hex = true;  // Set hexadecimal output flag
        }
        // Handle relocatable output flag
        else if (arg == "-relocatable") {
            if (is_hex) {
                std::cerr << "-hex and -relocatable options cannot both be entered" << std::endl;
                return -1;  // Error: Conflicting options
            }
            is_relocatable = true;  // Set relocatable output flag
        }
        // Handle input file names
        else if (arg[0] != '-') {
            input_files.push_back(arg);  // Add input file name to the list
        } else {
            std::cerr << "Unknown option: " << arg << "\n";
            return -1;  // Error: Unknown option
        }
    }

    // Ensure at least one input file is provided
    if (input_files.empty()) {
        std::cerr << "Error: No input files provided.\n";
        return -1;  // Error: No input files
    }

    // Ensure at least one of -hex or -relocatable is set
    if (!is_hex && !is_relocatable) {
        std::cerr << "Error: Missing flag. Either -hex or -relocatable must be specified.\n";
        return -1;  // Error: Missing required flag
    }

    // Custom default output name if not specified
    if (!output_file_set) {
        output_file_name = "linker_output";
        if (is_hex) {
            output_file_name += ".hex";
        } else if (is_relocatable) {
            output_file_name += ".o";
        }
    }
    if (is_relocatable) {
        // Clear the sections_to_be_placed map if the output should be relocatable
        to_be_placed.clear();
    }

    return 0;  // Success
}


int linker::process_input_files() {
    for (const auto& file : input_files) {
        if (read_obj_file(file) != 0) {
            std::cerr << "Error: Failed to read object file: " << file << "\n";
            return -1;  // Exit the loop and return an error if any file fails to be read
        }
    }
    return 0;  // Success
}


int linker::read_obj_file(const std::string& inputFileName) {
    std::ifstream inputFile(inputFileName, std::ios::binary);
    if (!inputFile.is_open()) {
        std::cerr << "Failed to open file for reading." << std::endl;
        return -1;
    }

    try {
        // Temporary tables for storing the data while reading the file
        std::vector<symbol_entry*> symbol_table;         // Temporary symbol table for the current file
        std::vector<asm_section_entry*> sections_table; // Temporary sections table for the current file
        std::vector<relocs_entry*> relocations;          // Temporary relocation entries for the current file

        // Deserialize symbol table
        size_t numSymbols;
        inputFile.read(reinterpret_cast<char*>(&numSymbols), sizeof(numSymbols));
        symbol_table.resize(numSymbols);

        for (size_t i = 0; i < numSymbols; ++i) {
            auto symbol = new symbol_entry();
            inputFile.read(reinterpret_cast<char*>(&symbol->idx), sizeof(symbol->idx));

            size_t nameLength;
            inputFile.read(reinterpret_cast<char*>(&nameLength), sizeof(nameLength));
            symbol->name.resize(nameLength);
            inputFile.read(&symbol->name[0], nameLength);

            inputFile.read(reinterpret_cast<char*>(&symbol->offset), sizeof(symbol->offset));
            inputFile.read(reinterpret_cast<char*>(&symbol->size), sizeof(symbol->size));
            inputFile.read(reinterpret_cast<char*>(&symbol->symbol_type), sizeof(symbol->symbol_type));
            inputFile.read(reinterpret_cast<char*>(&symbol->binding), sizeof(symbol->binding));
            inputFile.read(reinterpret_cast<char*>(&symbol->is_defined), sizeof(symbol->is_defined));
            inputFile.read(reinterpret_cast<char*>(&symbol->section_idx), sizeof(symbol->section_idx));

            size_t sctnNameLength;
            inputFile.read(reinterpret_cast<char*>(&sctnNameLength), sizeof(sctnNameLength));
            symbol->section_name.resize(sctnNameLength);
            inputFile.read(&symbol->section_name[0], sctnNameLength);

            // Deserialize forward references
            size_t numReferences;
            inputFile.read(reinterpret_cast<char*>(&numReferences), sizeof(numReferences));
            symbol->forward_references.resize(numReferences);
            for (size_t j = 0; j < numReferences; ++j) {
                int patch_offset, section_idx;
                inputFile.read(reinterpret_cast<char*>(&patch_offset), sizeof(patch_offset));
                inputFile.read(reinterpret_cast<char*>(&section_idx), sizeof(section_idx));
                symbol->forward_references[j] = new flink_entry(patch_offset, section_idx);
            }

            symbol_table[i] = symbol;
        }
        //std::cout << "Deserializing symbol table done" << std::endl;
        // Deserialize section table
        size_t numSections;
        inputFile.read(reinterpret_cast<char*>(&numSections), sizeof(numSections));
        sections_table.resize(numSections);

        for (size_t i = 0; i < numSections; ++i) {
            int idx;
            std::string name;
            addr_t base_addr;
            size_t section_size;

            std::vector<byte> data;

            inputFile.read(reinterpret_cast<char*>(&idx), sizeof(idx));
            size_t nameLength;
            inputFile.read(reinterpret_cast<char*>(&nameLength), sizeof(nameLength));
            name.resize(nameLength);
            inputFile.read(&name[0], nameLength);

            inputFile.read(reinterpret_cast<char*>(&base_addr), sizeof(base_addr));
            inputFile.read(reinterpret_cast<char*>(&section_size), sizeof(section_size));
    
            size_t data_size;
            inputFile.read(reinterpret_cast<char*>(&data_size), sizeof(data_size));
            data.resize(data_size);
            inputFile.read(reinterpret_cast<char*>(data.data()), data_size);

            // Create linker_section_entry
            auto section = new asm_section_entry(idx, name, inputFileName, base_addr, section_size, data);
            sections_table[i] = section;
        }
        //std::cout << "Deserializing sections table done" << std::endl;
        // Deserialize relocations table
        size_t numRelocations;
        inputFile.read(reinterpret_cast<char*>(&numRelocations), sizeof(numRelocations));
        relocations.resize(numRelocations);

        for (size_t i = 0; i < numRelocations; ++i) {
            std::string section_name;
            int section_idx, symbol_idx, type;
            size_t offset, addend;
            bool is_resolved;

            size_t sectionNameLength;
            inputFile.read(reinterpret_cast<char*>(&sectionNameLength), sizeof(sectionNameLength));
            section_name.resize(sectionNameLength);
            inputFile.read(&section_name[0], sectionNameLength);

            inputFile.read(reinterpret_cast<char*>(&section_idx), sizeof(section_idx));
            inputFile.read(reinterpret_cast<char*>(&symbol_idx), sizeof(symbol_idx));
            inputFile.read(reinterpret_cast<char*>(&type), sizeof(type));
            inputFile.read(reinterpret_cast<char*>(&offset), sizeof(offset));
            inputFile.read(reinterpret_cast<char*>(&addend), sizeof(addend));
            inputFile.read(reinterpret_cast<char*>(&is_resolved), sizeof(is_resolved));

            relocations[i] = new relocs_entry(section_name, section_idx, symbol_idx, type, offset, addend, is_resolved);
        }
        //std::cout << "Deserializing relocations done" << std::endl;
        // After reading the file, add the data to the global maps
        file_sym_table_map[inputFileName] = std::unordered_map<std::string, symbol_entry*>();
        for (auto sym : symbol_table) {
            file_sym_table_map[inputFileName][sym->name] = sym;
        }

        file_sctn_table_map[inputFileName] = sections_table;

        file_relocs_table_map[inputFileName] = std::unordered_map<std::string, std::vector<relocs_entry*>>();
        for (auto reloc : relocations) {
            file_relocs_table_map[inputFileName][reloc->section_name].push_back(reloc);
        }
    } catch (const std::exception& e) {
        std::cerr << "Error reading file: " << e.what() << std::endl;
        return -1;
    }

    inputFile.close();
    return 0;
}

void linker::place_section(linker_section_entry* new_sec) {
    // Place the new section
    placed_sections_map[new_sec->base_addr] = new_sec;
}

void linker::create_new_section(const asm_section_entry* section, addr_t start_addr) {

    linker_section_entry* new_sec = new linker_section_entry(
        section->name,
        start_addr,
        section->section_size,
        false
    );

    new_sec->data = section->data;
    new_sec->subsections_map[section->file_name] = start_addr;
    linker_section_table[section->name] = new_sec;
    output_sections.push_back(new_sec);
    curr_size += section->section_size;
}

void linker::print_symbol_table(std::ostream& output) const {
    // Print headers
    output << "#.symtab" << std::endl;
    output << std::setfill(' ')
           << std::setw(4)  << "Num" << "  " 
           << std::setw(8)  << "Value" << "  " 

           << std::setw(4)  << "Size" << "  " 
           << std::setw(5)  << "Type" << "  " 
           << std::setw(4)  << "Bind" << "  " 
           << std::setw(4)  << "Ndx" << "  "
           << std::setw(3)  << "Def" << "  "
           << "Name" << std::endl;  // Column headers

    // Print each symbol entry
    for (const auto& symbol : linker_symbol_table) {
        if (symbol) {
            // Print each symbol entry using the passed output stream
            symbol->print(output);  // Print symbol entry details
        }
    }
}

// Function to print data from all linker_section_entry objects in output_sections
void linker::print_linker_sections_data(std::ostream& output) const {
    output << "Current data size: " << std::dec << curr_size << std::endl;

    for (const auto& section : output_sections) {
        if (section) {
            output << "Section Name: " << section->name << std::endl;
            output << "Base Address: " << std::hex << section->base_addr << std::endl;
            output << "Section Size: " << section->section_size << std::dec << std::endl;

            // Print the subsections_map
            if (!section->subsections_map.empty()) {
                output << "Subsections:" << std::endl;
                for (const auto& subsection : section->subsections_map) {
                    output << "  File.Section: " << subsection.first 
                           << ", Address: " << std::hex << subsection.second << std::dec << std::endl;
                }
            } else {
                output << "  No subsections found." << std::endl;
            }

            output << std::endl;
            output << "Data:" << std::endl;
            print_byte_vector(section->data, output, section->base_addr, 8);
        } else {
            output << "Warning: Null section entry found in output_sections." << std::endl;
        }
    }
}

void linker::rearrange_relocatable_sections() {
    if (is_relocatable) {
        // Iterate through each input file
        for (const auto& file : input_files) {
            // Get sections from the current file
            auto it = file_sctn_table_map.find(file);
            if (it == file_sctn_table_map.end()) {
                std::cerr << "Error: File not found in section table map: " << file << std::endl;
                continue;
            }

            const auto& sections = it->second;

            // Iterate through each section
            for (const auto& section : sections) {
                auto linker_it = linker_section_table.find(section->name);

                if (linker_it == linker_section_table.end()) {
                    // Section does not exist, create a new entry with start address 0
                    create_new_section(section, 0);

                } else {
                    // Section exists, update information
                    linker_section_entry* existing_sec = linker_it->second;
                    addr_t base_addr = existing_sec->base_addr + existing_sec->section_size;
                    //update_section_map(file, section->name, base_addr);
                    existing_sec->subsections_map[file] = base_addr;
                    
                    // Append data from the currently analyzed section
                    existing_sec->data.insert(existing_sec->data.end(), section->data.begin(), section->data.end());

                    // Update the size of the existing section
                    existing_sec->section_size += section->section_size;
                    curr_size += section->section_size;
                    // No need to move sections, since they all start from zero
                }
            }
        }
    }
}

void linker::rearrange_hex_sections() {
    //All sections should be placed and there is sections addresses predefined
    if (is_hex && !to_be_placed.empty()) {
        addr_t next_addr = 0; // Initialize the next address to track the placement of sections
        //For each file in order of their appearance in command line
        for (const auto& file : input_files) {
            auto it = file_sctn_table_map.find(file);
            if (it == file_sctn_table_map.end()) {
                std::cerr << "Error: File not found in section table map: " << file << std::endl;
                continue;
            }

            const auto& sections = it->second;

            for (const auto& section : sections) {
                auto linker_it = linker_section_table.find(section->name);
                if (linker_it == linker_section_table.end()) {
                    // Section does not exist in the linker section table

                    bool is_placed;
                    addr_t base_address = 0;

                    // Check if the section is found in the sections_placed map
                    auto itPlace = to_be_placed.find(section->name);

                    if (itPlace != to_be_placed.end()) {
                        is_placed = true;
                        base_address = itPlace->second;

                        linker_section_entry* new_sec = new linker_section_entry(
                            section->name,
                            base_address,
                            section->section_size,
                            is_placed
                        );
                        new_sec->data = section->data;
                        new_sec->subsections_map[file] = base_address;
                    
                        // Add the new section to the linker section table
                        place_section(new_sec);
                        linker_section_table[section->name] = new_sec;
                        //output_sections.push_back(new_sec);
                        //update_section_map(file, section->name, base_address);

                        curr_size += section->section_size;
                        next_addr = std::max(next_addr, base_address + static_cast<addr_t>(section->section_size));
                        
                    } else {
                        unfinished_sections.push_back(section);
                    }
                } else {
                    // Section exists in the linker section table
                    linker_section_entry* existing_sec = linker_it->second;
                    addr_t base_addr = existing_sec->base_addr + existing_sec->section_size;
                    //update_section_map(file, section->name, base_addr);
                    existing_sec->subsections_map[file] = base_addr;

                    existing_sec->section_size += section->section_size;
                    existing_sec->data.insert(existing_sec->data.end(), section->data.begin(), section->data.end());
                    curr_size += section->section_size;
                    next_addr = std::max(next_addr, existing_sec->base_addr + static_cast<addr_t>(existing_sec->section_size));
                    
                }
            }
        }
        for (const auto& section_pair : placed_sections_map) {
           linker_section_entry* sec1 = section_pair.second;
           output_sections.push_back(sec1);
        }

        // Check and process unfinished sections only if there are any
        if (!unfinished_sections.empty()) {
            for (asm_section_entry* unfinished : unfinished_sections) {
                auto iterFinal = linker_section_table.find(unfinished->name);

                if (iterFinal == linker_section_table.end()) {

                    // Section does not exist in the linker section table
                    linker_section_entry* new_sec = new linker_section_entry(
                            unfinished->name,
                            next_addr,
                            unfinished->section_size,
                            false
                    );
                    new_sec->data = unfinished->data;
                    new_sec->subsections_map[unfinished->file_name] = next_addr;

                    linker_section_table[unfinished->name] = new_sec;
                    curr_size += unfinished->section_size;
                    next_addr += unfinished->section_size;
                    output_sections.push_back(new_sec);
                    
                    
                } else {
                    // Section already exists in the linker section table
                    linker_section_entry* existing_sec = iterFinal->second;
                    addr_t base_addr = existing_sec->base_addr + existing_sec->section_size;
                    existing_sec->subsections_map[unfinished->file_name] = base_addr;
                    existing_sec->section_size +=unfinished->section_size;
                    existing_sec->data.insert(existing_sec->data.end(), unfinished->data.begin(), unfinished->data.end());
                    curr_size += unfinished->section_size;
                    next_addr = std::max(next_addr, existing_sec->base_addr + static_cast<addr_t>(existing_sec->section_size));
                    shift_sections_after(unfinished->name, unfinished->section_size);
                }
            }
        }
        check_for_overlaps(); // Ensure no overlapping sections

    } else if (is_hex && to_be_placed.empty()) {
        for (const auto& file : input_files) {
            // Get sections from the current file
            auto it = file_sctn_table_map.find(file);
            if (it == file_sctn_table_map.end()) {
                std::cerr << "Error: File not found in section table map: " << file << std::endl;
                continue;
            }

            const auto& sections = it->second;

            for (const auto& section : sections) {
                auto linker_it = linker_section_table.find(section->name);

                if (linker_it == linker_section_table.end()) {
                    // Section does not exist, create a new entry
                    create_new_section(section, curr_size);
                    
                } else {
                    // Section exists, update information
                    linker_section_entry* existing_sec = linker_it->second;
                    addr_t base_addr = existing_sec->base_addr + existing_sec->section_size;
                    existing_sec->subsections_map[file] = base_addr;
                    
                    // Append data from the currently analyzed section
                    existing_sec->data.insert(existing_sec->data.end(), section->data.begin(), section->data.end());
                    existing_sec->section_size += section->section_size;
                    // Update the current size
                    curr_size += section->section_size;

                    // Move all sections in front of the current that are not absolute
                    shift_sections_after(section->name, section->section_size);
                }
            }
        }
    }
}


// Function to adjust non-absolute sections and their subsections
void linker::shift_sections_after(const std::string& section_name, size_t sizeToMove) {
    bool startMoving = false;

    for (auto* sec : output_sections) {
        if (sec->name == section_name) {
            startMoving = true;
        } else if (startMoving && !sec->is_placed) {
            // Shift the base address of the section
            sec->base_addr += sizeToMove;

            // Shift the addresses in the subsections map
            for (auto& subsection : sec->subsections_map) {
                subsection.second += sizeToMove; // Adjust each subsection's address
            }
        }
    }
}

void linker::check_for_overlaps() {
    // Iterate through the sorted sections
    auto it1 = placed_sections_map.begin();
    while (it1 != placed_sections_map.end()) {
        addr_t start1 = it1->first;
        linker_section_entry* sec1 = it1->second;
        addr_t end1 = start1 + sec1->section_size;

        // Compare with other sections
        auto it2 = std::next(it1);
        while (it2 != placed_sections_map.end()) {
            addr_t start2 = it2->first;
            linker_section_entry* sec2 = it2->second;
            addr_t end2 = start2 + sec2->section_size;

            // Check for overlap
            if (start1 < end2 && start2 < end1) {
                std::cerr << "ERROR: Section " << sec1->name << " overlaps with " << sec2->name << std::endl;
                std::cerr << "ERROR: Section " << sec1->name << " from " << std::hex << start1
                          << " to " << end1 << " overlaps with " << sec2->name
                          << " from " << start2 << " to " << end2 << std::endl;
                exit(-1);
            }

            ++it2;
        }

        ++it1;
    }
}



void linker::map_sections(){
    if (is_hex){
        rearrange_hex_sections();
    } else {
        rearrange_relocatable_sections();
    }
}


void linker::export_global_symbols() {
    // Iterate through each file in input_files
    for (const std::string& file : input_files) {
        // Retrieve the symbol table for the current file
        auto file_sym_it = file_sym_table_map.find(file);
        if (file_sym_it == file_sym_table_map.end()) {
            continue; // Skip if no symbol table for the file
        }

        const auto& temp_table = file_sym_it->second;

        // Iterate through the symbols in the symbol table
        for (const auto& symbol_pair : temp_table) {
            const std::string& symbol_name = symbol_pair.first;
            symbol_entry* symbol = symbol_pair.second;

            if (!symbol) {
                std::cerr << "Symbol pointer is null in file " << file << ".\n";
                exit(-1);
            }

            // Check if the symbol is either global or an extern symbol that is defined
            if (symbol->binding == STB_GLOBAL || (symbol->binding == STB_EXTERN && symbol->is_defined)) {
                // Check if the symbol already exists in the linker symbol table
                auto linker_it = linker_symbol_table_map.find(symbol_name);
                if (linker_it != linker_symbol_table_map.end()) {
                    std::cerr << "Multiple global definitions of symbol " << symbol_name << " in file " << file << ".\n";
                    exit(-1); // Exit with error code
                }

                auto section_it = linker_section_table.find(symbol->section_name);
                if (section_it == linker_section_table.end()) {
                    std::cerr << "Symbol " << symbol->name << " defined in unknown section: " << symbol->section_name << " in file " << file << ".\n";
                    exit(-1); // Exit with error code
                }

                linker_section_entry* sctn = section_it->second;
                if (!sctn) {
                    std::cerr << "Section pointer is null for section: " << symbol->section_name << " in file " << file << ".\n";
                    exit(-1); // Exit with error code
                }

                int new_offs = sctn->subsections_map[file] + symbol->offset; 
                

                // Create a new symbol entry with global binding
                symbol_entry* new_symbol = new symbol_entry(
                    symbol->name,
                    new_offs,
                    symbol->size,
                    symbol->symbol_type,
                    STB_GLOBAL, // Always set to global binding
                    symbol->is_defined,
                    sctn->idx,
                    symbol->section_name
                );

                // Add the new symbol to the linker symbol table map and vector
                linker_symbol_table_map[symbol_name] = new_symbol;
                linker_symbol_table.push_back(new_symbol);
            } else if (symbol->binding == STB_EXTERN && !symbol->is_defined) {
                // Handle extern symbols that are not defined
                extern_symbols[symbol_name] = symbol;
            }
        }
    }
}

void linker::finalize_symbol_table() {
    // Reset the ID counter for symbol_entry
    symbol_entry::resetID();

    // Step 1: Iterate through placed_sections_map only if there are in-place directives
    if (!placed_sections_map.empty()) {
        for (const auto& section_pair : placed_sections_map) {
            addr_t section_address = section_pair.first;
            linker_section_entry* section_entry = section_pair.second;
            std::string section_name = section_entry->name;

            if (linker_symbol_table_map.find(section_name) == linker_symbol_table_map.end()) {
                symbol_entry* new_symbol = new symbol_entry(
                    section_name,
                    section_address,
                    section_entry->section_size,
                    STT_SECTION,
                    STB_LOCAL,
                    true
                );

                linker_symbol_table_map[section_name] = new_symbol;
                linker_symbol_table.push_back(new_symbol);
            }
        }
    }

    // Step 2: Iterate through output_sections
    for (linker_section_entry* section_entry : output_sections) {
        std::string section_name = section_entry->name;

        if (linker_symbol_table_map.find(section_name) == linker_symbol_table_map.end()) {
            symbol_entry* new_symbol = new symbol_entry(
                section_name,
                section_entry->base_addr,
                section_entry->section_size,
                STT_SECTION,
                STB_LOCAL,
                true
            );

            linker_symbol_table_map[section_name] = new_symbol;
            linker_symbol_table.push_back(new_symbol);
        }
    }

    // Export global symbols
    export_global_symbols();

    // Check for undefined symbols
    check_for_undefined_symbols();
}


void linker::check_for_undefined_symbols() {
    // Iterate through all undefined external symbols in extern_symbols
    for (const auto& symbol_pair : extern_symbols) {
        const std::string& symbol_name = symbol_pair.first;
        symbol_entry* symbol = symbol_pair.second;

        // Check if the symbol is present in the linker symbol table
        auto linker_it = linker_symbol_table_map.find(symbol_name);
        if (linker_it == linker_symbol_table_map.end()) {
            // Symbol is not found in linker symbol table
            if (!is_relocatable) {
                // If the file is not relocatable, raise an error for undefined symbol
                std::cerr << "Undefined symbol: " << symbol_name << std::endl;
                exit(-1); // Exit with error code
            } else {
                // If the file is relocatable, add the symbol to the linker symbol table
                symbol_entry* new_symbol = new symbol_entry(*symbol);
                linker_symbol_table_map[symbol_name] = new_symbol;
                linker_symbol_table.push_back(new_symbol);
            }
        }
        // Continue if symbol is found or added successfully
    }
}

addr_t linker::find_base_addr(const std::string& file, const std::string& section_name) {
    auto linker_section_it = linker_section_table.find(section_name);
    
    if (linker_section_it != linker_section_table.end()) {
        const auto& linker_section = linker_section_it->second;
        auto subsection_it = linker_section->subsections_map.find(file);

        if (subsection_it != linker_section->subsections_map.end()) {
            return subsection_it->second;
        }
    }

    return INVALID_ADDR;  // Return the defined constant for invalid addresses
}


int linker::calculate_relocations() {
    // Iterate through each input file
    for (const std::string& file : input_files) {
        // Retrieve the relocation table for the current file
        auto file_reloc_table_it = file_relocs_table_map.find(file);

        if (file_reloc_table_it != file_relocs_table_map.end()) {
            const auto& reloc_table = file_reloc_table_it->second;

            // Iterate through all sections in the relocation table
            for (const auto& section_entry : reloc_table) {
                const std::string& section_name = section_entry.first;
                const std::vector<relocs_entry*>& rel_entries = section_entry.second;

                // Find base address for the section in the given file
                int sctn_idx = get_symbol_index(section_name);
                if (sctn_idx == -1) {
                    std::cerr << "Error: Section index not found for section " << section_name << std::endl;
                    return -1; // Error code indicating missing section index
                }

                addr_t base_addr = find_base_addr(file, section_name);
                if (base_addr == INVALID_ADDR) {
                    std::cerr << "Error: Base address not found for section " << section_name << " in file " << file << std::endl;
                    return -1;
                }

                // Iterate through all relocation entries for the current section
                for (const relocs_entry* reloc : rel_entries) {
                    if (reloc == nullptr) {
                        std::cerr << "Error: Null relocation entry encountered" << std::endl;
                        return -1;
                    }
                    relocs_entry new_output = *reloc;
                    addr_t addend;
                    if (reloc->symbol_idx == -1) {
                        // Symbol is local, proceed with addend adjustment
                        addend = base_addr; 

                    } else {
                        // Symbol is global
                        addend = 0;
                        symbol_entry* old_symbol = find_symbol_by_idx(file_sym_table_map[file], reloc->symbol_idx);
                        if (old_symbol == nullptr) {
                            std::cerr << "Error: Symbol not found for symbol index " << reloc->symbol_idx << std::endl;
                            return -1; // Error code indicating symbol not found
                        }
                        
                        int new_idx =  get_symbol_index(old_symbol->name);
                        if (new_idx == -1) {
                            std::cerr << "Error: New symbol index not found for symbol " << old_symbol->name << std::endl;
                            return -1; // Error code indicating missing section index
                        }
                        new_output.symbol_idx = new_idx;

                    }
                    new_output.addend = reloc->addend + addend;
                    //std::cout << std::hex << "Old offset: " << reloc->offset << ", addend: " << addend << ", new offset: " << new_output.addend << std::dec << std::endl;
                    new_output.offset = reloc->offset + base_addr;
                    new_output.section_idx = sctn_idx;
                    
                    // Add the new relocation entry to the output relocation table for the section
                    linker_relocs_table[section_name].push_back(new relocs_entry(new_output));
                    output_relocs.push_back(new relocs_entry(new_output));
                }
            }
        }
    }
    return 0;
}

int linker::get_symbol_index(const std::string& symbol_name) const {
    // Check if the symbol exists in the map
    auto it = linker_symbol_table_map.find(symbol_name);
    if (it != linker_symbol_table_map.end()) {
        // Retrieve the symbol_entry from the map
        const symbol_entry* sym_entry = it->second;
        // Return the index of the symbol
        return sym_entry->idx;
    }
    return -1; // Symbol is not found
}

symbol_entry* linker::find_symbol_by_idx(
    const std::unordered_map<std::string, symbol_entry*>& symbol_map, 
    int symbol_id) 
{
    for (const auto& entry : symbol_map) {
        if (entry.second != nullptr && entry.second->idx == symbol_id) {
            return entry.second;
        }
    }
    return nullptr;  // Return an empty string if the symbol ID is not found
}

void linker::print_relocations(std::ostream& output) const {
    if (output_relocs.empty()) {
        output << "#.rela   " << std::endl << "No data available." << std::endl;
        return;
    }

    std::string last_name; // Variable to keep track of the last section name

    // Print each relocation entry
    for (const auto& reloc : output_relocs) {
        if (reloc) {
            // Print the section name caption and headers if the section name has changed
            if (reloc->section_name != last_name) {
                output << "#.rela." << reloc->section_name << std::endl;
                output << std::setw(8) << "Offset" << "  " 
                       << std::setw(8) << "Type" << "  " 
                       << std::setw(6) << "Symbol" << "  " 
                       << std::setw(6) << "Addend" << std::endl;
                last_name = reloc->section_name; // Update the last section name
            }
            reloc->print(output);  // Call the print function of each relocation entry
        }
    }
}


int linker::resolve_relocations() {
    if (is_relocatable) {
        std::cerr << "Error: Attempt to apply relocations on a relocatable file." << std::endl;
        return(-1); // Exit the function if the file is relocatable
    }

    for (const auto& reloc_entry : output_relocs) {
        int symbol_idx = reloc_entry->symbol_idx;
        std::string section_name = reloc_entry->section_name;
        uint32_t offset = reloc_entry->offset;
        uint32_t addend = reloc_entry->addend;

        word value_to_write = 0;

        if (symbol_idx == -1) {
            value_to_write = addend;
        } else {
            symbol_entry* symbol = find_symbol_by_idx(linker_symbol_table_map, symbol_idx);
            if (!symbol) {
                std::cerr << "Could not find symbol in linker symbol table: " << symbol_idx << std::dec << std::endl;
                return(-1); // Exit if the symbol is not found
            }
            value_to_write = symbol->offset + addend;
        }

        // Find section with that name
        auto it = linker_section_table.find(section_name);
        if (it == linker_section_table.end()) {
            std::cerr << "Section not found: " << section_name << std::endl;
            return(-1); // Exit if the section is not found
        }
        linker_section_entry* section = it->second;

        if (offset + 4 > section->base_addr + section->section_size) { // Ensure we don't write past the end of the section
            std::cerr << "Offset exceeds section memory content: " << std::hex << offset  << " base addr: " << section->base_addr+section->section_size << " size: " << std::hex << section->section_size << std::dec << std::endl;
            return(-1); // Exit if the offset exceeds the section size
        }

        // Write the value in little-endian format
        for (size_t i = 0; i < 4; ++i) {
            section->data[offset + i - section->base_addr] = static_cast<byte>(value_to_write & 0xFF);
            value_to_write >>= 8; // Shift right by 8 bits to process the next byte
        }
    }
    return 0;
}


// Define the write_txt_file function
int linker::write_txt_file() const {
    // Prepare and open output file
    
    std::string output_file_txt = output_file_name.substr(0, output_file_name.find_last_of('.')) + ".txt";
    // Prepare and open output file
    std::ofstream output_txt(output_file_txt);

    if (!output_txt.is_open()) {
        std::cerr << "Error opening text file: " << output_file_txt << "\n";
        return -1;
    }

    // Write output text file
    print_symbol_table(output_txt);
    print_relocations(output_txt);
    print_linker_sections_data(output_txt);
    output_txt.close();

    return 0; // Return 0 on success
}


// Function to write the binary object file if -relocatable is set
int linker::write_obj_file() {

    if (!is_relocatable) {
        std::cerr << "Can't write obj file with if -hex==true : " << output_file_name << std::endl;
        return -1;
    }
       
    std::ofstream outputFile(output_file_name, std::ios::binary);
    if (outputFile.is_open()) {
        // Serialize symbol table
        size_t numSymbols = linker_symbol_table.size();
        outputFile.write(reinterpret_cast<const char*>(&numSymbols), sizeof(numSymbols));
        for (const auto& symbol : linker_symbol_table) {
            outputFile.write(reinterpret_cast<const char*>(&symbol->idx), sizeof(symbol->idx));

            size_t name_length = symbol->name.size();
            outputFile.write(reinterpret_cast<const char*>(&name_length), sizeof(name_length));
            outputFile.write(symbol->name.data(), name_length);

            outputFile.write(reinterpret_cast<const char*>(&symbol->offset), sizeof(symbol->offset));
            outputFile.write(reinterpret_cast<const char*>(&symbol->size), sizeof(symbol->size));
            outputFile.write(reinterpret_cast<const char*>(&symbol->symbol_type), sizeof(symbol->symbol_type));
            outputFile.write(reinterpret_cast<const char*>(&symbol->binding), sizeof(symbol->binding));
            outputFile.write(reinterpret_cast<const char*>(&symbol->is_defined), sizeof(symbol->is_defined));
            outputFile.write(reinterpret_cast<const char*>(&symbol->section_idx), sizeof(symbol->section_idx));
            
            size_t sctn_name_length = symbol->section_name.size();
            outputFile.write(reinterpret_cast<const char*>(&sctn_name_length), sizeof(sctn_name_length));
            outputFile.write(symbol->section_name.data(), sctn_name_length);

            size_t num_references = symbol->forward_references.size();
            outputFile.write(reinterpret_cast<const char*>(&num_references), sizeof(num_references));
            for (const auto& ref : symbol->forward_references) {
                outputFile.write(reinterpret_cast<const char*>(&ref->patch_offset), sizeof(ref->patch_offset));
                outputFile.write(reinterpret_cast<const char*>(&ref->section_idx), sizeof(ref->section_idx));
            }
        }

        // Serialize section table
        size_t numSections = output_sections.size();
        outputFile.write(reinterpret_cast<const char*>(&numSections), sizeof(numSections));
        for (const auto& section : output_sections) {
            outputFile.write(reinterpret_cast<const char*>(&section->idx), sizeof(section->idx));

            size_t name_length = section->name.size();
            outputFile.write(reinterpret_cast<const char*>(&name_length), sizeof(name_length));
            outputFile.write(section->name.data(), name_length);

            outputFile.write(reinterpret_cast<const char*>(&section->base_addr), sizeof(section->base_addr));
            outputFile.write(reinterpret_cast<const char*>(&section->section_size), sizeof(section->section_size));

            size_t data_size = section->data.size();
            outputFile.write(reinterpret_cast<const char*>(&data_size), sizeof(data_size));
            outputFile.write(reinterpret_cast<const char*>(section->data.data()), data_size);
        }

        // Serialize relocations table
        size_t numRelocations = output_relocs.size();
        outputFile.write(reinterpret_cast<const char*>(&numRelocations), sizeof(numRelocations));
        for (const auto& reloc : output_relocs) {
            size_t section_name_length = reloc->section_name.size();
            outputFile.write(reinterpret_cast<const char*>(&section_name_length), sizeof(section_name_length));
            outputFile.write(reloc->section_name.data(), section_name_length);

            outputFile.write(reinterpret_cast<const char*>(&reloc->section_idx), sizeof(reloc->section_idx));
            outputFile.write(reinterpret_cast<const char*>(&reloc->symbol_idx), sizeof(reloc->symbol_idx));
            outputFile.write(reinterpret_cast<const char*>(&reloc->type), sizeof(reloc->type));
            outputFile.write(reinterpret_cast<const char*>(&reloc->offset), sizeof(reloc->offset));
            outputFile.write(reinterpret_cast<const char*>(&reloc->addend), sizeof(reloc->addend));
            outputFile.write(reinterpret_cast<const char*>(&reloc->is_resolved), sizeof(reloc->is_resolved));
        }

        outputFile.close();
        return 0;
    } else {
        std::cerr << "Failed to open file for writing." << std::endl;
        return -1;
    }
}


// Define the write_hex_file function
int linker::write_hex_file() const {
    if (is_relocatable) {
        std::cerr << "Can't write hex file." << std::endl;
        return -1;
    } else {
        std::ofstream output_hex(output_file_name);

        if (!output_hex.is_open()) {
            std::cerr << "Error opening hex file: " << output_file_name << "\n";
            return -1;
        }

        // Setup formatting for hex output
        std::ios_base::fmtflags original_flags = output_hex.flags();
        output_hex << std::setfill('0') << std::uppercase << std::right << std::hex;

        size_t current_address = 0;
        const size_t bytes_per_row = 8;
        size_t bytes_on_current_line = 0;

        for (const auto& section_ptr : output_sections) {
            if (!section_ptr) {
                output_hex << "Warning: Null section entry found in output_sections." << std::endl;
                continue;
            }

            const auto& section = *section_ptr;

            // Handle address discontinuity by starting a new line if needed
            if (section.base_addr != current_address) {
                if (bytes_on_current_line > 0) {
                    output_hex << std::endl;  // Finish the current line
                }
                current_address = section.base_addr;
                bytes_on_current_line = 0;
            }

            // Print section data
            for (size_t i = 0; i < section.data.size(); ++i) {
                if (bytes_on_current_line == 0) {
                    output_hex << std::setw(4) << current_address << ": ";
                }

                output_hex << " " << std::setw(2) << static_cast<int>(section.data[i]);
                ++bytes_on_current_line;
                ++current_address;

                if (bytes_on_current_line >= bytes_per_row) {
                    output_hex << std::endl;
                    bytes_on_current_line = 0;
                }
            }
        }

        // Finalize any incomplete line
        if (bytes_on_current_line > 0) {
            output_hex << std::endl;
        }

        // Reset the output stream to original formatting
        output_hex.flags(original_flags);
        output_hex.close();
    }

    return 0; // Return 0 on success
}

// Generate output based on the is_hex flag, text + obj/hex
int linker::generate_output() {
    int result = write_txt_file(); // Always write the text file

    if (result != 0) {
        // Handle error if writing the text file failed
        std::cerr << "Error: Failed to write text output file. Error code: " << result << std::endl;
        return result;
    }

    if (is_hex) {
        result = write_hex_file();
        if (result != 0) {
            // Handle error if writing the hex file failed
            std::cerr << "Error: Failed to write hex output file. Error code: " << result << std::endl;
        }
    } else {
        result = write_obj_file();
        if (result != 0) {
            // Handle error if writing the object file failed
            std::cerr << "Error: Failed to write object output file. Error code: " << result << std::endl;
        }
    }

    return result;
}

int linker::link_all() {
    // Step 1: Map all the sections to their appropriate locations in memory.
    map_sections();

    // Step 2: Finalize the symbol table by resolving all symbol references and assigning addresses.
    finalize_symbol_table();

    // Step 3: Calculate the relocation offsets needed for symbols and sections.
    if (calculate_relocations() == -1) {
        std::cerr << "Error: Failed to calculate relocations." << std::endl;
        return -1;
    }

    // Step 4: Apply all relocations to adjust addresses based on the calculated offsets.
    if (resolve_relocations() == -1) {
        std::cerr << "Error: Failed to resolve relocations." << std::endl;
        return -1;
    }

    // All steps completed successfully
    return 0;
}
