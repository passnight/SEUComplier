%{
#include <stdio.h>
#define IDENTIFIER 10000
#define CONSTANT 128
#define SIZEOF 129
#define STATIC 130
#define STRUCT 131
#define SWITCH 132
#define DEFAULT 133
#define TYPEDEF 134
#define CONTINUE 135
#define REGISTER 136
#define UNSIGNED 137
#define VOLATILE 138
#define LEFT_ASSIGN 139
#define RIGHT_ASSIGN 140
#define FOR 141
#define INT 142
#define PTR_OP 143
#define DIV_ASSIGN 144
#define AUTO 145
#define CASE 146
#define CHAR 147
#define ELSE 148
#define ENUM 149
#define GOTO 150
#define LEFT_OP 151
#define LONG 152
#define LE_OP 153
#define EQ_OP 154
#define GE_OP 155
#define RIGHT_OP 156
#define XOR_ASSIGN 157
#define VOID 158
#define DO 159
#define BREAK 160
#define CONST 161
#define IF 162
#define FLOAT 163
#define SHORT 164
#define NE_OP 165
#define STRING_LITERAL 166
#define OR_ASSIGN 167
#define UNION 168
#define MOD_ASSIGN 169
#define OR_OP 170
#define AND_OP 171
#define WHILE 172
#define AND_ASSIGN 173
#define MUL_ASSIGN 174
#define ELLIPSIS 175
#define DOUBLE 176
#define INC_OP 177
#define EXTERN 178
#define ADD_ASSIGN 179
#define DEC_OP 180
#define RETURN 181
#define SUB_ASSIGN 182
#define SIGNED 183
#define TYPE_NAME 184

void count();
int yywrap();
void comment();
int check_type();
%}
D			[0-9]
L			[a-zA-Z_]
H			[a-fA-F0-9]
E			[Ee][+-]?{D}+
FS			(f|F|l|L)
IS			(u|U|l|L)*
%%
"/*"			{ comment(); }

"auto"			{ count(); return(AUTO); }
"break"			{ count(); return(BREAK); }
"case"			{ count(); return(CASE); }
"char"			{ count(); return(CHAR); }
"const"			{ count(); return(CONST); }
"continue"		{ count(); return(CONTINUE); }
"default"		{ count(); return(DEFAULT); }
"do"			{ count(); return(DO); }
"double"		{ count(); return(DOUBLE); }
"else"			{ count(); return(ELSE); }
"enum"			{ count(); return(ENUM); }
"extern"		{ count(); return(EXTERN); }
"float"			{ count(); return(FLOAT); }
"for"			{ count(); return(FOR); }
"goto"			{ count(); return(GOTO); }
"if"			{ count(); return(IF); }
"int"			{ count(); return(INT); }
"long"			{ count(); return(LONG); }
"register"		{ count(); return(REGISTER); }
"return"		{ count(); return(RETURN); }
"short"			{ count(); return(SHORT); }
"signed"		{ count(); return(SIGNED); }
"sizeof"		{ count(); return(SIZEOF); }
"static"		{ count(); return(STATIC); }
"struct"		{ count(); return(STRUCT); }
"switch"		{ count(); return(SWITCH); }
"typedef"		{ count(); return(TYPEDEF); }
"union"			{ count(); return(UNION); }
"unsigned"		{ count(); return(UNSIGNED); }
"void"			{ count(); return(VOID); }
"volatile"		{ count(); return(VOLATILE); }
"while"			{ count(); return(WHILE); }

{L}({L}|{D})*		    { count(); return(check_type()); }

0[xX]{H}+{IS}?		    { count(); return(CONSTANT); }
0{D}+{IS}?		        { count(); return(CONSTANT); }
{D}+{IS}?		        { count(); return(CONSTANT); }
L?'(\\.|[^\\'])+'	    { count(); return(CONSTANT); }

