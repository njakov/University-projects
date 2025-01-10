#ifndef OPERAND_HPP
#define OPERAND_HPP

#include "common.hpp"
#include <iostream>  // For std::cout

enum addr_mode {
    IMM_LIT,        // $<literal> - immediate value <literal>
    IMM_SYM,        // $<symbol> - immediate value <symbol>
    MEM_LIT,        // <literal> - value from memory at address <literal>
    MEM_SYM,        // <symbol> - value from memory at address <symbol>
    REG_DIR,        // %<reg> - value in register <reg>
    REG_IND,        // [%<reg>] - value from memory at address <reg>
    REG_IND_OFFSL,  // [%<reg> + <literal>] - value from memory at address <reg> + <literal>
    REG_IND_OFFSS,  // [%<reg> + <symbol>] - value from memory at address <reg> + <symbol>
    UNDEF
};

struct operand {
public:
    addr_mode mode;
    word literal;   // Literal value
    byte reg;       // Register value
    char* symbol;   // Symbol name

    operand() = default;
    // Public constructor for initialization with all arguments
    operand(addr_mode m, int l, int r, char* s);

    // Method to print the operand
    void print(std::ostream& output) const;
    void free_sym();
};


#endif // OPERAND_HPP
