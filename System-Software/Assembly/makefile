# Directories
SRC_DIR = src
MISC_DIR = misc
BUILD_DIR = build
TEST_A = tests/nivo-a
TEST_B = tests/nivo-b

# Tools
FLEX = flex
BISON = bison
CXX = g++
CXXFLAGS = -Wall -Wextra -Wpedantic

# Source Files
ASM_SRC = $(SRC_DIR)/common.cpp $(MISC_DIR)/lexer.cpp $(MISC_DIR)/parser.cpp \
          $(SRC_DIR)/operand.cpp $(SRC_DIR)/structs.cpp $(SRC_DIR)/assembler.cpp \
          $(SRC_DIR)/instruction.cpp $(SRC_DIR)/directive.cpp $(SRC_DIR)/assembler_main.cpp

LN_SRC = $(SRC_DIR)/common.cpp $(SRC_DIR)/structs.cpp \
         $(SRC_DIR)/linker.cpp $(SRC_DIR)/linker_main.cpp

EMU_SRC = $(SRC_DIR)/emulator.cpp $(SRC_DIR)/common.cpp \
          $(SRC_DIR)/emulator_main.cpp

# Targets
ASSEMBLER = assembler
LINKER = linker
EMULATOR = emulator

nivo-a: 
	./${ASSEMBLER} -o $(TEST_A)/main.o $(TEST_A)/main.s
	./${ASSEMBLER} -o $(TEST_A)/math.o $(TEST_A)/math.s
	./${ASSEMBLER} -o $(TEST_A)/handler.o $(TEST_A)/handler.s
	./${ASSEMBLER} -o $(TEST_A)/isr_timer.o $(TEST_A)/isr_timer.s
	./${ASSEMBLER} -o $(TEST_A)/isr_terminal.o $(TEST_A)/isr_terminal.s
	./${ASSEMBLER} -o $(TEST_A)/isr_software.o $(TEST_A)/isr_software.s
	./${LINKER} -hex \
		-place=my_code@0x40000000 -place=math@0xF0000000 \
		-o $(TEST_A)/program.hex \
		$(TEST_A)/handler.o $(TEST_A)/math.o $(TEST_A)/main.o $(TEST_A)/isr_terminal.o $(TEST_A)/isr_timer.o $(TEST_A)/isr_software.o
	./${EMULATOR} $(TEST_A)/program.hex

nivo-b: 
	./${ASSEMBLER} -o $(TEST_B)/main.o $(TEST_B)/main.s
	./${ASSEMBLER} -o $(TEST_B)/handler.o $(TEST_B)/handler.s
	./${ASSEMBLER} -o $(TEST_B)/isr_terminal.o $(TEST_B)/isr_terminal.s
	./${ASSEMBLER} -o $(TEST_B)/isr_timer.o $(TEST_B)/isr_timer.s
	./${LINKER} -hex \
		-place=my_code@0x40000000 \
		-o $(TEST_B)/program.hex \
		$(TEST_B)/main.o $(TEST_B)/isr_terminal.o $(TEST_B)/isr_timer.o $(TEST_B)/handler.o
	./${EMULATOR} $(TEST_B)/program.hex

all: $(ASSEMBLER) $(LINKER) $(EMULATOR)

# Parser and Lexer Generation
$(MISC_DIR)/parser.cpp $(MISC_DIR)/parser.hpp: $(MISC_DIR)/parser.y
	$(BISON) -d $<

$(MISC_DIR)/lexer.cpp: $(MISC_DIR)/lexer.l $(MISC_DIR)/parser.hpp
	$(FLEX) -o $@ $<

# Build Targets
$(ASSEMBLER): $(ASM_SRC)
	$(CXX) $(CXXFLAGS) -o $@ $^

$(LINKER): $(LN_SRC)
	$(CXX) $(CXXFLAGS) -o $@ $^

$(EMULATOR): $(EMU_SRC)
	$(CXX) $(CXXFLAGS) -o $@ $^

# Clean Targets
clean_all: clean 
	rm -f $(ASSEMBLER) $(LINKER) $(EMULATOR)

clean: clean_tests
	rm -f $(MISC_DIR)/lexer.cpp $(MISC_DIR)/lexer.hpp $(MISC_DIR)/parser.cpp $(MISC_DIR)/parser.hpp
	
clean_tests:
	rm -f $(TEST_A)/*.o $(TEST_A)/*.txt $(TEST_A)/*.hex
	rm -f $(TEST_B)/*.o $(TEST_B)/*.txt $(TEST_B)/*.hex


