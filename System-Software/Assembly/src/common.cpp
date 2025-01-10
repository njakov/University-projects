#include "../inc/common.hpp"
#include <iomanip>
#include <regex>

void print_byte_vector(const std::vector<byte>& data, std::ostream& output, size_t start, size_t bytes_per_row) {
    if (data.empty()) {
        output << "    No data available." << std::endl;
        return;
    }
    std::ios_base::fmtflags original_flags = output.flags();

    output << std::setfill('0') << std::uppercase << std::right << std::hex;
    //size_t start = 0;
    for (size_t i = 0; i < data.size(); ++i) {
        if (i % bytes_per_row == 0) {
            if (i != 0) {
                output << std::endl;
            }
            output << std::setw(4) << start << ": ";
            start += bytes_per_row;
        }
        output << " " << std::setw(2) << static_cast<int>(data[i]);
    }
    output << std::endl;
    
    // Reset stream to default formatting (decimal)
    output.flags(original_flags);
    
}

bool exceeds_12(word value) {
    return value > 0xFFF; // 4095 in decimal
}


// Function to write a string to a binary file
void write_string(std::ofstream& output, const std::string& str) {
    size_t length = str.size();
    output.write(reinterpret_cast<const char*>(&length), sizeof(length));  // Write the length of the string
    output.write(str.c_str(), length);  // Write the string characters
}