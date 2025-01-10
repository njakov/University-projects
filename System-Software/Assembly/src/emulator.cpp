#include "../inc/emulator.hpp"
#include <iostream>
#include <cstring>      // For strerror function
#include <iomanip>

// Initialize the static instance pointer to nullptr
emulator* emulator::instance = nullptr;

// Get the singleton instance of the emulator
emulator* emulator::get_instance() {
    if (instance == nullptr) {
        instance = new emulator();
    }
    return instance;
}

// Private constructor
emulator::emulator() {}


int emulator::map_memory() {
    // Define protection and flags for memory mapping
    int prot = PROT_READ | PROT_WRITE;  // Enable reading and writing
    int flags = MAP_PRIVATE | MAP_ANONYMOUS; // Private mapping, not visible to other processes; not backed by a file
     
    // Map the specified size of memory
    memory = static_cast<char*>(mmap(nullptr, MEMORY_SIZE, prot, flags, -1, 0));

    // Check for mapping errors
    if (memory == MAP_FAILED) {
        std::cerr << "Memory mapping failed: " << strerror(errno) << std::endl;
        return -1;
    }

    return 0;
}

emulator::~emulator() {
    unmap_memory();
}

void emulator::unmap_memory() {
    if (memory && memory != MAP_FAILED) {
        if (munmap(memory, MEMORY_SIZE) != 0) {
            std::cerr << "Memory unmapping failed: " << strerror(errno) << std::endl;
        }
        memory = nullptr;
    }
}

int emulator::process_cmd_line_args(int argc, char* argv[]) {
    // Ensure only one argument is provided (besides program name)
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " <file.hex>\n";
        return -1; // Error: Incorrect number of arguments
    }

    std::string arg = argv[1];

    // Check if the argument ends with .hex
    if (arg.size() > 4 && arg.compare(arg.size() - 4, 4, ".hex") == 0) {
        //std::cout << "Processing file: " << arg << std::endl;
        hex_file_name = arg; // Store the file name in the class field
        return 0;
    } else {
        std::cerr << "Error: The provided argument must have a .hex suffix\n";
        return -1; // Error: Incorrect file format
    }
}


int emulator::hex_to_mem() {
    std::ifstream input_hex(hex_file_name);

    if (!input_hex.is_open()) {
        std::cerr << "Error opening hex file: " << hex_file_name << "\n";
        return -1;
    }

    std::string line;
    while (std::getline(input_hex, line)) {
        if (line.empty()) {
            continue; // Skip empty lines
        }

        std::istringstream line_stream(line);
        size_t address;
        line_stream >> std::hex >> address;

        std::string colon;
        line_stream >> colon; // Read and discard the colon

        // Ensure address is within the memory range
        if (address >= MEMORY_SIZE) {
            std::cerr << "Address out of bounds: " << address << std::endl;
            return -1;
        }

        // Read bytes and write them directly into the memory
        int byte_value;
        while (line_stream >> std::hex >> byte_value) {
            if (address >= MEMORY_SIZE) {
                std::cerr << "Memory write out of bounds at address: " << address << std::endl;
                return -1;
            }
            memory[address++] = static_cast<char>(byte_value);
        }
    }

    input_hex.close();
    return 0; // Return true on success
}

int emulator::dump_memory(addr_t start_address, size_t length) {
    // Check if the requested dump exceeds memory bounds
    if (start_address + length > MEMORY_SIZE) {
        std::cerr << "Dump exceeds memory bounds." << std::endl;
        return -1;
    }

    // Set up the output format for hexadecimal values
    std::cout << std::hex << std::uppercase << std::setfill('0') << std::setw(2);

    for (size_t i = start_address; i < start_address + length; ++i) {
        if ((i - start_address) % 8 == 0 && i != start_address) {
            // Print a newline every 8 bytes for readability
            std::cout << std::endl;
        }

        // Print each byte as two hexadecimal digits
        std::cout << std::setw(2) << static_cast<int>(static_cast<unsigned char>(memory[i])) << " ";
    }
    std::cout << std::dec << std::endl; // Switch back to decimal output
    return 0; // Return 0 on success
}

void emulator::print_cpu_state() {
    std::cout << "Emulated processor state:" << std::endl;

    for (int i = 0; i < 16; ++i) {
        // Print each register's name and value
        std::cout << ((i < 10) ? " " : "") 
                  << "r" << std::dec << i
                  << "=0x" << std::setw(8) << std::setfill('0') << std::hex << registers.gpr[i]
                  << ((i % 4 == 3) ? "\n" : "   ");  // Format for 4 registers per line
    }
    std::cout << std::dec;  // Reset stream to default (decimal) format
}


