package seu.io

import seu.dfa.DFA
import seu.dfa.DFAUtil

class CodeFileUtil(private val dfa: DFA) {

    fun generate(): String {
        val head = """
            #include <iostream>
            #include <string>
            #include <unordered_map>
            #include "yy.tab.h"
            using namespace std;
            string yytext;
            unsigned int yylength;
            FILE* yyin = stdin;
            FILE* yyout = stdout;
            """.trimIndent()
        return head + '\n' +
                transitionTable() + '\n' +
                acceptFunctions() + '\n' +
                actionMap() + '\n' +
                yylex() + '\n'
    }

    fun acceptFunctions(): String {
        val builder = StringBuilder()
        for (entry in dfa.acceptAction) {
            builder.append("""
                int accept${entry.key}() {
                    ${entry.value}
                    return 0;
                }
                """.trimIndent())
            builder.append('\n')
        }
        builder.append("""
            int acceptDefault() {
                cout << yytext << ":unknown" << endl;
                return 0;
            }
            """.trimIndent())
        return builder.toString()
    }

    fun actionMap(): String {
        val builder = StringBuilder("unordered_map<int, int(*)()> actionMap = {")
        for (entry in dfa.acceptAction) {
            builder.append("\n\t{${entry.key},accept${entry.key}},")
        }
        builder.deleteCharAt(builder.lastIndex)
        builder.append("\n};")
        return builder.toString()
    }

    fun yylex(): String {
        return """
            int yylex() {
                int state = ${dfa.start};
                int nextChar;
                yytext = "";
                yylength = 0;
                int lastAccept = -1;
                unsigned int lastLength = 0;

                while (true) {
                    if (stdin == yyin)
                        nextChar = cin.get();
                    else
                        nextChar = fgetc(yyin);
                    if (nextChar == EOF)
                        if (0 == yylength)
                            return -1;
                        else
                            state = -1;
                    else
                    {
                        state = transitionTable[state][nextChar];
                        yytext.push_back(nextChar);
                        yylength++;
                        if (actionMap.find(state) != actionMap.end()) {
                            lastAccept = state;
                            lastLength = yylength;
                        }
                    }
                    if (state == -1) {
                        while (yylength > lastLength + (lastAccept == -1)) {
                            if (stdin == yyin)
                                cin.unget();
                            else
                                fseek(yyin, -1, SEEK_CUR);
                            yytext.pop_back();
                            yylength--;
                        }
                        if (lastAccept == -1)
                            return acceptDefault();
                        else
                            return actionMap.at(lastAccept)();
                    }
                }
                return 0;
            }
            """.trimIndent()
    }

    fun transitionTable(): String {
        val builder = StringBuilder()
        builder.append("const int transitionTable[${dfa.transitionTable.size}][${DFAUtil.COLUMNS}] = {")
        for (row in dfa.transitionTable) {
            builder.append("\n\t")
            for (state in row) {
                builder.append(state ?: -1).append(',')
            }
        }
        builder.deleteCharAt(builder.lastIndex)
        builder.append("\n};")
        return builder.toString()
    }
}