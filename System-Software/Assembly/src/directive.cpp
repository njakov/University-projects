#include "../inc/assembler.hpp"
#include "../inc/directive.hpp"
#include <iostream> // For std::cout

assembler *asm_instance = assembler::get_instance();

// Function definitions for assembly directives
void mk_global(const std::string &symbol_name)
{
    //std::cout << "Directive: mk_global, Symbol: " << symbol_name << std::endl;

    symbol_entry *symbol = asm_instance->search_symbol(symbol_name);
    if (symbol)
    {
        symbol->binding = STB_GLOBAL;
    }
    else
    {
        symbol_entry *new_symbol = new symbol_entry(symbol_name, 0, 0, STT_NOTYPE, STB_GLOBAL, false, SHN_UNDEF, "");
        asm_instance->add_symbol(new_symbol);
    }
}

void mk_extern(const std::string &symbol_name)
{
    //std::cout << "Directive: mk_extern, Symbol: " << symbol_name << std::endl;
    symbol_entry *symbol = asm_instance->search_symbol(symbol_name);
    if (symbol)
    {
        symbol->binding = STB_EXTERN;
    }
    else
    {
        symbol_entry *new_symbol = new symbol_entry(symbol_name, 0, 0, STT_NOTYPE, STB_EXTERN, false, SHN_UNDEF, "");
        asm_instance->add_symbol(new_symbol);
    }
}

void mk_section(const std::string &section_name)
{
    //std::cout << "Directive: mk_section, Section Name: " << section_name << std::endl;

    symbol_entry *symbol = asm_instance->search_symbol(section_name);
    if (symbol)
    {
        std::cout << "ERROR: Section" << section_name << " is already defined in the Symbol Table";
        exit(-1);
    }

    section_entry *curr_section = asm_instance->get_curr_section();

    if (curr_section != nullptr)
    {
        curr_section->finalize_section();
    }

    symbol_entry *new_symbol = new symbol_entry(section_name, 0, 0, STT_SECTION, STB_LOCAL, true);
    asm_instance->add_symbol(new_symbol);

    section_entry *new_section = new section_entry(new_symbol->idx, section_name);
    asm_instance->set_curr_section(new_section);
    asm_instance->add_section(new_section);
    
}


void mk_label(const std::string &label_name)
{
    //std::cout << "Directive: mk_label, Label Name: " << label_name << std::endl;

    // Get the current section
    section_entry *current_section = asm_instance->get_curr_section();
    if (!current_section)
    {
        std::cerr << "Error: Can't define label outside of a section." << std::endl;
        exit(-1);
    }

    int location_counter = current_section->lc;
    symbol_entry *symbol = asm_instance->search_symbol(label_name);

    if (symbol)
    {
        // If symbol is already defined, report an error
        if (symbol->is_defined)
        {
            std::cerr << "Error: Label " << label_name << " is already defined." << std::endl;
            exit(-1);
        }
        else {
            // Define the symbol in the current section if it is undefined
            if (symbol->section_idx == SHN_UNDEF)
            {
                symbol->section_idx = current_section->idx;
                symbol->is_defined = true;
                symbol->offset = location_counter;
                symbol->section_idx = current_section->idx;
                symbol->section_name = current_section->name;
            }
        }
    }
    else
    {
        // Add a new symbol to the symbol table
        symbol_entry *new_symbol = new symbol_entry(
            label_name, 
            location_counter, 
            0, 
            STT_NOTYPE, 
            STB_LOCAL,
            true,
            current_section->idx, 
            current_section->name
        );
        asm_instance->add_symbol(new_symbol);
    }
}

