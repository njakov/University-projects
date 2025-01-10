#ifndef EMULATOR_HPP
#define EMULATOR_HPP

#include <string>
#include <vector>
#include <cstddef>    // for size_t
#include <sys/mman.h> // for mmap and munmap
#include <cerrno>     // for errno
#include <fcntl.h>    // for file control options
#include <unistd.h>   // for close, read, write
#include <termios.h>  // for terminal I/O
#include "common.hpp"
#include "instruction.hpp"


#define NUM_REGISTERS 16 // Assuming there are 16 general-purpose registers

constexpr size_t MEMORY_SIZE = static_cast<size_t>(1) << 32; // 2^32 bytes (4 GB)
constexpr addr_t START_ADDRESS = 0x40000000U;
constexpr addr_t TERM_OUT = 0xFFFFFF00U;
constexpr addr_t TERM_IN  = 0xFFFFFF04U;


// Compute the last addressable word in the range
constexpr addr_t MAX_ADDRESS = static_cast<addr_t>(MEMORY_SIZE) - 4;

typedef word reg;

struct cpu_registers {
    reg gpr[NUM_REGISTERS]; // General-purpose registers (r0 to r15)
    reg csr[3];  // Control and status registers (status, handler, cause)
};

enum {
    r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15
};

enum {
    status, handler, cause,
    sp = 14, pc = 15
};

enum exception_cause {
    IllegalInstruction = 1,
    TimerInterrupt,
    TerminalInterrupt,
    SoftwareInterrupt
};

enum status_flags {
    TimerDisable    = 1U,
    TerminalDisable = 2U,
    AllDisable      = 4U
};

enum opcode {
    Halt,       // Halt the execution
    Interrupt,  // Trigger an interrupt
    Call,       // Call a subroutine
    Jmp,        // Jump to a different location
    Xchg,       // Exchange values
    Arit,       // Arithmetic operations
    Logic,      // Logic operations
    Shift,      // Shift operations
    Store,      // Store data
    Load        // Load data
};


class emulator {
public:
    // Singleton pattern - Get the instance of the emulator
    static emulator* get_instance();

    // Prevent copying and assignment
    emulator(const emulator&) = delete;
    emulator& operator=(const emulator&) = delete;

    // Public methods
    int process_cmd_line_args(int argc, char* argv[]);

    int hex_to_mem();
    int map_memory();
    void unmap_memory();
    int dump_memory(addr_t start_address, size_t length);

    void print_cpu_state();
    void print_instr(instr_t instr);
    
    int emulate();
    void illegal_trap();
    
private:
    // Private constructor to prevent direct instantiation
    emulator();
    ~emulator();

    // Private members
    static emulator* instance; // Singleton instance

    cpu_registers registers;   // CPU registers
    char* memory;              // Memory mapped for the emulator

    // Terminal settings
    struct termios original_settings;
    int terminal_flags;

    // Private methods for terminal management
    void set_terminal_mode();
    void restore_terminal_mode();

    std::string hex_file_name; // Name of the hex file to load

    // Access to program counter and stack pointer (using references)
    reg& rpc = registers.gpr[pc];
    reg& rsp = registers.gpr[sp];

    bool halt = false;
    
    //Registers operations
    int init_registers();
    int write_reg(int index, word value);
    int pushGPR(uint src);
    int pushCSR(uint src);

    //Memory operations
    reg read_mem(addr_t addr);
    instr_t read_instr(addr_t pc);
    int write_mem(reg src, addr_t addr);

    // Handle HALT instruction
    void handle_halt(word mode, bool& halt);

    // Handle INTERRUPT instruction
    void handle_interrupt(word mode);

    // Handle CALL instruction
    void handle_call(word mode, word A, word B, word D);

    // Handle JMP and related instructions
    void handle_jump(word mode, word A, word B, word C, word D);

    // Handle XCHNG instruction
    void handle_xchg(word B, word C);

    // Handle arithmetic instructions
    void handle_arithmetic(word mode, word A, word B, word C);

    // Handle logic instructions
    void handle_logic(word mode, word A, word B, word C);

    // Handle shift instructions
    void handle_shift(word mode, word A, word B, word C);

    // Handle STORE instructions
    void handle_store(word mode, word A, word B, word C, word D);

    // Handle LOAD instructions
    void handle_load(word mode, word A, word B, word C, word D);

    // Handle terminal input
    void handle_terminal_input();

    // Handle terminal output
    void handle_terminal_output();
};

#endif // EMULATOR_HPP
