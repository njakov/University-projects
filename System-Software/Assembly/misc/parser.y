%{
	#include <stdio.h>
	#include <stdlib.h>
	#include <string.h>

	using namespace std;

	extern FILE *yyin;
	extern FILE *yyout;
	extern int line_num;

	int yyparse(void);
	int yylex();
	void yyerror(const char* msg);

%}

%code requires {
	#include "../inc/common.hpp"
	#include "../inc/instruction.hpp"
	#include "../inc/directive.hpp"
}

%union {
  unsigned int number;
  char* string;
	operand oper;
}
/* Declare output file names. */
%output "misc/parser.cpp"
%defines "misc/parser.hpp"




/* Token definition */
%token GLOBAL EXTERN EQU SECTION WORD SKIP ASCII END
%token  HALT INT IRET CALL RET JMP BEQ BNE BGT PUSH POP XCHG ADD SUB MUL DIV NOT AND OR XOR SHL SHR LD ST CSRRD CSRWR
%token LPAREN RPAREN LBRACK RBRACK LBRACE RBRACE
%token SHLOP SHROP
%token PCT DOLLAR DOT SEMI COLON COMMA QUOTE
%token STATUS HANDLER CAUSE
%token EOL

%token <number> GPR
%token <number> NUMBER
%token <string> SYMBOL
%token <string> CCONST
%token <string> STRING

%nterm <number> csreg;
%nterm <oper> jmp_operand;
%nterm <oper> op;

%left '*' '/' SHLOP SHROP
%left '|' '&' '^' '!'
%left '+' '-' 


/* expression priorities and rules */

%%

prog
	: statements
	;

statements
	:
	| statements statement
	;

statement
	:
	label
	| SYMBOL COLON directive {
			//printf("Directive new label before directive: %s\n", $1);
			mk_label($1);
			free($1);
	}
	| SYMBOL COLON instr {
			//printf("Directive new label before instruction: %s\n", $1);
			mk_label($1);
			free($1);
	}
	| directive
	| instr
	| EOL {}
	;


label:
	SYMBOL COLON EOL  {
		//printf("Directive new label: %s\n", $1);
		//adding to symbol_table
		mk_label($1);
		free($1);
	}

directive
	:  
	GLOBAL global_list EOL
	| EXTERN extern_list EOL
	| SECTION SYMBOL EOL {
		//printf("Parser: Directive section: %s\n", $2);
		mk_section($2);
		free($2);
	}
	| WORD word_list EOL
	| SKIP NUMBER EOL {
		//printf("Directive skip: %d\n", $2);
		mk_skip($2);
	}
	| ASCII STRING EOL{
		//printf("Directive ASCII: %s\n", $2);
		mk_ascii($2);
		free($2);
	}
	| END EOL {
		//printf("Directive END\n");
		mk_end();
	}
	
	| EQU SYMBOL COMMA expr EOL {	
		//printf("Directive EQU defining symbol : %s\n", $2);
		//mk_equ
		free($2);
		}
	;

global_list
	: SYMBOL {
		//printf("Global symbol: %s\n", $1);
		mk_global($1);
		free($1);
	}
	| global_list COMMA SYMBOL {
		//printf("Global symbol: %s\n", $3);
		mk_global($3);
		free($3);
		
	}
	;

extern_list
	: SYMBOL {
		//printf("Extern symbol: %s\n", $1);
		mk_extern($1);
		free($1);
		
		
	}
	| extern_list COMMA SYMBOL {
		//printf("Extern symbol: %s\n", $3);
		mk_extern($3);
		free($3);
		
	}
	;

word_list
	: word
	| word_list COMMA word
	;

word
	: SYMBOL {
		//printf("Word symbol: %s\n", $1);
		mk_word($1);
		free($1);
		
	}
	| NUMBER {
		//printf("Word literal: %d\n", $1);
		mk_word($1);
	}
	;



