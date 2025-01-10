#ifndef INSTR_HPP
#define INSTR_HPP
#include "operand.hpp"
#include <iostream>  // For std::cout


// Register indexes
constexpr byte REG_ZERO = 0x00;                  // Zero register
constexpr byte REG_SP = 0x0E;                    // Stack Pointer
constexpr byte REG_PC = 0x0F;                    // Program Counter
constexpr byte REG_STATUS = REG_ZERO;            // Status Register (alias for zero)
constexpr byte REG_HANDLER = 0x01;               // Exception Handler
constexpr byte REG_CAUSE = 0x02;                 // Cause of Exception

// Commonly used pseudo-instructions codes
constexpr byte HALT_OC = 0x00;
constexpr byte INT_OC = 0x10;
constexpr byte CALL_OC = 0x21;
constexpr byte JMP_OC = 0x30;

constexpr byte BEQ_OC = 0x39;
constexpr byte BNE_OC = 0x3A;
constexpr byte BGT_OC = 0x3B;

constexpr byte XCHNG_OC = 0x40;
constexpr byte ADD_OC = 0x50;
constexpr byte SUB_OC = 0x51;
constexpr byte MUL_OC = 0x52;
constexpr byte DIV_OC = 0x53;
constexpr byte NOT_OC = 0x60;
constexpr byte AND_OC = 0x61;
constexpr byte OR_OC = 0x62;
constexpr byte XOR_OC = 0x63;
constexpr byte SHL_OC = 0x70;
constexpr byte SHR_OC = 0x71;
constexpr byte PUSH_OC = 0x81;
constexpr byte ST_OC = 0x82;
constexpr byte CSRRD_OC = 0x90;
constexpr byte LD_OC = 0x92; //  gpr[A]<=mem32[gpr[B]+gpr[C]+D];
constexpr byte POP_OC = 0x93;
constexpr byte CSRWR_OC = 0x94;


// Shift definitions
constexpr byte FIRST_BYTE = 24;
constexpr byte SECOND_BYTE_UPPER = 20;
constexpr byte SECOND_BYTE_LOWER = 16;
constexpr byte THIRD_BYTE_UPPER = 12;
constexpr byte DISP = 0; // No shift needed for displacement

// Function declarations for assembly instructions
void mk_halt();
void mk_ret();
void mk_int();
void mk_iret();

void mk_call(operand op);
void mk_jmp(operand op);
void mk_branch(byte cond, byte reg_b, byte reg_c, operand op);

void mk_push(byte reg);
void mk_pop(byte reg);
void mk_xchng(byte reg_s, byte reg_d);

void mk_alu_op(byte op_code, byte reg_b, byte reg_c = 0);

void mk_ld(operand op, byte reg);
void mk_st(byte reg, operand op);

void mk_csrrd(byte csreg, byte reg);
void mk_csrwr(byte reg, byte csreg);


#endif // INSTR_HPP
