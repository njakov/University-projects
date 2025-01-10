#include "../inc/instruction.hpp"
#include "../inc/assembler.hpp"
#include <bitset>

extern assembler *asm_instance;


// Assembly instruction functions
void mk_halt() {
    //std::cout << "Function: mk_halt" << std::endl;
    // Append the encoded instruction to the current section
    asm_instance->append_instruction(HALT_OC, 0, 0, 0, 0);

}

void mk_int() {
    //Software interrupt
    //std::cout << "Function: mk_int" << std::endl;
    asm_instance->append_instruction(INT_OC, 0, 0, 0, 0);
}


void mk_iret() {
    //pop pc; pop status;
    //std::cout << "Function: mk_iret" << std::endl;
    asm_instance->append_instruction(0x96, REG_STATUS, REG_SP, 0, 4); // status = mem[sp+4]; 
    asm_instance->append_instruction(0x93, REG_PC, REG_SP, 0, 8); // pc = mem[sp]; sp=sp+8;
}

void mk_call(operand op) {
    // push pc; pc <= operand;
    // Print function information for debugging
    //std::cout << "Function: mk_call" << std::endl;
    //op.print(std::cout);
    section_entry* curr_section = asm_instance->get_curr_section();
    // Handle operand modes
    if (op.mode == IMM_LIT) {
        // Check if literal does not exceed 12 bits
        if (!exceeds_12(op.literal)) {
            // If the literal does not exceed 12 bits, encode directly
            asm_instance->append_instruction(0x20, 0, 0, 0, op.literal);
        } else {
            // If the literal exceeds 12 bits, use extended encoding
            curr_section->add_literal_to_pool(op.literal, curr_section->lc);
            // Push pc; pc <= mem32[gpr[A] + gpr[B] + D]
            asm_instance->append_instruction(0x21, REG_PC, 0, 0, 0);
            //need to calculate dispacement to pool
            

        }
    } else if (op.mode == IMM_SYM) {
        // Handle immediate symbol
        // Push pc; pc <= mem32[gpr[A] + gpr[B] + D]
        curr_section->add_symbol_to_pool(op.symbol, curr_section->lc);
        asm_instance->append_instruction(0x21, REG_PC, 0, 0, 0);
        
       
    } else {
        // Unexpected operand mode, print error message to stderr
        std::cerr << "Unexpected operand mode: " << op.mode << std::endl;
    }
}

void mk_jmp(operand op) {
    //razlaze se
    //std::cout << "Function: mk_jmp" << std::endl;
    //op.print(std::cout);
    section_entry* curr_section = asm_instance->get_curr_section();
    // Handle operand modes
    if (op.mode == IMM_LIT) {
        // Check if literal does not exceed 12 bits
        if (!exceeds_12(op.literal)) {
            // If the literal does not exceed 12 bits, encode directly
            asm_instance->append_instruction(0x30, 0, 0, 0, op.literal);
        } else {
            // If the literal exceeds 12 bits, use pool of literals
            // pc<=mem32[gpr[A]+D];
            curr_section->add_literal_to_pool(op.literal, curr_section->lc);
            asm_instance->append_instruction(0x38, REG_PC, 0, 0, 0);
            // 32-BIT LITERAL CONST
            //curr_section->add_literal_to_pool(op.literal, curr_section->lc);
        }
    } else if (op.mode == IMM_SYM) {
        // Handle immediate symbol
        curr_section->add_symbol_to_pool(op.symbol, curr_section->lc);
        // pc<=mem32[gpr[A]+D];
        asm_instance->append_instruction(0x38, REG_PC, 0, 0, 0);
        // Encode the symbol as a 32-bit value
        
        
    } else {
        // Unexpected operand mode, print error message to stderr
        std::cerr << "Unexpected operand mode: " << op.mode << std::endl;
    }
}