{D}+{E}{FS}?		    { count(); return(CONSTANT); }
{D}*"."{D}+({E})?{FS}?	{ count(); return(CONSTANT); }
{D}+"."{D}*({E})?{FS}?	{ count(); return(CONSTANT); }

L?\"(\\.|[^\\"])*\"	    { count(); return(STRING_LITERAL); }

"..."			{ count(); return(ELLIPSIS); }
">>="			{ count(); return(RIGHT_ASSIGN); }
"<<="			{ count(); return(LEFT_ASSIGN); }
"+="			{ count(); return(ADD_ASSIGN); }
"-="			{ count(); return(SUB_ASSIGN); }
"*="			{ count(); return(MUL_ASSIGN); }
"/="			{ count(); return(DIV_ASSIGN); }
"%="			{ count(); return(MOD_ASSIGN); }
"&="			{ count(); return(AND_ASSIGN); }
"^="			{ count(); return(XOR_ASSIGN); }
"|="			{ count(); return(OR_ASSIGN); }
">>"			{ count(); return(RIGHT_OP); }
"<<"			{ count(); return(LEFT_OP); }
"++"			{ count(); return(INC_OP); }
"--"			{ count(); return(DEC_OP); }
"->"			{ count(); return(PTR_OP); }
"&&"			{ count(); return(AND_OP); }
"||"			{ count(); return(OR_OP); }
"<="			{ count(); return(LE_OP); }
">="			{ count(); return(GE_OP); }
"=="			{ count(); return(EQ_OP); }
"!="			{ count(); return(NE_OP); }
";"			    { count(); return(';'); }
("{"|"<%")		{ count(); return('{'); }
("}"|"%>")		{ count(); return('}'); }
","			    { count(); return(','); }
":"			    { count(); return(':'); }
"="			    { count(); return('='); }
"("			    { count(); return('('); }
")"			    { count(); return(')'); }
("["|"<:")		{ count(); return('['); }
("]"|":>")		{ count(); return(']'); }
"."			    { count(); return('.'); }
"&"			    { count(); return('&'); }
"!"			    { count(); return('!'); }
"~"			    { count(); return('~'); }
"-"			    { count(); return('-'); }
"+"			    { count(); return('+'); }
"*"			    { count(); return('*'); }
"/"			    { count(); return('/'); }
"%"			    { count(); return('%'); }
"<"			    { count(); return('<'); }
">"			    { count(); return('>'); }
"^"			    { count(); return('^'); }
"|"			    { count(); return('|'); }
"?"			    { count(); return('?'); }

[ \t\v\n\f]		{ count(); }
.			{ /* ignore bad characters */ }

%%


int yywrap()
{
	return(1);
}


void comment()
{
	/*char c, c1;

loop:
	while ((c = input()) != '*' && c != 0)
		putchar(c);

	if ((c1 = input()) != '/' && c != 0)
	{
		unput(c1);
		goto loop;
	}

	if (c != 0)
		putchar(c1);*/
}


int column = 0;

void count()
{
	int i;

	for (i = 0; yytext[i] != '\0'; i++)
		if (yytext[i] == '\n')
			column = 0;
		else if (yytext[i] == '\t')
			column += 8 - (column % 8);
		else
			column++;

	//ECHO;
}


int check_type()
{
/*
* pseudo code --- this is what it should check
*
*	if (yytext == type_name)
*		return(TYPE_NAME);
*
*	return(IDENTIFIER);
*/

/*
*	it actually will only return IDENTIFIER
*/

	return(IDENTIFIER);
}

int main(int argc, char *argv[])
{
	int ID;
    if (argc > 1)
        fopen_s(&yyin, argv[1], "r");
	if (NULL == yyin)
	{
		cerr << "Wrong path input.\n";
		return -1;
	}
	while ((ID = yylex()) >= 0)
	{
		fprintf(yyout, "%s%s%s%d%c", "Match word: ", &yytext[0], "\tID: ", ID, '\n');
	}
}