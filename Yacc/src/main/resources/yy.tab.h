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
int yylex();