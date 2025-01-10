#include "../inc/structs.hpp"
#include <utility>
#include <algorithm> // For std::reverse
#include <bitset>
#include <iostream>
#include <fstream>
#include <vector>
#include <unordered_map>
#include <string>
#include <iomanip> // For std::setw and std::setfill
// Initialize the static ID counter for symbol_entry
int symbol_entry::ID = 0;

// Constructors and functions for flink_entry
flink_entry::flink_entry(int patchValue, int idx)
    : patch_offset(patchValue), section_idx(idx) {}

flink_entry::flink_entry() : patch_offset(0), section_idx(0) {}

// Print function to display the contents of flink_entry in a table format
void flink_entry::print(std::ostream& output) const
{
    output << "0x" << std::setw(8)  << std::hex << patch_offset  // Hexadecimal format
            << std::setw(8) << std::dec << section_idx << "\n";  // Decimal format
}

// Default constructor for symbol_entry
symbol_entry::symbol_entry()
    : idx(ID++), name(""), offset(0), size(0), symbol_type(STT_NOTYPE), binding(STB_LOCAL),
       is_defined(false), section_idx(SHN_UNDEF), section_name("") {}

// Parameterized constructor for symbol_entry
symbol_entry::symbol_entry(const std::string &nameValue, int val, int sz, int tp, int bnd,
                           bool def, int secIdx, const std::string &sctn_name)
    : idx(ID++), name(nameValue), offset(val), size(sz), symbol_type(tp), binding(bnd), is_defined(def),
      section_idx(secIdx), section_name(sctn_name) {}

// Constructor for symbol_entry for sections
symbol_entry::symbol_entry(const std::string &nameValue, int val, int sz, int tp, int bnd, bool def)
    : idx(ID++), name(nameValue), offset(val), size(sz), symbol_type(tp), binding(bnd), is_defined(def) {
    if (symbol_type == STT_SECTION) {
        this->section_idx = idx;
        this->section_name = nameValue;
    } else {
        this->section_idx = SHN_UNDEF;
        this->section_name = "";
    }
}
 
// Copy constructor
// Assuming flink_entry doesn't need deep copy
symbol_entry::symbol_entry(const symbol_entry& other)
    : idx(ID++),  // Assign a new unique id
        name(other.name),
        offset(other.offset),
        size(other.size),
        symbol_type(other.symbol_type),
        binding(other.binding),
        is_defined(other.is_defined),
        section_idx(other.section_idx),
        section_name(other.section_name) {}


// Static method to reset the ID counter when creating entries in linker symbol table
void symbol_entry::resetID() {
    ID = 1;
}

void symbol_entry::print_flink(std::ostream &output) const
{
    if (!forward_references.empty())
    {
        output << "  Forward References:\n";
        for (const auto &ref : forward_references)
        {
            output << "    Patch Offset: " << ref->patch_offset
                      << ", Section Index: " << ref->section_idx << '\n';
        }
    }
    else
    {
        output << "  No Forward References.\n";
    }
}

void symbol_entry::print(std::ostream &output) const
{
    // Determine binding flag
    std::string bind_flag;
    switch (binding) {
        case STB_LOCAL:   bind_flag = "LOC";  break;
        case STB_GLOBAL:  bind_flag = "GLOB"; break;
        case STB_EXTERN:  bind_flag = "EXT";  break;
        default:          bind_flag = " ";    break;
    }

    // Determine type flag
    std::string type_flag;
    switch (symbol_type) {
        case STT_NOTYPE:   type_flag = "NOTYP"; break;
        case STT_SECTION:  type_flag = "SCTN";  break;
        default:           type_flag = "NOTYP"; break;
    }

    // Determine section flag
    std::string section_flag;
    switch (section_idx) {
        case SHN_UNDEF:     section_flag = "UND";  break;
        case SHN_ABS:       section_flag = "ABS";  break;
        default:            section_flag = std::to_string(section_idx); break;
    }

    // Print formatted symbol entry
    output << std::setfill(' ') << std::setw(4) << std::right << idx << "  "                            // Right-align index
           << std::setfill('0') << std::setw(8) << std::right << std::hex << offset << std::dec << "  " // Zero-padding for offset
           << std::setfill(' ') << std::setw(4) << std::right << size << "  "                           // Zero-padding for size
           << std::setw(5) << std::left << type_flag << "  "                                            // Left-align type
           << std::setw(4) << std::left << bind_flag << "  "                                            // Left-align binding
           << std::setw(4) << std::right << section_flag << "  "                                        // Right-align section (no zero-padding)
           << std::setw(3) << std::left << (is_defined ? "Y" : "N") << "  "                             // Left-align definition status
           << name << std::right << std::endl;
}

