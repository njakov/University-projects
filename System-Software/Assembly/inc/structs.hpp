#ifndef STRUCTS_H
#define STRUCTS_H

#include <string>
#include <iostream>
#include <fstream>
#include <vector>
#include <unordered_map>
#include "common.hpp"

/*  Legal values for ST_BINDING (symbol binding).    */
#define STB_LOCAL 0  /* Local symbol */
#define STB_GLOBAL 1 /* Global symbol */
#define STB_EXTERN 2 /* Extern symbol */

/*  Legal values for ST_TYPE (symbol type).  */
#define STT_NOTYPE 0  /* Symbol type is unspecified */
#define STT_SECTION 3 /* Symbol associated with a section */

/*  Special section indices.    */
#define SHN_UNDEF 0     /* Undefined section */
#define SHN_ABS                0xfff1                /* Associated symbol is absolute */
/* Legal values for section type.   */
#define SHT_NULL 0     /* Section unused */
#define SHT_PROGBITS 1 /* Program data */
#define SHT_SYMTAB 2   /* Symbol table */
#define SHT_RELA 3     /* Relocation entries with addends */

/* Relocation type  */
#define R_386_32 0   /* Relocation type for 32-bit absolute addresses. */

using size_t = std::size_t; // Ensure compatibility
constexpr size_t MAX_SECTION_SIZE = static_cast<size_t>(1) << 12; // 2^12 bytes (4096 bytes)


// Structure to hold information about forward references
struct flink_entry
{
    int patch_offset; // Offset in the section where the patch is needed
    int section_idx;
    flink_entry(int offs, int idx);
    flink_entry();  // Default constructor
    void print(std::ostream& output) const;
};

// Structure to represent an entry in the symbol table
struct symbol_entry {
    static int ID;  // Static ID counter for unique indexing

    int idx;                  // Unique index for the symbol
    std::string name;         // Name of the symbol
    int offset;               // Offset of the symbol in the section
    int size;                 // Size of the symbol
    int symbol_type;          // Type of the symbol (e.g., object, function)
    int binding;              // Binding type (local, global, extern)
    bool is_defined;          // Whether the symbol is defined
    int section_idx;          // Index of the section where the symbol is located
    std::string section_name;

    std::vector<flink_entry*> forward_references;  // Forward references of symbol

    // Constructors
    symbol_entry();  // Default constructor
    symbol_entry(const std::string &nameValue, int val, int sz, int tp, int bnd, bool defined, int sec_idx, const std::string &sctn_name);
    symbol_entry(const std::string &nameValue, int val, int sz, int tp, int bnd, bool defined);

    // Copy constructor
    symbol_entry(const symbol_entry& other);
    
    // Function to print forward references of symbol
    void print_flink(std::ostream &output) const;
    // Function to print symbol entry details
    void print(std::ostream& output) const;
    static void resetID();

};

// Define the structure relocs_entry
struct relocs_entry
{   std::string section_name;
    
    int section_idx;  // Index of the section where relocation occurs
    int symbol_idx;   // Index of the symbol being relocated
    int type;         // Type of relocation (e.g., R_386_32)
    size_t offset;       // Offset in the section where relocation is applied
    size_t addend;       // Addend to be added to the relocation
    bool is_resolved; // Flag indicating if the relocation is resolved

    //std::string symbol_name;
    // Constructors
    relocs_entry();
    relocs_entry(std::string sec_name, int sec_idx, int sym_idx, int tp, size_t offs, size_t add, bool resolved=true);
    relocs_entry(std::string sec_name, int sec_idx, int tp, size_t offs, size_t add, bool resolved=true);

    // Print function
    void print() const;
    void print(std::ostream& output) const;
};

enum class pool_entry_type {
    LITERAL,
    SYMBOL
};

struct pool_entry {
    int insertion_order;        // Order of insertion in the pool
    pool_entry_type type;          // Type of the entry (literal or symbol)
    union {
        unsigned int lit_value;           // Literal value (if type is LITERAL)
        std::string sym_name;   // Symbol name (if type is SYMBOL)
    };
    std::vector<addr_t> ref_addresses; // Addresses that reference this literal or symbol

    pool_entry(int order, unsigned int value);
    pool_entry(int order, const std::string& name);
    ~pool_entry();
    bool operator<(const pool_entry& other) const;
    void print(std::ostream& output) const;
};


struct section_entry {
    // Section identification and size information
    int idx;                     // Section index (unique identifier)
    std::string name;            // Name of the section
    addr_t base_addr;            // Base address of the section
    addr_t lc;                   // Location counter, tracks the current location within the section
    size_t section_size;         // Total size of the section
    addr_t pool_start_addr;      // Address where the pool starts
    word order_idx = 0;          // Order index for pool entries
    size_t pool_size = 0;        // Size of the pool


    // Data and tables
    std::vector<byte> data;                    // Holds the machine code and pool data
    std::unordered_map<std::string, std::vector<addr_t>> symbol_relocations;

    // Pool management
    std::unordered_map<std::string, pool_entry*> pool_map;  // Map of symbols and literals to pool entries
    std::vector<pool_entry*> pool_entries; // List of pool entries in order of insertion

    // Constructors
    section_entry();                                 // Default constructor
    section_entry(int idx, const std::string &name); // Constructor with index and name

    // Destructor
    ~section_entry();

    // Public methods
    void add_symbol_to_pool(const std::string& symbol, addr_t instr_addr); // Add a symbol to the pool
    void add_literal_to_pool(unsigned int value, addr_t instr_addr);      // Add a literal to the pool
    void append_data(int value, size_t size = 4);                         // Append data to the section
    
    void concatenate_pool();                                             // Concatenate literals and symbols into section data
    void resolve_displacements();                                        // Resolve displacements for symbols and literals
    void finalize_section();
    
    void print_info(std::ostream& output) const;                    // Print the details of the section entry
    void print_data(std::ostream& output) const;
    void print_pool_entries(std::ostream& output) const;
};



#endif // STRUCTS_H