int emulator::init_registers() {
    // Initialize general-purpose registers (r0 to r14) to 0
    for (int i = 0; i < 15; ++i) {
        registers.gpr[i] = 0;
    }

    // Initialize the program counter (pc) register to 0x40000000
    rpc = START_ADDRESS;

    // Initialize control and status registers (status, handler, cause) to 0
    registers.csr[status] = 0;
    registers.csr[handler] = 0;
    registers.csr[cause] = 0;
    return 0;
}


void emulator::set_terminal_mode() {
    // Save the original terminal settings
    if (tcgetattr(STDIN_FILENO, &original_settings) == -1) {
        perror("tcgetattr failed");
        exit(EXIT_FAILURE);
    }

    // Disable echo and canonical mode (line buffering)
    struct termios new_settings = original_settings;
    new_settings.c_lflag &= ~(ICANON | ECHO);
    if (tcsetattr(STDIN_FILENO, TCSANOW, &new_settings) == -1) {
        perror("tcsetattr failed");
        exit(EXIT_FAILURE);
    }

    // Set stdin to non-blocking mode
    terminal_flags = fcntl(STDIN_FILENO, F_GETFL, 0);
    if (terminal_flags == -1 || fcntl(STDIN_FILENO, F_SETFL, terminal_flags | O_NONBLOCK) == -1) {
        perror("fcntl failed");
        exit(EXIT_FAILURE);
    }

    // Disable stdout buffering
    if (setvbuf(stdout, nullptr, _IONBF, 0) != 0) {
        perror("setvbuf failed");
        exit(EXIT_FAILURE);
    }
}

void emulator::restore_terminal_mode() {
    // Restore the original terminal settings
    if (tcsetattr(STDIN_FILENO, TCSANOW, &original_settings) == -1) {
        perror("tcsetattr failed");
        // Log error; avoid exiting in destructor
    }

    // Restore the original stdin blocking mode
    if (fcntl(STDIN_FILENO, F_SETFL, terminal_flags) == -1) {
        perror("fcntl restore failed");
        // Log error; avoid exiting in destructor
    }

    // Restore output buffering to default
    if (setvbuf(stdout, nullptr, _IOFBF, BUFSIZ) != 0) {
        perror("setvbuf restore failed");
        // Log error; avoid exiting in destructor
    }
}


void emulator::print_instr(instr_t instr){
    std::cout << "\033[1;34m=== Instruction Info ===\033[0m" << std::endl;
    std::cout << "\033[1;32mPC:\033[0m 0x" << std::hex << rpc;
    std::cout << ", \033[1;32mINSTR:\033[0m 0x" << instr << std::endl;
}

// Function to write a value to a general-purpose register
int emulator::write_reg(int index, word value) {
    if (index == 0) {
        std::cerr << "Error: Attempt to write to the zero register." << std::endl;
        return -1; // Return error code
    }
    if (index < 0 || index >= NUM_REGISTERS) {
        std::cerr << "Error: Invalid register index." << std::endl;
        return -1; // Return error code for invalid index
    }
    registers.gpr[index] = value; // Write value to the specified register
    return 0; // Return success code
}

// Reads a 32-bit instruction from memory at the given PC. 
instr_t emulator::read_instr(addr_t pc){
    if (pc > MAX_ADDRESS) return -1;

    unsigned char byte0 = memory[pc];       // First byte
    unsigned char byte1 = memory[pc + 1];   // Second byte
    unsigned char byte2 = memory[pc + 2];   // Third byte
    unsigned char byte3 = memory[pc + 3];   // Fourth byte

    return (reg)(byte3 | (byte2 << 8) | (byte1 << 16) | (byte0 << 24));
}

// Reads a 32-bit value from memory at the specified address in little-endian order.
reg emulator::read_mem(addr_t addr) {
    if (addr > MAX_ADDRESS) return -1;

    unsigned char byte0 = memory[addr];       // First byte
    unsigned char byte1 = memory[addr + 1];   // Second byte
    unsigned char byte2 = memory[addr + 2];   // Third byte
    unsigned char byte3 = memory[addr + 3];   // Fourth byte

    return (reg)(byte0 | (byte1 << 8) | (byte2 << 16) | (byte3 << 24));
}


// Writes a 32-bit value to memory at the specified address in little-endian order.
int emulator::write_mem(reg src, addr_t addr) {
    if (addr > MAX_ADDRESS) {
        std::cerr << "Address exceeds max address" << std::endl;
        return -1;
    }

    // Write each byte of the 32-bit value to memory in little-endian order
    for (int i = 0; i < 4; ++i) {
        memory[addr + i] = static_cast<unsigned char>((src >> (i * 8)) & BYTE_MASK);
    }

    return 0;
}