void mk_branch(byte cond, byte reg_b, byte reg_c, operand op) {
    //razlaze se
    /*std::cout << "Function: mk_branch, Mode: " << static_cast<int>(cond)
              << ", Reg B: " << static_cast<int>(reg_b)
              << ", Reg C: " << static_cast<int>(reg_c) << std::endl;*/
    // Handle operand modes
    section_entry* curr_section = asm_instance->get_curr_section();
    if (op.mode == IMM_LIT) {
        // Check if literal does not exceed 12 bits
        if (!exceeds_12(op.literal)) {
            // If the literal does not exceed 12 bits, encode directly
            asm_instance->append_instruction(cond & ~0x10, 0, reg_b, reg_c, op.literal);
        } else {
            // If the literal exceeds 12 bits, use extended encoding
            // if (gpr[B] == gpr[C]) pc<=gpr[A]+D;
            curr_section->add_literal_to_pool(op.literal, curr_section->lc);
            asm_instance->append_instruction(cond, REG_PC, reg_b, reg_c, 0);
            
        }
    } else if (op.mode == IMM_SYM) {
        // Handle immediate symbol
        curr_section->add_symbol_to_pool(op.symbol, curr_section->lc);
        asm_instance->append_instruction(cond, REG_PC, reg_b, reg_c, 0);
        
        // pc <= gpr[A] + D
        
    } else {
        // Unexpected operand mode, print error message to stderr
        std::cerr << "Unexpected operand mode: " << op.mode << std::endl;
    }
}

void mk_push(byte reg) {
    //sp <= sp - 4; mem32[sp] <= gpr;
    //std::cout << "Function: mk_push, Reg: " << static_cast<int>(reg) << std::endl;
    // gpr[A]<=gpr[A]+D; mem32[gpr[A]]<=gpr[C];
    asm_instance->append_instruction(PUSH_OC, REG_SP, 0, reg, -4);
}

void mk_pop(byte reg) {
    //gpr <= mem32[sp]; sp <= sp + 4;
    //std::cout << "Function: mk_pop, Reg: " << static_cast<int>(reg) << std::endl;
    //gpr[A]<=mem32[gpr[B]]; gpr[B]<=gpr[B]+D;
    asm_instance->append_instruction(POP_OC, reg, REG_SP, 0, 4);
    //Can't use this op code if reg is csreg
}

void mk_xchng(byte reg_s, byte reg_d) {
    /*std::cout << "Function: mk_xchng, Reg A: " << static_cast<int>(reg_s)
              << ", Reg B: " << static_cast<int>(reg_d) << std::endl;
              */
    asm_instance->append_instruction(XCHNG_OC, 0, reg_d, reg_s, 0);
    }

void mk_alu_op(byte ocmod, byte reg_s, byte reg_d) {
    /*std::cout << "Function: mk_arithmetic_op, OC Mod: " << static_cast<int>(ocmod)
              << ", Reg source: " << static_cast<int>(reg_s)
              << ", Reg destination: " << static_cast<int>(reg_d) << std::endl;*/
    asm_instance->append_instruction(ocmod, reg_d, reg_d, reg_s, 0);
}

