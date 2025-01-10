#ifndef ASSEMBLER_H
#define ASSEMBLER_H

#include "structs.hpp"
#include "directive.hpp"
#include "instruction.hpp"
#include <string>
#include <map>
#include <unordered_map>
#include <vector>


class assembler {
public:
    // Get the singleton instance
    static assembler* get_instance();

    // Prevent copying and assignment
    assembler(const assembler&) = delete;
    assembler& operator=(const assembler&) = delete;

    // Current section management
    void set_curr_section(section_entry* section);
    section_entry* get_curr_section() const;

    // Symbol management
    void add_symbol(symbol_entry* symbol);
    symbol_entry* get_symbol(const std::string& name) const;

    // Section table management
    void add_section(section_entry* section);
    //section_entry* get_section(int index) const; unimplemented since not used

    // Code management, append instruction or value to current section's data
    void append_instruction(word op_code, word reg_a, word reg_b, word reg_c, word disp); // Append to current section

    // Symbol searching and relocation resolution
    symbol_entry* search_symbol(const std::string& name) const;
    void resolve_relocations();

    std::string byte_to_binary_string(byte b) const;

    // Print the data of the current section
    void print_curr_section(std::ostream& output) const;

    // Print symbol table (to file)
    void print_symbol_table(std::ostream& output) const;

    // Print all sections, choose: info, data, pool entries or everything
    void print_section_table(std::ostream& output) const;

    //Print resolved relocations after assembling
    void print_relocations(std::ostream& output) const;

    // Simple print of relocations
    void print_relocations() const;

     // Method to print all sections data
    void print_sections_data(std::ostream& output) const;

    // Function to write output to a text file
    int write_txt_file(const std::string& output_file_txt) const;
    // Function to write object file
    int write_obj_file(const std::string& outputFileName);

private:
    // Private constructor for singleton
    assembler();

    // Singleton instance
    static assembler* instance_ptr;

    // Data structures
    std::unordered_map<std::string, symbol_entry*> symbol_map;
    std::vector<symbol_entry*> symbol_table; 
    std::vector<section_entry*> sections_table; // Vector to store all sections
    std::vector<relocs_entry*> relocations; // Vector to store all relocations

    section_entry* curr_section;
};

#endif // ASSEMBLER_H
