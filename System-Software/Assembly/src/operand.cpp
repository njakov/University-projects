#include "../inc/operand.hpp"

// Constructor with optional symbol initialization
operand::operand(addr_mode m, int l, int r, char* s)
    : mode(m), literal(static_cast<word>(l)), reg(static_cast<byte>(r)), symbol(s) {
}

void operand::free_sym(){
	if(mode == IMM_SYM || mode == MEM_SYM || mode == REG_IND_OFFSS){
		free(symbol);
        symbol = nullptr; // Prevent dangling pointer
	}
}

void operand::print(std::ostream& output) const {
    output << "Mode: ";
    switch (mode) {
        case IMM_LIT: output << "IMM_LIT"; break;
        case IMM_SYM: output << "IMM_SYM"; break;
        case MEM_LIT: output << "MEM_LIT"; break;
        case MEM_SYM: output << "MEM_SYM"; break;
        case REG_DIR: output << "REG_DIR"; break;
        case REG_IND: output << "REG_IND"; break;
        case REG_IND_OFFSL: output << "REG_IND_OFFSL"; break;
        case REG_IND_OFFSS: output << "REG_IND_OFFSS"; break;
        default: output << "UNKNOWN"; break;
    }
    output << "\nLiteral: " << literal;
    output << "\nRegister: " << static_cast<int>(reg);
    if (symbol) {
        output << "\nSymbol: " << symbol;
    } else {
        output << "\nSymbol: NULL";
    }
    output << std::endl;
}