// Constructor for literals
pool_entry::pool_entry(int order, unsigned int value)
    : insertion_order(order), type(pool_entry_type::LITERAL), lit_value(value) {}

// Constructor for symbols
pool_entry::pool_entry(int order, const std::string &name)
    : insertion_order(order), type(pool_entry_type::SYMBOL), sym_name(name) {}

// Destructor
pool_entry::~pool_entry()
{
    if (type == pool_entry_type::SYMBOL)
    {
        // Explicitly call the destructor for std::string
        sym_name.~basic_string();
    }
}

// Comparison operator for sorting by insertion order
bool pool_entry::operator<(const pool_entry &other) const
{
    return insertion_order < other.insertion_order;
}

// Print function for pool_entry
void pool_entry::print(std::ostream& output) const {

    output << std::setw(5) << insertion_order << " | ";
    if (type == pool_entry_type::LITERAL) {
        output << "LITERAL  | "
               << "0x" << std::setw(8) << std::setfill('0') << std::right << std::hex << lit_value << " | ";
    } else {
        output << "SYMBOL   | "
               << std::setw(10) << std::setfill(' ') << std::left << sym_name << " | ";
    }
    output << "References: ";
    if (ref_addresses.empty()) {
        output << "None";
    } else {
        for (size_t i = 0; i < ref_addresses.size(); ++i) {
            if (i > 0) output << ", ";
            output << "0x" << std::setw(8) << std::setfill('0') << std::right << std::hex << ref_addresses[i];
        }
    }
    output << std::setfill(' ') << std::right << std::dec << std::endl;
}


// Default constructor
section_entry::section_entry()
    : idx(0), name(""), base_addr(0), lc(0), section_size(0), pool_start_addr(0),
      order_idx(0), pool_size(0) {}

// Constructor with index and name
section_entry::section_entry(int idx, const std::string &name)
    : idx(idx), name(name), base_addr(0), lc(0), section_size(0), pool_start_addr(0),
      order_idx(0), pool_size(0) {}

// Add a literal to the pool
void section_entry::add_literal_to_pool(unsigned int value, addr_t instr_addr)
{
    std::string key = std::to_string(value);
    if (pool_map.find(key) == pool_map.end())
    {
        pool_entry *entry = new pool_entry(order_idx++, value);
        pool_entries.push_back(entry);
        pool_map[key] = entry;
        pool_size += 4; // Assuming each literal occupies 4 bytes
    }
    pool_map[key]->ref_addresses.push_back(instr_addr);
}

// Add a symbol to the pool
void section_entry::add_symbol_to_pool(const std::string &symbol, addr_t instr_addr)
{
    if (pool_map.find(symbol) == pool_map.end())
    {
        pool_entry *entry = new pool_entry(order_idx++, symbol);
        pool_entries.push_back(entry);
        pool_map[symbol] = entry;
        pool_size += 4; // Assuming each symbol address occupies 4 bytes
    }
    pool_map[symbol]->ref_addresses.push_back(instr_addr);
}

// Finalize the pools and concatenate them into the data vector
void section_entry::concatenate_pool()
{
    pool_start_addr = lc; // Initialize the pool start address
    for (const auto &entry : pool_entries)
    {
        if (entry->type == pool_entry_type::LITERAL)
        {
            append_data(entry->lit_value, 4);
        }
        else
        {
            // Create a relocation entry for the symbol
            symbol_relocations[entry->sym_name].push_back(lc);
            append_data(0, 4); // Placeholder bytes for symbols
        }
    }
}

// Resolve displacements for symbols and literals
void section_entry::resolve_displacements()
{
    for (const auto &entry : pool_entries)
    {
        addr_t pool_entry_address = pool_start_addr + entry->insertion_order * 4;
        for (addr_t ref_addr : entry->ref_addresses)
        {
            uint displacement = pool_entry_address - ref_addr - 4;
            displacement &= DISP_MASK;

            data[ref_addr + 2] |= ((displacement >> 8) & REG_MASK); // Higher byte
            data[ref_addr + 3] = displacement & BYTE_MASK;          // Lower byte
        }
    }
}

// Append a 4-byte integer to the section's data in little-endian format
void section_entry::append_data(int value, size_t size)
{
    for (size_t i = 0; i < size; ++i)
    {
        data.push_back(static_cast<byte>(value & BYTE_MASK));
        //std::cout << "Pushed byte: " << std::bitset<8>(static_cast<byte>(value & BYTE_MASK)) << std::endl; // Note: Display byte (8 bits)
        value >>= 8;
    }
    lc += size;
}

// Finalize the section: append pool and resolve displacements
void section_entry::finalize_section()
{
    // Concatenate literals and symbols into the data vector
    concatenate_pool();
    // Resolve displacements for symbols and literals
    resolve_displacements();
    section_size = data.size();
}

