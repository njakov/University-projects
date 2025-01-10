#include <iostream>       // For input/output stream operations.
#include <vector>         // For using std::vector.
#include <string>         // For using std::string.
#include <map>            // For using std::map.
#include <regex>          // For regular expressions.
#include "../inc/linker.hpp" // For the linker class definition.

using namespace std;     // To simplify standard library names.
int main(int argc, char* argv[]) {

    
    // Obtain singleton instance of linker
    linker* ln_instance = linker::get_instance();

    // Process command-line arguments
    int ret = ln_instance->process_cmd_line_args(argc, argv);
    if (ret != 0) {
        return ret;  // Exit if there was an error in processing command-line arguments
    }
    //ln_instance->print_summary();

    // Process all input files
    ret = ln_instance->process_input_files();
    if (ret != 0) {
        return ret;  // Exit if there was an error in processing input files

    }

    ret = ln_instance->link_all();
    if (ret != 0) {
        return ret;  // Exit if there was an error in processing input files
    }
    
    //ln_instance->print_symbol_table(std::cout);
    //ln_instance->print_linker_sections_data(std::cout);
    //ln_instance->print_relocations(std::cout);

    // Prepare and open output file
    ln_instance->generate_output();

    return 0;
}