void mk_ld(operand op, byte reg) {
    // Load instruction: reg <= memory or immediate value
    //std::cout << "Function: mk_ld, Reg: " << static_cast<int>(reg) << std::endl;
    //op.print(std::cout);
    section_entry* curr_section = asm_instance->get_curr_section();

    // Handle operand modes
    switch (op.mode) {
        case IMM_LIT: {
            //std::cout << "Mode: IMM_LIT" << std::endl; // ld $<literal>, %gpr
            if (!exceeds_12(op.literal)) {
                // If the literal does not exceed 12 bits, encode directly
                asm_instance->append_instruction(0x91, reg, REG_ZERO, 0, op.literal); // gpr[A]<=gpr[B]+D;
               
            } else {
                curr_section->add_literal_to_pool(op.literal, curr_section->lc); 
                asm_instance->append_instruction(LD_OC, reg, REG_PC, REG_ZERO, 0); // gpr[A]<=mem32[gpr[B]+gpr[C]+D]; 
            }
            break;
        }
        case IMM_SYM: {
            //std::cout << "Mode: IMM_SYM" << std::endl; //ld $<simbol>, %gpr
            curr_section->add_symbol_to_pool(op.symbol, curr_section->lc);
            asm_instance->append_instruction(LD_OC, reg, REG_PC, REG_ZERO, 0); // gpr[A]<=mem32[gpr[B]+gpr[C]+D];
            break;
        }
        case MEM_LIT: {
            //std::cout << "Mode: MEM_LIT" << std::endl; //ld <literal>, %gpr
            //vrednost iz memorije na adresi <literal>
            if (!exceeds_12(op.literal)) {
                // If the literal does not exceed 12 bits, encode directly
                asm_instance->append_instruction(LD_OC, reg, REG_ZERO, REG_ZERO, op.literal); // gpr[A]<=mem32[gpr[B]+gpr[C]+D];

            } else {
                // If the literal exceeds 12 bits, use pool of literals
                curr_section->add_literal_to_pool(op.literal, curr_section->lc);
                asm_instance->append_instruction(LD_OC, reg, REG_PC, REG_ZERO, 0);  //gpr[reg]<=mem32[gpr[PC]+0+D]; u reg upisi literal
                asm_instance->append_instruction(LD_OC, reg, reg, REG_ZERO, REG_ZERO); // gpr[reg]<=mem32[gpr[reg]+0+0];
            }
            break;
        }
        case MEM_SYM: {
            //std::cout << "Mode: MEM_SYM" << std::endl; //ld <simbol>, %gpr
            curr_section->add_symbol_to_pool(op.symbol, curr_section->lc);
            asm_instance->append_instruction(LD_OC, reg, REG_PC, REG_ZERO, 0); //reg <= mem32[PC+D]
            asm_instance->append_instruction(LD_OC, reg, reg, REG_ZERO, REG_ZERO); //reg <= mem32[reg]
            break;
        }
        case REG_DIR: {
            //std::cout << "Mode: REG_DIR" << std::endl; // ld %gpr, %gpr
            asm_instance->append_instruction(AND_OC, reg, op.reg, op.reg, 0); //: gpr[A]<=gpr[B] & gpr[C]; reg<= reg2 & reg2
            break;
        }
        case REG_IND: {
            //std::cout << "Mode: REG_IND" << std::endl; // ld [%<reg>], reg
            asm_instance->append_instruction(LD_OC, reg, op.reg, REG_ZERO, 0); // gpr[A]<=mem32[gpr[B]+gpr[C]+D];
            break;
        }
        case REG_IND_OFFSL: {
            //std::cout << "Mode: REG_IND_OFFSL" << std::endl; // ld [%<reg> + <literal>], reg
            if (exceeds_12(op.literal)) {
                std::cerr << "Error: Literal value exceeds 12 bits" << std::endl;
                exit(-1);
            }
            asm_instance->append_instruction(LD_OC, reg, op.reg, REG_ZERO, op.literal);
            break;
        }
        case REG_IND_OFFSS: {
            std::cout << "Mode: REG_IND_OFFSS UNIMPLEMENTED" << std::endl; // ld [%<reg> + <simbol>], reg
            break;
        }
        default:    {
            std::cerr << "Unexpected operand mode: " << op.mode << std::endl;
            break;
        }
    }
}