// Pushes a GPR value onto the stack and updates the stack pointer.
int emulator::pushGPR(uint src){
    rsp -= 4; // Allocate space on stack
    return write_mem(registers.gpr[src], rsp); 

}

// Pushes a CSR value onto the stack and updates the stack pointer.
int emulator::pushCSR(uint src){
    rsp -= 4; // Allocate space on stack
    return write_mem(registers.csr[src], rsp);
}

// Handle illegal instruction trap
void emulator::illegal_trap() {
    std::cout << "Illegal instruction encountered. Handling trap." << std::endl;
    pushCSR(status);          // Save status to stack
    pushGPR(pc);              // Save PC to stack
    
    registers.csr[cause] = exception_cause::IllegalInstruction; // Set cause
    rpc = registers.csr[handler]; // Set RPC to handler address
}

int emulator::emulate(){

    init_registers();
    //set_terminal_mode();

    while (!halt) {

        addr_t instr = read_instr((addr_t)rpc);
        //print_instr(instr);
        //print_cpu_state();
        rpc += 4;

        word opcode = instr >> 28;  // 4 bits for Opcode
        word mode   = (instr >> 24 ) & REG_MASK;    // 4 bits for Mode
        word A      = (instr >> 20) & REG_MASK; // 4 bits for A
        word B      = (instr >> 16) & REG_MASK;       // 4 bits for B
        word C      = (instr >> 12) & REG_MASK;    // 4 bits for C
        word D = instr & DISP_MASK;  // 12 bits for D

        // Sign-extend D if negative
        if (D & 0x00000800U) {
            D |= 0xFFFFF000U;
        }
        /*
        std::cout << "OPCODE: " << std::hex << opcode << std::endl;
        std::cout << "MODE: " << std::hex << mode << std::endl;
        std::cout << "A: " << std::hex << A << std::endl;
        std::cout << "B: " << std::hex << B << std::endl;
        std::cout << "C: " << std::hex << C << std::endl;
        std::cout << "D: " << std::hex << D << std::endl;
        */

        // Handle the instruction based on the opcode
        switch (opcode) {
            case opcode::Halt: {
                //std::cout << "Handling HALT instruction" << std::endl;
                handle_halt(mode, halt);
                break;
            }
            case opcode::Interrupt: {
                //std::cout << "Handling INTERRUPT instruction" << std::endl;
                handle_interrupt(mode);
                break;
            }
            case opcode::Call: {
                handle_call(mode, A, B, D);
                break;
            }
            case opcode::Jmp: {
                handle_jump(mode, A, B, C, D);
                break;
            }
            case opcode::Xchg: {
                handle_xchg(B, C);
                break;
            }
            case opcode::Arit: {
                handle_arithmetic(mode, A, B, C);
                break;
            }
            case opcode::Logic: {
                handle_logic(mode, A, B, C);
                break;
            }
            case opcode::Shift: {
                handle_shift(mode, A, B, C);
                break;
            }
            case opcode::Store: {
                handle_store(mode, A, B, C, D);
                break;
            }
            case opcode::Load: {
                handle_load(mode, A, B, C, D);
                break;
            }
            default: {
                std::cerr << "Unknown Opcode: " << std::hex << std::setw(2) << std::setfill('0') << opcode << std::endl;
                illegal_trap();
                halt = true; // Stop execution on unknown opcode
                break;
            }
        }

    // Handle terminal
    //handle_terminal_input();
    //handle_terminal_output();  

    }
    //restore_terminal_mode();
    
    return 0;
}


void emulator::handle_halt(word mode, bool& halt) {
    halt = (mode == 0x0);
    if (mode != 0x0) illegal_trap();
    std::cout << "-----------------------------------------------------------------" << std::endl;
    std::cout << "Emulated processor executed halt instruction" << std::endl;
    print_cpu_state();
}

