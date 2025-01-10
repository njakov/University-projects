#include <iostream>       // For input/output stream operations.
#include <vector>         // For using std::vector.
#include <string>         // For using std::string.
#include <map>            // For using std::map.
#include <regex>          // For regular expressions.
#include "../inc/emulator.hpp" // For the emulator class definition.

using namespace std;     // To simplify standard library names.


int main(int argc, char* argv[]) {
    // Obtain singleton instance of emulator
    emulator* emu = emulator::get_instance();

    // Process command-line arguments
    if (emu->process_cmd_line_args(argc, argv) != 0) {
        std::cerr << "Error processing command-line arguments." << std::endl;
        return -1;  // Exit if there was an error in processing command-line arguments
    }

    // Map memory
    if (emu->map_memory() != 0) {
        std::cerr << "Error mapping memory." << std::endl;
        return -1; // Exit if there was an error mapping memory
    }

    // Populate memory from hex
    if (emu->hex_to_mem() != 0) {
        std::cerr << "Error populating memory from hex file." << std::endl;
        return -1; // Exit if there was an error populating memory
    }
    
    // Emulate
    if (emu->emulate() != 0) {
        std::cerr << "Error during emulation." << std::endl;
        return -1; // Exit if there was an error during emulation
    }
    
    return 0; // Successful execution
}
