#include <iostream>
#include <fstream>
#include <string>
#include <cstring>
#include "../inc/assembler.hpp"

// Function declaration from parser
extern int parse_file(const char* file_to_parse);

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cerr << "Usage: assembler [-o <output_file_name>] <input_file_name>\n";
        return -1;
    }

    const char* file_to_parse = nullptr;
    std::string output_file_name;

    // Parse command-line arguments
    for (int i = 1; i < argc; ++i) {
        if (strcmp(argv[i], "-o") == 0 && i + 1 < argc) {
            output_file_name = argv[++i];
        } else if (argv[i][0] != '-') {
            file_to_parse = argv[i];  // Store file name as const char*
        } else {
            std::cerr << "Unknown option: " << argv[i] << "\n";
            return -1;
        }
    }

    // Validate and process input file name
    if (file_to_parse == nullptr) {
        std::cerr << "Error: No input file provided.\n";
        return -1;
    }

    // Set default output file name if not specified
    if (output_file_name.empty()) {
        std::string input_file_name = file_to_parse;
        // Replace .s with .o for the output file name
        if (input_file_name.size() > 2 && input_file_name.substr(input_file_name.size() - 2) == ".s") {
            output_file_name = input_file_name.substr(0, input_file_name.size() - 2) + ".o";
        } else {
            std::cerr << "Error: Input file name should end with '.s'.\n";
            return -1;
        }
    } else {
        // Ensure that the output file name ends with .o
        if (output_file_name.size() < 2 || output_file_name.substr(output_file_name.size() - 2) != ".o") {
            std::cerr << "Error: Output file name should end with '.o'.\n";
            std::cerr << "Usage: assembler [-o <output_file_name>] <input_file_name>\n";
            return -1;
        }
    }

    // Call parser with file_to_parse and check if parsing was successful
    int parse_result = parse_file(file_to_parse);
    if (parse_result != 0) {
        std::cerr << "Parsing failed with error code: " << parse_result << "\n";
        return parse_result;
    }

    // Obtain singleton instance of assembler
    assembler* asm_instance = assembler::get_instance();
    asm_instance->resolve_relocations();
        
    //asm_instance->print_symbol_table(std::cout);
    //asm_instance->print_relocations(std::cout);
    //asm_instance->print_section_table(std::cout);

    // Prepare and open output file
    std::string output_file_txt = output_file_name.substr(0, output_file_name.size() - 2) + ".txt";

    if (asm_instance->write_obj_file(output_file_name) != 0) {
        return -1; // Error already reported by write_obj_file
    }

    if (asm_instance->write_txt_file(output_file_txt) != 0) {
        return -1; // Error already reported by write_txt_file
    }

    std::cout << "Files created successfully: " << output_file_txt << " and " << output_file_name << std::endl;
    return 0;
}


