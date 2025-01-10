#ifndef AS_DIR_HPP
#define AS_DIR_HPP

#include <string>

// Function declarations for assembly directives
void mk_global(const std::string& symbol);
void mk_extern(const std::string& symbol);
void mk_section(const std::string& section_name);
void mk_label(const std::string& label_name);
void mk_word(const std::string& symbol);
void mk_word(int value);
void mk_skip(int count);
void mk_ascii(const std::string& ascii);
void mk_equ(const std::string& symbol, int value);
void mk_end();

#endif // AS_DIR_HPP