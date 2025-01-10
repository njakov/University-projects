#include "../inc/assembler.hpp"
#include <bitset>
#include <iostream>
#include <iomanip>

// Initialize the singleton instance pointer
assembler* assembler::instance_ptr = nullptr;

// Private constructor
assembler::assembler() : curr_section(nullptr) {
    symbol_entry *zero_symbol = new symbol_entry("", 0, 0, STT_NOTYPE, STB_LOCAL, true);
    symbol_table.push_back(zero_symbol);


}

// Get the singleton instance (returns a pointer)
assembler* assembler::get_instance() {
    if (instance_ptr == nullptr) {
        instance_ptr = new assembler();
    }
    return instance_ptr;
}

void assembler::set_curr_section(section_entry* section) {
    curr_section = section;
}

section_entry* assembler::get_curr_section() const {
    return curr_section;
}

void assembler::add_symbol(symbol_entry* symbol) {
    auto result = symbol_map.emplace(symbol->name, symbol);
    if (!result.second) {
        // Handle the case where the symbol already exists
        std::cerr << "Symbol already exists: " << symbol->name << std::endl;
        exit(-1);
    }
    symbol_table.push_back(symbol);
}

symbol_entry* assembler::get_symbol(const std::string& name) const {
    auto it = symbol_map.find(name);
    return (it != symbol_map.end()) ? it->second : nullptr;
}


void assembler::add_section(section_entry* section) {
    if (!section) {
        std::cerr << "Error: Attempt to add a null section.\n";
        return;
    }
    // Add or overwrite the section in the sections table
    sections_table.push_back(section);
    //std::cout << "Added section: " << section->name << " with index " << section->idx << "\n";

}

\
void assembler::append_instruction(word op_code, word reg_a, word reg_b, word reg_c, word disp) {
    if (curr_section == nullptr) {
        std::cerr << "ERROR: No active section to append instruction." << std::endl;
        exit(-1);
    }

    word encoded_inst 
        = ((op_code & BYTE_MASK) << FIRST_BYTE)        // Shift op_code to the highest 8 bits
        | ((reg_a & REG_MASK) << SECOND_BYTE_UPPER)    // Shift reg_a to the next 4 bits
        | ((reg_b & REG_MASK) << SECOND_BYTE_LOWER)    // Shift reg_b to the next 4 bits
        | ((reg_c & REG_MASK) << THIRD_BYTE_UPPER)     // Shift reg_c to the next 4 bits
        | (disp & DISP_MASK);                          // Mask displacement to 12 bits

    //std::cout << "Encoded instruction: " << std::bitset<32>(encoded_inst) << std::endl;  
    // Push the 4 bytes of the instruction in big-endian order
    const byte* bytes = reinterpret_cast<const byte*>(&encoded_inst);
    for (int i = 3; i >= 0; --i) {
        curr_section->data.push_back(bytes[i]);
        curr_section->lc += 1;
        //std::cout << "Pushed byte: " << std::bitset<8>(bytes[i]) << std::endl;  // Note: Display byte (8 bits)
    }
}


symbol_entry* assembler::search_symbol(const std::string& label) const {
    auto it = symbol_map.find(label);
    return (it != symbol_map.end()) ? it->second : nullptr;
}


void assembler::resolve_relocations() {
    // Clear previous relocations
    relocations.clear();

    // Iterate through each section in the sections_table
    for (const auto& section : sections_table){
        // Process relocations for this section
        for (const auto& relocs_pair : section->symbol_relocations) {
            const std::string& symbol_name = relocs_pair.first;
            const std::vector<addr_t>& offsets = relocs_pair.second;

            symbol_entry* symbol = search_symbol(symbol_name);
            if (!symbol) {
                std::cerr << "Error: Symbol \"" << symbol_name << "\" not found in symbol table." << std::endl;
                exit(-1);
            }
            
            if (symbol->binding == STB_LOCAL) {
                if (!symbol->is_defined) {
                    std::cerr << "Error: Local symbol \"" << symbol_name << "\" is not defined." << std::endl;
                    exit(-1);
                }
                if (section->idx == symbol->section_idx) {
                    for (addr_t offset : offsets) {
                        relocs_entry* new_reloc = new relocs_entry(section->name, section->idx, R_386_32, offset, symbol->offset);
                        relocations.push_back(new_reloc);
                    }
                } else {
                    std::cerr << "Error: Local symbol \"" << symbol_name << "\" defined in different section." << std::endl;
                    exit(-1);
                }
            } else if (symbol->binding == STB_GLOBAL || symbol->binding == STB_EXTERN) {
               for (addr_t offset : offsets) {
                relocs_entry* new_reloc = new relocs_entry(section->name, section->idx, symbol->idx, R_386_32, offset, 0);
                //new_reloc->symbol_name = symbol->name;
                relocations.push_back(new_reloc);
                }
                
            }
        }
    }
}