void mk_word(const std::string &symbol_name)
{
    // Log the directive and symbol being processed
    //std::cout << "Directive: mk_word, Word: " << symbol_name << std::endl;

    // Retrieve the current section and location counter (lc)
    section_entry *curr_section = asm_instance->get_curr_section();
    if (!curr_section)
    {
        std::cerr << "Error: Can't define label outside of a section." << std::endl;
        exit(-1);
    }
    int lc = curr_section->lc;

    // Search for the symbol in the symbol table
    symbol_entry *symbol = asm_instance->search_symbol(symbol_name);

    if (!symbol)
    {
        // If the symbol is not found, create a new undefined symbol entry
        symbol = new symbol_entry(symbol_name, 0, 0, STT_NOTYPE, STB_LOCAL, false, SHN_UNDEF, "");
        asm_instance->add_symbol(symbol);
    }

    // Add the location counter to the section's symbol relocations map
    curr_section->symbol_relocations[symbol_name].push_back(lc);

    // Regardless of the symbol's definition status, write a placeholder (0) into the section's data
    curr_section->append_data(0);

    // Check if the section size exceeds the allowed limit
    if (curr_section->lc > MAX_SECTION_SIZE)
    {
        std::cerr << "Error: Section size exceeds 4096 bytes." << std::endl;
        std::exit(-1); // Exit with error code -1
    }
}

void mk_word(int value)
{
    //std::cout << "Directive: mk_word, Value: " << value << std::endl;

    section_entry *curr_section = asm_instance->get_curr_section();
    curr_section->append_data(value); // Update the location counter in the current section inside append_data
    
    // Check if the location counter exceeds section size limit
    if (curr_section->lc > MAX_SECTION_SIZE)
    {
        std::cerr << "Error: Section size exceeds " << MAX_SECTION_SIZE << " bytes." << std::endl;
        exit(EXIT_FAILURE); // Use EXIT_FAILURE from <cstdlib> for consistency
    }

    // Optionally, print the current state for debugging purposes
    //std::cout << "Current Location Counter: " << curr_section->lc << std::endl;
}



void mk_skip(int count)
{

    // Get the current section
    section_entry *curr_section = asm_instance->get_curr_section();
    if (curr_section == nullptr)
    {
        std::cerr << "Error: No current section set." << std::endl;
        exit(-1);
    }

    // Check for invalid count values
    if (count < 0)
    {
        std::cerr << "Error: Skip count must be non-negative." << std::endl;
        exit(-1);
    }

    //std::cout << "Directive: mk_skip, Count: " << count << std::endl;

    // Add zeroes to the current section's data
    curr_section->data.insert(curr_section->data.end(), count, static_cast<byte>(0));

    // Update the location counter in the current section
    curr_section->lc += count;

    // Check if the location counter exceeds section size limit
    if (curr_section->lc > MAX_SECTION_SIZE)
    {
        std::cerr << "Error: Section size exceeds " << MAX_SECTION_SIZE << " bytes." << std::endl;
        exit(EXIT_FAILURE); // Use EXIT_FAILURE from <cstdlib> for consistency
    }

    // Optionally, print the current state for debugging purposes
    //std::cout << "Current Location Counter: " << curr_section->lc << std::endl;
}

void mk_ascii(const std::string &ascii) {
    section_entry *curr_section = asm_instance->get_curr_section();
    // Ensure the current section is valid
    if (curr_section == nullptr) {
        std::cerr << "Error: No active section to add ASCII data." << std::endl;
        return;
    }

    // Allocate space in the section's data for each character in the string
    for (char ch : ascii) {
        // Convert character to its ASCII value and add it to the section's data
        curr_section->data.push_back(static_cast<byte>(ch));
    }

    // Update the location counter to reflect the number of bytes added
    curr_section->lc += ascii.length();

    // Check if the section size exceeds the allowed limit
    if (curr_section->lc > MAX_SECTION_SIZE) {
        std::cerr << "Error: Section size exceeds 4096 bytes." << std::endl;
        std::exit(-1); // Exit with error code -1
    }
}


void mk_equ(const std::string &symbol, int value)
{
    std::cout << "Directive: mk_equ, Symbol: " << symbol << ", Value: " << value << std::endl;
}

void mk_end()
{
    //std::cout << "Directive: mk_end" << std::endl;
    section_entry *curr_section = asm_instance->get_curr_section();
    curr_section->finalize_section();
    
}
