package seu.io;

import seu.dfa.DFA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeFile {

    private BufferedWriter headWriter;
    private BufferedWriter writer;
    private LexFile lexFile;
    private DFA dfa;

    public CodeFile(LexFile lexFile, DFA dfa) {
        this.lexFile = lexFile;
        this.dfa = dfa;
    }

    public void writeFile(String filePath) throws IOException {
        File file = new File(filePath);
        String parent = file.getParent();
        String headPath;
        if (parent == null) {
            headPath = "yy.tab.h";
        } else {
            headPath = parent + "/yy.tab.h";
        }
        headWriter = new BufferedWriter(new FileWriter(headPath));
        writer = new BufferedWriter(new FileWriter(filePath));
        writeHeaders();
        writer.write(new CodeFileUtil(dfa).generate());
        writeUserSeg();
        writer.close();
        headWriter.close();
    }

    private void writeHeaders() throws IOException {
        headWriter.write(lexFile.headers.toString());
        headWriter.write("int yylex();");
    }

    private void writeUserSeg() throws IOException {
        writer.write(lexFile.userSeg.toString());
    }

}