// Function to convert a byte to an 8-bit binary string
std::string assembler::byte_to_binary_string(byte b) const {
    std::bitset<8> bits(b);
    return bits.to_string();
}


// Function to print data of the current section
void assembler::print_curr_section(std::ostream& output) const {
    if (!curr_section) {
        std::cerr << "Error: No current section set.\n";
        return;
    }
    curr_section->print_data(output);
}

void assembler::print_section_table(std::ostream& output) const {
    if (sections_table.empty()) {
        std::cerr << "No sections available to print.\n";
        return;
    }
    for (const auto& section : sections_table) {
        if (section) {
            section->print_info(output);
            //section->print_data(output);
            //section->print_pool_entries(output);
        }
    }
}

void assembler::print_relocations() const {
    std::cout << "Relocations:" << std::endl;
    for (const auto& reloc : relocations) {
        std::cout << "Symbol Index: " << reloc->symbol_idx
                  << ", Section Index: " << reloc->section_idx
                  << ", Type: " << reloc->type
                  << ", Offset: " << reloc->offset
                  << ", Addend: " << reloc->addend
                  << ", Resolved: " << (reloc->is_resolved ? "Yes" : "No")
                  << std::endl;
    }
}

void assembler::print_symbol_table(std::ostream& output) const {
    // Print headers
    output << "#.symtab" << std::endl;
    output << std::setw(4)  << "Num" << "  " 
           << std::setw(8)  << "Value" << "  " 
           << std::setw(4)  << "Size" << "  " 
           << std::setw(5)  << "Type" << "  " 
           << std::setw(4)  << "Bind" << "  " 
           << std::setw(4)  << "Ndx" << "  "
           << std::setw(3)  << "Def" << "  "
           << "Name" << std::endl;  // Column headers

    // Print each symbol entry
    for (const auto& symbol : symbol_table) {
        if (symbol) {
            // Print each symbol entry using the passed output stream
            symbol->print(output);  // Print symbol entry details
        }
    }
}


// Implementation of print_all_sections
void assembler::print_sections_data(std::ostream& output) const {
    // // Check if the sections table has any data
    if (sections_table.empty()) {
        output << "    No data available." << std::endl;
    }   
    for (const auto& section : sections_table) {
        if (section){
            section->print_data(output); // Print data for each section
        }
    }
}

void assembler::print_relocations(std::ostream& output) const {
    if (relocations.empty()) {
        output << "#.rela   " << std::endl << "No data available." << std::endl;
        return;
    }

    std::string last_name; // Variable to keep track of the last section name

    // Print each relocation entry
    for (const auto& reloc : relocations) {
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

// Define the write_txt_file function
int assembler::write_txt_file(const std::string& output_file_txt) const {
    // Prepare and open output file
    std::ofstream output_txt(output_file_txt);

    if (!output_txt.is_open()) {
        std::cerr << "Error opening text file: " << output_file_txt << "\n";
        return -1;
    }

    // Write output text file
    print_symbol_table(output_txt);
    print_relocations(output_txt);
    print_sections_data(output_txt);
    output_txt.close();

    return 0; // Return 0 on success
}

int assembler::write_obj_file(const std::string& outputFileName) {
    std::ofstream outputFile(outputFileName, std::ios::binary);

    if (outputFile.is_open()) {
        // Serialize symbol table
        size_t numSymbols = symbol_table.size();
        outputFile.write(reinterpret_cast<const char*>(&numSymbols), sizeof(numSymbols));
        for (const auto& symbol : symbol_table) {
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
        size_t numSections = sections_table.size();
        outputFile.write(reinterpret_cast<const char*>(&numSections), sizeof(numSections));
        for (const auto& section : sections_table) {
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
        size_t numRelocations = relocations.size();
        outputFile.write(reinterpret_cast<const char*>(&numRelocations), sizeof(numRelocations));
        for (const auto& reloc : relocations) {
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