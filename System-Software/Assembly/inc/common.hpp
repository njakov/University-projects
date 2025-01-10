#ifndef COMMON_HPP
#define COMMON_HPP

#include <stdint.h>
//#include <cstring>  // For std::strlen, std::strdup
#include <string>
#include <regex>
#include <vector>
#include <iostream>
#include <fstream>


/* Standard ELF types. */
/* Type for an 8-bit quantity. */
typedef uint8_t byte;

/* Type for a 16-bit quantity. */
typedef uint16_t half;

/* Types for signed and unsigned 32-bit quantities. */
typedef uint32_t uint;  // Define uint as a 32-bit unsigned integer
typedef uint32_t word;  // Define word as a 32-bit unsigned integer
typedef int32_t  sword; // Define sword as a 32-bit signed integer

/* Type for addresses (32-bit unsigned integer). */
typedef uint32_t addr_t;

/* Type for section indices (16-bit unsigned integer). */
typedef uint16_t section_t;

/* Type for instructions (32-bit unsigned integer). */
typedef uint32_t instr_t;

// Mask definitions for registers and displacement
constexpr byte REG_MASK = 0xFU;    // 4 bits for register
constexpr byte BYTE_MASK = 0xFFU; // Mask for 8-bit values
constexpr word DISP_MASK = 0xFFFU; // 12 bits for displacement

// Function to print a vector of bytes to any output stream
void print_byte_vector(const std::vector<byte>& data, std::ostream& output, size_t start = 0, size_t bytes_per_row = 8);

// Function to check if a value requires more than 12 bits
bool exceeds_12(word value);

void write_string(std::ofstream& output, const std::string& str);

#endif // COMMON_HPP
