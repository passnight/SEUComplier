package seu.io;

import org.javatuples.Pair;
import org.junit.BeforeClass;
import org.junit.Test;
import seu.io.LexFile;

import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.*;

public class LexFileTest {

    private static LexFile lexFile;

    @BeforeClass
    public static void constructor() throws Exception {
        lexFile = new LexFile("resource/example.lex");
    }

    @Test
    public void readHeaders() {
        assertEquals("#include <iostream>\n" +
                "#define IF 5\n" +
                "#define ID 12\n" +
                "#define INTEGER 13\n", lexFile.headers.toString());
    }

    @Test
    public void readMacros() {
        HashMap<String, String> expected = new HashMap<>();
        expected.put("ws", "[ \\t\\n]+");
        expected.put("letter", "[A-Za-z]");
        expected.put("digit", "[0-9]");
        expected.put("id", "[A-Za-z]([A-Za-z]|[0-9])*");
        expected.put("integer", "[0-9]+");
        assertEquals(expected, lexFile.macros);
    }

    @Test
    public void readRegExps() {
        Vector<Pair<String, String>> expected = new Vector<>();
        expected.add(new Pair<>("[ \\t\\n]+", "{}"));
        expected.add(new Pair<>("if", "{return IF;}"));
        expected.add(new Pair<>("[A-Za-z]([A-Za-z]|[0-9])*", "{return ID;}"));
        expected.add(new Pair<>("[0-9]+", "{return INTEGER;}"));
        assertEquals(expected, lexFile.regExps);
    }

    @Test
    public void readUserSeg() {
        assertEquals("int main() {\n" +
                "\twhile (yylex(cin)>=0) ;\n" +
                "}\n", lexFile.userSeg.toString());
    }
}