// Print details of the section entry
void section_entry::print_info(std::ostream& output) const {
    std::ios_base::fmtflags original_flags = output.flags();

    output << std::hex << std::uppercase << std::right;

    output << "===============================\n";
    output << "   Section Entry Information\n";
    output << "===============================\n\n";

    output << "Index            : " << std::setw(4) << std::setfill(' ') << std::dec << idx << "\n";
    output << "Name             : " << name << "\n";
    output << "Base Address     : 0x" << std::setw(8) << std::setfill('0') << std::hex << base_addr << "\n";
    output << "Location Counter : 0x" << std::setw(8) << std::setfill('0') << std::hex << lc << "\n";
    output << "Section Size     : " << std::setfill(' ') << std::dec << std::setw(4) << section_size << " bytes\n";
    output << "Pool Start Addr  : 0x" << std::setw(8) << std::setfill('0') << std::hex << pool_start_addr << "\n";
    output << "Order Index      : " << std::setfill(' ') << std::dec << std::setw(4) << order_idx << "\n";
    output << "Pool Size        : " << std::dec << std::setw(4) << pool_size << " bytes\n\n";

    output.flags(original_flags);
}

// Function to print only the data of this section without address
void section_entry::print_data(std::ostream &output) const
{
    // Print the section name as a header
    output << "-------------------------------\n";
    output << "   Data Segment #." << name << "\n";
    output << "-------------------------------\n\n";

    print_byte_vector(data, output, base_addr, 8);
}

// Function to print pool entries with column labels
void section_entry::print_pool_entries(std::ostream &output) const {
    // Print header for pool entries
    output << "-------------------------------\n";
    output << "   Pool Entries\n";
    output << "-------------------------------\n";

    // Check if there are any pool entries
    if (pool_entries.empty()) {
        output << "No pool entries available.\n";
        output << "-------------------------------\n";
        return;
    }

    // Print column labels
    output << std::setfill(' ') << std::setw(5) << "Order" << " | "
           << std::setw(8) << "Type" << " | "
           << std::setw(10) << "Value" << " | "
           << "References\n";
    output << "-------------------------------\n";

    // Print each pool entry
    for (const auto& entry : pool_entries) {
        if (entry) {
            entry->print(output);
        }
    }

    // Print footer for pool entries
    output << "-------------------------------\n";
}

// Destructor
section_entry::~section_entry()
{
    // Clean up pool entries
    for (auto &entry : pool_entries)
    {
        delete entry; // Assuming pool_entry is dynamically allocated
    }

    // Clear the vectors and maps
    pool_entries.clear();
    pool_map.clear();
    symbol_relocations.clear();
}

// Default constructor
relocs_entry::relocs_entry()
    : section_name(""), section_idx(-1), symbol_idx(-1), type(0), offset(0), addend(0), is_resolved(true) {}

// Parameterized constructor with symbol index
relocs_entry::relocs_entry(std::string sec_name, int sec_idx, int sym_idx, int tp, size_t offs, size_t add, bool resolved)
    : section_name(sec_name), section_idx(sec_idx), symbol_idx(sym_idx), type(tp), offset(offs), addend(add), is_resolved(resolved) {}

// Parameterized constructor without symbol index
relocs_entry::relocs_entry(std::string sec_name, int sec_idx, int tp, size_t offs, size_t add, bool resolved)
    : section_name(sec_name), section_idx(sec_idx), symbol_idx(-1), type(tp), offset(offs), addend(add), is_resolved(resolved) {}

// Print function to display the contents of the relocation entry
void relocs_entry::print() const
{
    std::cout << "Relocation Entry:" << std::endl;
    std::cout << "  Symbol Index: " << symbol_idx << std::endl;
    std::cout << "  Section Index: " << section_idx << std::endl;
    std::cout << "  Type: " << type << std::endl;
    std::cout << "  Offset: " << offset << std::endl;
    std::cout << "  Addend: " << addend << std::endl;
    std::cout << "  Resolved: " << (is_resolved ? "Yes" : "No") << std::endl;
}

void relocs_entry::print(std::ostream &output) const
{
    // Determine the relocation type flag
    std::string type_flag;
    switch (type)
    {
    case R_386_32:
        type_flag = "R_386_32";
        break;
    // Add other relocation types as needed
    default:
        type_flag = "UNKNOWN";
    }

    // Print formatted relocation entry
    output << std::setw(8) << std::setfill('0') << std::right << std::hex << offset << std::dec << "  "                        // Offset
           << std::setw(8) << std::setfill(' ') << std::left << type_flag << "  "                                              // Type
           << std::setw(6) << std::setfill(' ') << std::right << (symbol_idx == -1 ? " " : std::to_string(symbol_idx)) << "  " // Symbol index
           << std::setw(6) << std::setfill(' ') << std::right << addend << std::endl;                                          // Addend
}


