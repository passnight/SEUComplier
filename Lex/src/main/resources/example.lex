%{
#include <iostream>
#define IF 5
#define ID 12
#define INTEGER 13
%}
ws      [ \t\n]+
letter  [A-Za-z]
digit   [0-9]
id      {letter}({letter}|{digit})*
integer {digit}+
%%
{ws}      {}
if        {return IF;}
{id}      {return ID;}
{integer} {return INTEGER;}
%%
int main() {
	while (yylex(cin)>=0) ;
}