void emulator::handle_arithmetic(word mode, word A, word B, word C) {
    switch (mode) {
        case 0x0: {
            //std::cout << "Handling ADD instruction" << std::endl;
            write_reg(A, registers.gpr[B] + registers.gpr[C]);
            break;
        }
        case 0x1: {
            //std::cout << "Handling SUB instruction" << std::endl;
            write_reg(A, registers.gpr[B] - registers.gpr[C]);
            break;
        }
        case 0x2: {
            //std::cout << "Handling MUL instruction" << std::endl;
            write_reg(A, registers.gpr[B] * registers.gpr[C]);
            break;
        }
        case 0x3: {
            //std::cout << "Handling DIV instruction" << std::endl;
            if (registers.gpr[C] != 0) {
                write_reg(A, registers.gpr[B] / registers.gpr[C]);
            } else {
                //std::cerr << "Division by zero error" << std::endl;
                illegal_trap();
            }
            break;
        }
        default: {
            //std::cerr << "Illegal arithmetic mode: " << std::hex << mode << std::endl;
            illegal_trap();
            break;
        }
    }
}

void emulator::handle_logic(word mode, word A, word B, word C) {
    switch (mode) {
        case 0x0: {
            //std::cout << "Handling NOT instruction" << std::endl;
            registers.gpr[A] = ~registers.gpr[B];
            break;
        }
        case 0x1: {
            //std::cout << "Handling AND instruction" << std::endl;
            registers.gpr[A] = registers.gpr[B] & registers.gpr[C];
            break;
        }
        case 0x2: {
            //std::cout << "Handling OR instruction" << std::endl;
            registers.gpr[A] = registers.gpr[B] | registers.gpr[C];
            break;
        }
        case 0x3: {
            //std::cout << "Handling XOR instruction" << std::endl;
            registers.gpr[A] = registers.gpr[B] ^ registers.gpr[C];
            break;
        }
        default: {
            //std::cerr << "Illegal logic mode: " << std::hex << mode << std::endl;
            illegal_trap();
            break;
        }
    }
}

void emulator::handle_interrupt(word mode) {
    //std::cout << "Handling INTERRUPT instruction" << std::endl;
    if (mode == 0x0) {
        //std::cout << "Handler address " << registers.csr[handler] << std::endl;
        pushCSR(status);
        pushGPR(pc);
        registers.csr[cause] = SoftwareInterrupt;
        registers.csr[status] &= ~0x1;
        rpc = registers.csr[handler];
    } else {
        illegal_trap();
    }
}

void emulator::handle_load(word mode, word A, word B, word C, word D) {
    switch (mode) {
        case 0x0: {
            // CSRRD: Load CSR[B] into GPR[A]
            //std::cout << "Handling CSRRD instruction" << std::endl;
            registers.gpr[A] = registers.csr[B];
            break;
        }
        case 0x1: {
            // LD with immediate: GPR[A] = GPR[B] + D
            //std::cout << "Handling LD instruction with immediate" << std::endl;
            registers.gpr[A] = registers.gpr[B] + D;
            break;
        }
        case 0x2: {
            // LD with memory read: GPR[A] = mem[GPR[B] + GPR[C] + D]
            //std::cout << "Handling LD instruction with memory read" << std::endl;
            addr_t addr = registers.gpr[B] + registers.gpr[C] + D;
            //std::cout << "Address for reading: " << std::hex << addr << std::endl;
            //dump_memory(addr, 4);
            registers.gpr[A] = read_mem(addr);
            break;
        }
        case 0x3: {
            // POP: GPR[A] = mem[GPR[B]]; GPR[B] += D
            //std::cout << "Handling POP instruction" << std::endl;
            registers.gpr[A] = read_mem((addr_t)registers.gpr[B]);
            registers.gpr[B] += D;
            break;
        }
        case 0x4: {
            // CSRWR: CSR[A] = GPR[B]
            //std::cout << "Handling CSRWR instruction" << std::endl;
            registers.csr[A] = registers.gpr[B];
            break;
        }
        case 0x5: {
            // CSRWR with OR: CSR[A] |= D
            registers.csr[A] = registers.csr[B] | D;
            break;
        }
        case 0x6: {
            // Load into CSR: CSR[A] = mem[GPR[B] + GPR[C] + D]
            addr_t addr = (addr_t)registers.gpr[B] + (addr_t)registers.gpr[C] + D;
            registers.csr[A] = read_mem(addr);
            break;
        }
        case 0x7: {
            // POP to CSR: CSR[A] = mem[GPR[B]]; GPR[B] += D
            registers.csr[A] = read_mem((addr_t)registers.gpr[B]);
            registers.gpr[B] += D;
            break;
        }
        default: {
            illegal_trap();
            break;
        }
    }
}

