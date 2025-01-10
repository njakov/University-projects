#ifndef LINKER_HPP
#define LINKER_HPP

#include <string>
#include <vector>
#include <map>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <regex>
#include <algorithm>
#include "structs.hpp"

// Forward declaration of structures used
struct flink_entry;
struct symbol_entry;
struct relocs_entry;

// Function to print a vector of bytes to any output stream
extern void print_byte_vector(const std::vector<byte>& data, std::ostream& output, size_t start, size_t bytes_per_row);

constexpr size_t MAX_TOTAL_SIZE = static_cast<size_t>(1) << 32; // 2^32 bytes
constexpr addr_t INVALID_ADDR = std::numeric_limits<addr_t>::max();


struct asm_section_entry {
    // Section identification and size information
    int idx;                  // Section index (unique identifier)
    std::string name;         // Name of the section
    std::string file_name;
    addr_t base_addr;         // Base address of the section
    //addr_t lc;                // Location counter, tracks the current location within the section
    size_t section_size;      // Total size of the section
    //addr_t pool_start_addr;   // Address where the pool starts
    //word order_idx;           // Order index for pool entries
    //size_t pool_size;         // Size of the pool
    
    // Data
    std::vector<byte> data;   // Holds the machine code and pool data
    
    
    // Default constructor
    asm_section_entry() = default;

    // Parameterized constructor
    asm_section_entry(int idx, const std::string& name, const std::string& file, addr_t base_addr,
                         size_t section_size, const std::vector<byte>& data);

    // Print details of the section entry
    void print_info(std::ostream& output) const;

    // Function to print only the data of this section without address
    void print_data(std::ostream& output) const;
};


struct linker_section_entry {
    // Section identification and size information
    static int next_idx;       // Static index to assign unique IDs to sections
    int idx;                   // Section index (unique identifier)
    std::string name;          // Name of the section
    addr_t base_addr;          // Base address of the section
    size_t section_size;       // Total size of the section
    bool is_placed;            // Indicates if the section's base address is fixed (absolute)

    // Data
    std::vector<byte> data;    // Holds the machine code and pool data

    //Includes sections from files
    std::unordered_map<std::string, addr_t> subsections_map;

    // Relocation Table
    std::vector<relocs_entry*> relocation_table;  // Relocation entries associated with this section

    // Parameterized constructor
    linker_section_entry(const std::string& name, addr_t base_addr, size_t section_size, bool is_placed)
        : idx(next_idx++), name(name), base_addr(base_addr), section_size(section_size),
          is_placed(is_placed), data(), relocation_table() {}
};

class linker {
public:
    // Static method to get the singleton instance
    static linker* get_instance();

    // Prevent copying and assignment
    linker(const linker&) = delete;
    linker& operator=(const linker&) = delete;

    int process_cmd_line_args(int argc, char* argv[]);

    void print_summary(); //prints configuration from command line

    // Method to process all input files
    int process_input_files();

    int read_obj_file(const std::string& inputFileName);

    void map_sections();
    void rearrange_hex_sections();
    void rearrange_relocatable_sections();
    void create_new_section(const asm_section_entry* section, addr_t start_addr);
    void place_section(linker_section_entry* new_sec);
    

    //void print_relocations(const std::string& filename) const;
    void print_symbol_table(std::ostream& output) const;
    void print_linker_sections_data(std::ostream& output) const;
    void check_for_overlaps();
    void shift_sections_after(const std::string& section_name ,size_t sizeToMove);

    void export_global_symbols();
    void finalize_symbol_table();
    void check_for_undefined_symbols();
    addr_t find_base_addr(const std::string& file, const std::string& section_name);
    int calculate_relocations();
    int resolve_relocations();

    int get_symbol_index(const std::string& symbol_name) const;
    symbol_entry* find_symbol_by_idx(const std::unordered_map<std::string, symbol_entry*>& symbol_map, 
    int symbol_id);

    void print_relocations(std::ostream& output) const;

    int write_txt_file() const;
    int write_obj_file();
    int write_hex_file() const;
    int generate_output();

    int link_all();
    //void merge_relocation_tables();

private:
    // Private constructor for singleton
    linker();

    // Static pointer for singleton instance
    static linker* instance_ptr;

    addr_t lc;                   // Location counter, tracks the current location within the section
    size_t curr_size;         // Total size of placed sections
    addr_t memory_start;

    std::unordered_map<std::string, std::unordered_map<std::string, symbol_entry*>> file_sym_table_map;
    std::unordered_map<std::string, std::vector<asm_section_entry*>> file_sctn_table_map;
    std::unordered_map<std::string, std::unordered_map<std::string, std::vector<relocs_entry*>>> file_relocs_table_map;


    // Global tables
    std::vector<symbol_entry*> linker_symbol_table; 
    std::unordered_map<std::string, symbol_entry*> linker_symbol_table_map;
    std::unordered_map<std::string, symbol_entry*> extern_symbols;

    std::unordered_map<std::string, linker_section_entry*> linker_section_table; //map of linker sections

    // Output sections in linker order
    std::vector<linker_section_entry*> output_sections;
    std::vector<relocs_entry*> output_relocs;
    // Sections placed in ascending order of their addresses - used for checking overlaps
    std::map<addr_t, linker_section_entry*> placed_sections_map;

    // Define the map
    std::unordered_map<std::string, std::vector<relocs_entry*>> linker_relocs_table;

    // Unresolved relocations
    std::vector<relocs_entry*> unresolved_relocations;

    //Helper struct for mapping sections
    std::vector<asm_section_entry*> unfinished_sections;

    // Command line configurations
    std::string output_file_name;                        // Output file name
    std::vector<std::string> input_files;                // List of input file names
    std::unordered_map<std::string, addr_t> to_be_placed;       // Section names and their addresses
    bool is_hex;                                         // Flag for hexadecimal output
    bool is_relocatable;                                 // Flag for relocatable output
    
};

#endif // LINKER_HPP
