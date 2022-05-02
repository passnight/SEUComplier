package seu;

import seu.dfa.DFA;
import seu.dfa.DFAUtil;
import seu.io.CodeFile;
import seu.io.LexFile;
import seu.nfa.IntegratedNFA;

public class App {
    public static void main(String[] args) {
        try {
            String inputFilePath = "C:\\Project\\SEUCompiler\\Lex\\src\\main\\resources\\example2.lex";
            String outputFilePath = "C:\\Project\\SEUCompiler\\Lex\\src\\main\\resources\\example2.cpp";
            LexFile lexFile = new LexFile(inputFilePath);
            IntegratedNFA nfa = new IntegratedNFA(lexFile.regExps);
            DFA dfa = new DFA(nfa);
            dfa = DFAUtil.minimizeDFA(dfa);
            CodeFile codeFile = new CodeFile(lexFile, dfa);
            codeFile.writeFile(outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