void emulator::handle_store(word mode, word A, word B, word C, word D) {
    switch (mode) {
        case 0x0: {
            //std::cout << "Handling ST instruction" << std::endl;
            // ST: Store GPR[C] into memory at address GPR[A] + GPR[B] + D
            addr_t addr = (addr_t)registers.gpr[A] + (addr_t)registers.gpr[B] + D;
            write_mem(registers.gpr[C], addr);
            break;
        }
        case 0x1: {
            // PUSH: Push GPR[C] onto the stack
            //std::cout << "Handling PUSH instruction" << std::endl;
            //std::cout << "Register to push: " << std::hex << C << std::endl;
            //std::cout << "Value: " << std::hex << registers.gpr[C] << std::endl;
            pushGPR(C);
            break;
        }
        case 0x2: {
            //std::cout << "Handling ST instruction with memory read" << std::endl;
            // mem32[mem32[gpr[A]+gpr[B]+D]]<=gpr[C]; 
            reg tmp = read_mem((addr_t)registers.gpr[A] + (addr_t)registers.gpr[B] + D);
            write_mem(registers.gpr[C], tmp);
            break;
                }
        default: {
            illegal_trap();
            break;
        }
    }
}


void emulator::handle_jump(word mode, word A, word B, word C, word D) {
    switch (mode) {
        case 0x0: {
            //std::cout << "Handling JMP instruction" << std::endl;
            rpc = registers.gpr[A] + D;
            break;
        }
        case 0x1: {
            //std::cout << "Handling BEQ instruction" << std::endl;
            if (registers.gpr[B] == registers.gpr[C]) rpc = registers.gpr[A] + D;
            break;
        }
        case 0x2: {
            //std::cout << "Handling BNE instruction" << std::endl;
            // Handle branch if not equal
            if (registers.gpr[B] != registers.gpr[C]) rpc = registers.gpr[A] + D;
            break;
        }
        case 0x3: {
            //std::cout << "Handling BGT instruction" << std::endl;
            if (registers.gpr[B] > registers.gpr[C]) rpc = registers.gpr[A] + D;
            break;
        }
        case 0x8: {
            rpc = read_mem((addr_t) registers.gpr[A] + D);
            break;
        }
        case 0x9: {
            if (registers.gpr[B] == registers.gpr[C]) {
                rpc = read_mem((addr_t)registers.gpr[A] + D);
            }
            break;
        }
        case 0xA: {
            if (registers.gpr[B] != registers.gpr[C]) {
                rpc = read_mem((addr_t)registers.gpr[A] + D);
            }
            break;
        }
        case 0xB: {
            if (registers.gpr[B] > registers.gpr[C]) {
                rpc = read_mem((addr_t) registers.gpr[A] + D);
            }
            break;
        }
        default: {
            illegal_trap();
            break;
        }
    }
}

void emulator::handle_call(word mode, word A, word B, word D) {
    // Handle function call
    //std::cout << "Handling CALL instruction" << std::endl;
    pushGPR(pc);
    if (mode == 0x0) {
        //pc<=gpr[A]+gpr[B]+D;
        rpc = registers.gpr[A] + registers.gpr[B] + D;
    } else if (mode == 0x1) {
        // pc<=mem32[gpr[A]+gpr[B]+D]; 
        rpc = read_mem((addr_t)registers.gpr[A] + (addr_t)registers.gpr[B] + D);
    } else {
        illegal_trap();
    }
}

void emulator::handle_xchg(word B, word C) {
    //std::cout << "Handling XCHNG instruction" << std::endl;
    reg tmp = registers.gpr[B];
    write_reg(B, registers.gpr[C]);
    write_reg(C, tmp);
}


void emulator::handle_shift(word mode, word A, word B, word C) {
    if (mode == 0x0) {
        //std::cout << "Handling SHL instruction" << std::endl;
        registers.gpr[A] = registers.gpr[B] << registers.gpr[C];
    }
    else if (mode == 0x1) {
        //std::cout << "Handling SHR instruction" << std::endl;
        registers.gpr[A] = registers.gpr[B] >> registers.gpr[C];
    }
    else {
        illegal_trap();
    }
}


// Function to handle terminal input
void emulator::handle_terminal_input() {
    char ch;
    if ((read(STDIN_FILENO, &ch, 1) == 1) && !halt) {
        memory[TERM_IN] = ch; // Write character to term_in register
        //std::fill(memory + TERM_IN + 1, memory + TERM_IN + 4, 0);
        //dump_memory(memory + TERM_IN + 1, )

        if ((registers.csr[status] & (status_flags::TerminalDisable | status_flags::AllDisable)) == 0) {
            pushCSR(status);
            pushGPR(pc);
            registers.csr[status] |= status_flags::AllDisable; 
            registers.csr[cause] = exception_cause::TerminalInterrupt; 
            rpc = registers.csr[handler]; 
        }
    }
}
