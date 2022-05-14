#include <ctype.h>
#include <iostream>
#include <unordered_map>
#include <string>
#include <vector>
#include <stack>
#include "yy.tab.h"

#define or 1;
#define and 2;
#define b 3

#define $$ -1
using namespace std;
extern FILE *yyin;
extern FILE *yyout;
extern string yytext;
extern int column;
void yyerror();
class Token
{
public:
    string name;
    unsigned char *value;
    int length;
    Token() {}
};
stack<Token> tokenStack;
class Production
{
public:
    int l;
    int rl;
    Production(int l, int rl) : l(l), rl(rl) {}
};
vector<Production> productions{
    {0, 1},
    {1, 1},
    {1, 3},
    {0, 3},
    {2, 1},
    {3, 1}};
void r0()
{
    Token t1 = tokenStack.top();
    tokenStack.pop();
    Token newToken;

    tokenStack.push(newToken);
}
void r1()
{
    Token t1 = tokenStack.top();
    tokenStack.pop();
    Token newToken;

    tokenStack.push(newToken);
}
void r2()
{
    Token t3 = tokenStack.top();
    tokenStack.pop();
    Token t2 = tokenStack.top();
    tokenStack.pop();
    Token t1 = tokenStack.top();
    tokenStack.pop();
    Token newToken;

    tokenStack.push(newToken);
}
void r3()
{
    Token t3 = tokenStack.top();
    tokenStack.pop();
    Token t2 = tokenStack.top();
    tokenStack.pop();
    Token t1 = tokenStack.top();
    tokenStack.pop();
    Token newToken;

    tokenStack.push(newToken);
}
void r4()
{
    Token t1 = tokenStack.top();
    tokenStack.pop();
    Token newToken;

    tokenStack.push(newToken);
}
void r5()
{
    Token t1 = tokenStack.top();
    tokenStack.pop();
    Token newToken;

    tokenStack.push(newToken);
}
vector<void (*)()> actions{r0, r1, r2, r3, r4, r5};
class Entry
{
public:
    int label;
    int target;
    Entry(int label, int target) : label(label), target(target) {}
};
unordered_map<int, unordered_map<int, Entry>> parsingTable = {
    {0, {{$$, Entry(1, 3)}, { or, Entry(0, 5)}, {and, Entry(1, 3)}}},
    {1, {{$$, Entry(1, 1)}, { or, Entry(1, 1)}, {and, Entry(1, 1)}}},
    {2, {{$$, Entry(2, -1)}, {and, Entry(0, 7)}}},
    {3, {{b, Entry(0, 4)}}},
    {4, {{$$, Entry(1, 4)}, { or, Entry(1, 4)}, {and, Entry(1, 4)}}},
    {5, {{b, Entry(0, 4)}}},
    {6, {{$$, Entry(1, 2)}, { or, Entry(1, 2)}, {and, Entry(1, 2)}}},
    {7, {{b, Entry(0, 4)}}},
    {8, {{$$, Entry(1, 0)}, { or, Entry(0, 5)}, {and, Entry(1, 0)}}}};
unordered_map<int, unordered_map<int, int>> gotoTable = {
    {0, {}},
    {1, {}},
    {2, {}},
    {3, {{0, 2}, {1, 8}, {2, 1}}},
    {4, {}},
    {5, {{2, 6}}},
    {6, {}},
    {7, {{1, 0}, {2, 1}}},
    {8, {}}};
int yylex_()
{
    int t = 0;
    do
    {
        t = yylex();
    } while (t == 0);
    return t;
}
void yyparse()
{
    int a = yylex_();
    stack<int> stack;
    stack.push(3);
    while (true)
    {
        try
        {
            int s = stack.top();
            Entry e = parsingTable.at(s).at(a);
            if (e.label == 0)
            {
                stack.push(e.target);
                a = yylex_();
                tokenStack.push(Token());
            }
            else if (e.label == 1)
            {
                for (size_t i = 0; i < productions.at(e.target).rl; i++)
                {
                    stack.pop();
                }
                int t = stack.top();
                stack.push(gotoTable.at(t).at(productions.at(e.target).l));
                actions.at(e.target)();
            }
            else if (e.label == 2)
            {
                break;
            }
        }
        catch (const std::out_of_range &oor)
        {
            yyerror();
            return;
        }
        catch (const std::exception &)
        {
            cerr << "fatal error" << endl;
            return;
        }
    }
}
// yylex() {
//     int c;
//     c = getchar();
//     if (isdigit(c)) {
//         yylval = c-'0';
//         return DIGIT;
//     }
//     return c;
// }