void mk_st(byte reg, operand op) {
    //st %gpr, operand          operand <= gpr;
    //std::cout << "Function: mk_st, Reg: " << static_cast<int>(reg) << std::endl; 
    //op.print(std::cout);
    section_entry* curr_section = asm_instance->get_curr_section();

    // Handle operand modes
    switch (op.mode) {
        case IMM_LIT: {
            std::cerr << "Error: ST instruction with immediate literal." << std::endl;
            exit(-1);
            break;
        }
        case IMM_SYM: {
            std::cerr << "Error: ST instruction with immediate symbol." << std::endl;
            exit(-1);
            break;
        }
        case MEM_LIT: {
            //std::cout << "Mode: MEM_LIT" << std::endl; // st %gpr, <literal>
            if (!exceeds_12(op.literal)) {
                // If the literal does not exceed 12 bits, encode directly
                asm_instance->append_instruction(0x80, REG_ZERO, REG_ZERO, reg, op.literal); //mem32[gpr[A]+gpr[B]+D]<=gpr[C];
            }
            else {
                // If the literal exceeds 12 bits, use pool of literals 
                curr_section->add_literal_to_pool(op.literal, curr_section->lc);
                asm_instance->append_instruction(ST_OC, REG_PC, REG_ZERO, reg, 0); // mem32[mem32[gpr[A]+gpr[B]+D]]<=gpr[C]
            }
            break;
        }
        case MEM_SYM: {
            //std::cout << "Mode: MEM_SYM" << std::endl; // st %gpr, <symbol> 
            //vrednost iz memorije na adresi <simbol>
            curr_section->add_symbol_to_pool(op.symbol, curr_section->lc); 
            asm_instance->append_instruction(ST_OC, REG_PC, REG_ZERO, reg, 0); // mem32[mem32[gpr[A]+gpr[B]+D]]<=gpr[C]
            break;
        }
        case REG_DIR: {
            //std::cout << "Mode: REG_DIR" << std::endl; // st %gpr, %gpr (operand)
            asm_instance->append_instruction(AND_OC, op.reg, reg, reg, 0); //gpr[A]<=gpr[B] & gpr[C];
            break;
        }
        case REG_IND: {
            //std::cout << "Mode: REG_IND" << std::endl; // st %gpr, [%<reg>] 
            //vrednost iz memorije na adresi <reg>
            asm_instance->append_instruction(0x80, op.reg, REG_ZERO, reg, 0); //mem32[gpr[A]+gpr[B]+D]<=gpr[C];
            break;
        }
        case REG_IND_OFFSL: {
            //std::cout << "Mode: REG_IND_OFFSL" << std::endl; // st %gpr, [%<reg> + <literal>]
            if (exceeds_12(op.literal)) {
                std::cerr << "Error: Literal value exceeds 12 bits" << std::endl;
                exit(-1);
            }
            asm_instance->append_instruction(0x80, op.reg, REG_ZERO, reg, op.literal); // mem32[gpr[A]+gpr[B]+D]<=gpr[C];
            break;
        }
        case REG_IND_OFFSS: {
            std::cout << "Mode: REG_IND_OFFSS UNIMPLEMENTED" << std::endl; // st %gpr, [%<reg> + <symbol>]
            break;
        }
        default: {
            std::cerr << "Unexpected operand mode: " << op.mode << std::endl;
            break;
        }
    }
}


void mk_csrrd(byte csreg, byte reg) {
    //csrrd %csr, %gpr <==> gpr <= csr
    /*std::cout << "Function: mk_csrrd, C Reg: " << static_cast<int>(csreg)
              << ", Reg: " << static_cast<int>(reg) << std::endl;*/
    asm_instance->append_instruction(CSRRD_OC, reg, csreg, 0, 0);
    //gpr[A]<=csr[B]; 
}

void mk_csrwr(byte reg, byte csreg) {
    //csrwr %gpr, %csr <==> csr <= gpr;
    /*std::cout << "Function: mk_csrwr, Reg: " << static_cast<int>(reg)
              << ", CSR Reg: " << static_cast<int>(csreg) << std::endl;*/
    asm_instance->append_instruction(CSRWR_OC, csreg, reg, 0, 0);
    //csr[A]<=gpr[B];
}

