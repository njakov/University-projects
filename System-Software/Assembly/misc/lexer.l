%{
  #include <stdio.h>
  #include <iostream>
  #include "parser.hpp"

  using namespace std;
  int line_num = 1;
  void yyerror(const char*);
%}
%option outfile="misc/lexer.cpp" header-file="misc/lexer.hpp"
%option noyywrap
%option nounput


/*  All literal consts will be returned as NUMBER (int) */
LITERALHEX  (0[Xx][0-9a-fA-F]+)
LITERALOCT  0o[0-7]+
LITERALBIN  0[Bb][01]+
LITERALDEC  0|([1-9][0-9]*)

SYMBOL 			[a-zA-Z_][a-zA-Z0-9_]*
OPSIGN      [\+\-\*\/\|\~\&\^]

PRINT		[ -~]
CCONST		(\'{PRINT}\')|(\'\\[nftrbv]\')
STRING		\"{PRINT}*\"

/*Operators*/


/*Directives*/
GLOBAL ^\.global
EXTERN ^\.extern
EQU ^\.equ
SECTION ^\.section
WORD ^\.word
SKIP ^\.skip
ASCII ^\.ascii
END	^\.end


/*Instructions*/
HALT    (halt)
INT     (int)
IRET    (iret)
CALL    (call)
RET     (ret)
JMP     (jmp)
BEQ     (beq)
BNE     (bne)
BGT     (bgt)
PUSH    (push)
POP     (pop)
XCHG    (xchg)
ADD     (add)
SUB     (sub)
MUL     (mul)
DIV     (div)
NOT     (not)
AND     (and)
OR      (or)
XOR     (xor)
SHL     (shl)
SHR     (shr)
LD      (ld)
ST      (st)

/*Registers*/

/*  All GPR registers will be returned as NUMBER (int) */
CSRRD   (csrrd)
CSRWR   (csrwr)
GPR (%r[0-9]|1[0-3])
SP (%sp|%r14)
PC (%pc|%r15)
STATUS  (%status)
HANDLER (%handler)
CAUSE   (%cause)

%%

"#"[^\n\r]* { printf("Comment: '%s'\n", yytext);  }

{GLOBAL} { return (GLOBAL); }
{EXTERN} { return (EXTERN); }
{EQU} { return (EQU); }
{SECTION} { return (SECTION); }
{WORD} { return (WORD); }
{SKIP} { return (SKIP); }
{ASCII} { return (ASCII); }
{END}	{ return (END); }

{HALT} {return (HALT); }
{INT}  {return (INT); }
{IRET} {return (IRET); }
{CALL} {return (CALL); }
{RET} {return (RET); }
{JMP} {return (JMP); }
{BEQ} {return (BEQ); }
{BNE} {return (BNE); }
{BGT} {return (BGT); }
{PUSH} {return (PUSH); }
{POP} {return (POP); }
{XCHG} {return (XCHG); }
{ADD} {return (ADD); }
{SUB}  {return (SUB); }
{MUL} {return (MUL); }
{DIV} {return (DIV); }
{NOT} {return (NOT); }
{AND} {return (AND); }
{OR} {return (OR); }
{XOR} {return (XOR); }
{SHL} {return (SHL); }
{SHR} {return (SHR); }
{LD} {return (LD); }
{ST} {return (ST); }
{CSRRD} {return (CSRRD); }
{CSRWR} {return (CSRWR); }

{OPSIGN}  { return(yytext[0]);  }
(<<)        {return SHLOP;}
(>>)        {return SHROP;}

"("				{ return LPAREN; }
")"				{ return RPAREN; }
"["				{ return LBRACK; }
"]"				{ return RBRACK; }
"{"				{ return LBRACE; }
"}"				{ return RBRACE; }

"%"       { return PCT; }
"$"       { return DOLLAR;  }
"."       { return DOT; }
":"       { return COLON; }
";"				{ return SEMI; }
","       { return COMMA; }
["]       { return QUOTE; }


{LITERALDEC}  {   yylval.number = stoi(yytext, nullptr); return NUMBER; }
{LITERALHEX}	{   yylval.number = stoul(yytext + 2, nullptr, 16); return NUMBER; }
{LITERALOCT}  {   yylval.number = stoul(yytext + 2, nullptr, 8); return NUMBER; }
{LITERALBIN}  {   yylval.number = stoul(yytext + 2, nullptr, 2); return NUMBER; }

{GPR} { yylval.number = stoi(yytext + 2, nullptr); return GPR;  }
{SP}  { yylval.number = 14; return GPR; }
{PC}  { yylval.number = 15; ; return GPR; }

{STATUS}  { return STATUS; }
{HANDLER} { return HANDLER; }
{CAUSE} { return CAUSE; }

{SYMBOL}  { yylval.string = strdup(yytext); return SYMBOL;  }
{CCONST}  { yylval.string = strdup(yytext); return CCONST;  }
{STRING}  { yylval.string = strdup(yytext); return STRING; }

[ \t] { }
[ \t\r\f]+  { }
"\n"    { ++line_num; return EOL; }


.				  { yyerror("Unrecognized character"); }


%%