instr
	: HALT EOL
			{ 
				//printf("HALT\n");
				mk_halt(); 
			}
	| INT EOL
			{ 
				//printf("INT\n");
			 	mk_int(); 
			}
	| IRET EOL
			{ 
				//printf("IRET\n");
				mk_iret(); 
			}
	| CALL jmp_operand EOL
			{ 
				//printf("CALL\n");
				mk_call($2); 
			}
	| RET EOL
			{ 
				//printf("RET\n");
				mk_pop(REG_PC); 
			}
	| JMP jmp_operand EOL
			{ 
				//printf("JMP\n");
				mk_jmp($2); 
			}
	| BEQ GPR COMMA GPR COMMA jmp_operand EOL
			{ 
				//printf("BEQ %d , %d \n", $2, $4);

				mk_branch(BEQ_OC, $2, $4, $6); 
			}
	| BNE GPR COMMA GPR COMMA jmp_operand EOL
			{ 
				//printf("BNE\n");
				mk_branch(BNE_OC, $2, $4, $6); 
			}
	| BGT GPR COMMA GPR COMMA jmp_operand EOL
			{ 
				//printf("BGT %d\n", $2);
				mk_branch(BGT_OC, $2, $4, $6); 
			}
	| PUSH GPR EOL
			{ 
				//printf("PUSH %d\n", $2);
				mk_push($2); 
			}
	| POP GPR EOL
			{ 
				//printf("POP\n");
				mk_pop($2); 
			}
	| XCHG GPR COMMA GPR EOL
			{ 
				//printf("XCHG\n");
				mk_xchng($2, $4); 
			}
	| ADD GPR COMMA GPR EOL
			{ 
				//printf("ADD\n");
				mk_alu_op(ADD_OC, $2, $4); 
			}
	| SUB GPR COMMA GPR EOL
			{ 
				//printf("SUB\n");
				mk_alu_op(SUB_OC, $2, $4); 
			}
	| MUL GPR COMMA GPR EOL
			{ 
				//printf("MUL\n");
				mk_alu_op(MUL_OC, $2, $4); 
			}
	| DIV GPR COMMA GPR EOL
			{ 
				//printf("DIV\n");
				mk_alu_op(DIV_OC, $2, $4); 
			}
	| NOT GPR EOL
			{ 
				//printf("NOT\n");
				mk_alu_op(NOT_OC, $2, $2); 
			}
	| AND GPR COMMA GPR EOL
			{ 
				//printf("AND\n");
				mk_alu_op(AND_OC, $2, $4); 
			}
	| OR GPR COMMA GPR EOL
			{ 
				//printf("OR\n");
				mk_alu_op(OR_OC, $2, $4); 
			}
	| XOR GPR COMMA GPR EOL
			{ 
				//printf("XOR\n");
				mk_alu_op(XOR_OC, $2, $4); 
			}
	| SHL GPR COMMA GPR EOL
			{ 
				//printf("SHL\n");
				mk_alu_op(SHL_OC, $2, $4); 
			}
	| SHR GPR COMMA GPR EOL
			{ 
				//printf("SHR\n");
				mk_alu_op(SHR_OC, $2, $4); 
			}
	| LD op COMMA GPR EOL
			{ 
				//printf("LD\n");
				mk_ld($2, $4); 
			}
	| ST GPR COMMA op EOL
			{ 
				//printf("ST\n");
				mk_st($2, $4); 
			}
	| CSRRD csreg COMMA GPR EOL
			{ 
				//printf("CSRRD\n");
				mk_csrrd($2, $4); 
			}
	| CSRWR GPR COMMA csreg EOL
			{ 
				//printf("CSRWR\n");
				mk_csrwr($2, $4); 
			}
	;

op
	: NUMBER {
    //printf("Recognized operand: MEM_LIT, value: %d\n", $1);
		$$ = operand(MEM_LIT, $1, 0, nullptr);	
}
| SYMBOL {
    //printf("Recognized operand: MEM_SYM, identifier: %s\n", $1);
		$$ = operand(MEM_SYM, 0, 0, $1);
		
}
| DOLLAR NUMBER {
    //printf("Recognized operand: IMM_LIT, value: %d\n", $2);
		$$ = operand(IMM_LIT, $2, 0, nullptr);	
}
| DOLLAR SYMBOL {
    //printf("Recognized operand: IMM_SYM, identifier: %s\n", $2);
		$$ = operand(IMM_SYM, 0, 0, $2);
		
}
| GPR {
    //printf("Recognized operand: IMM_REG, register: %d\n", $1);
		$$ = operand(REG_DIR, 0, $1, nullptr);

}
| LBRACK GPR RBRACK {
    //printf("Recognized operand: MEM_REG, register: %d\n", $2);
		$$ = operand(REG_IND, 0, $2, nullptr);
}
| LBRACK GPR '+' NUMBER RBRACK {
    //printf("Recognized operand: MEM_REG_LIT, register: %d, offset: %d\n", $2, $4);
		$$ = operand(REG_IND_OFFSL, $4, $2, nullptr);
}
| LBRACK GPR '+' SYMBOL RBRACK {
    //printf("Recognized operand: MEM_REG_SYM, register: %d, identifier: %s\n", $2, $4);
		$$ = operand(REG_IND_OFFSS, 0, $2, $4);
		}
;


jmp_operand
	: NUMBER {
		//printf("This operand is number %d\n", $1);
		$$ = operand(IMM_LIT, $1, 0, nullptr);
		}
		
	| SYMBOL {
		//printf("This operand is string %s\n", $1);
		$$ = operand(IMM_SYM, 0, 0, $1);
		}
	;

csreg
	: STATUS  { printf("STATUS REG\n"); }
	| HANDLER { printf("HANDLER REG\n");	}	
	| CAUSE   { printf("CAUSE REG\n");	}
	;



expr:
	arg '*' arg	{	printf("MUL expr\n");	}
	|	arg '/' arg	{ printf("Division operation:\n"); }
	| arg SHROP arg { printf("SHIFTR operation:\n"); }
	| arg SHLOP arg { printf("SHIFTL operation:\n"); }
	|	arg '|' arg { printf("OR operation:\n"); }
	|	arg '&' arg { printf("AND operation:\n"); }
	|	arg '^' arg { printf("^ operation:\n"); }
	|	arg '+' arg { printf("ADD operation:\n"); }
	|	arg '-' arg { printf("MINUS operation:\n"); }
	| arg
;


arg:
 	NUMBER {printf("Number arg %d\n", $1);}
 | SYMBOL {printf("Symbol arg %s\n", $1);}
 | subexpr
 ;

subexpr: LPAREN expr RPAREN 
	| '~' arg { printf("NOT operation:\n"); }
;


%%

/* In the event of some kind of error, this will get called. */
void yyerror(const char *msg) {
	fprintf(stderr, "Parse error on line : %d\n", line_num);
	fprintf(stderr, "Halting the program due to parse error: %s\n", msg);
}


// Function to parse the file
int parse_file(const char* file_to_parse) {
    // Open the file using FILE*
    FILE* file = fopen(file_to_parse, "r");
    if (!file) {
        // Use fprintf to report errors to stderr
        fprintf(stderr, "Error: Cannot open file %s\n", file_to_parse);
        return -1;
    }

    yyin = file;

    // Parse through the input
    int result = yyparse();

    // Close the file
    fclose(file);

    return result;
}
