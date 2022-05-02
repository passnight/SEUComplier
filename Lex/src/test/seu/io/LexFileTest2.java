package seu.io;

import org.javatuples.Pair;
import org.junit.BeforeClass;
import org.junit.Test;
import seu.io.LexFile;

import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.*;

public class LexFileTest2 {

    private static LexFile lexFile;

    @BeforeClass
    public static void constructor() throws Exception {
        lexFile = new LexFile("resource/example2.lex");
    }

    @Test
    public void readMacros() {
        HashMap<String, String> expected = new HashMap<>();
        expected.put("D", "[0-9]");
        expected.put("L", "[a-zA-Z_]");
        expected.put("H", "[a-fA-F0-9]");
        expected.put("E", "[Ee][+-]?[0-9]+");
        expected.put("FS", "(f|F|l|L)");
        expected.put("IS", "(u|U|l|L)*");
        assertEquals(expected, lexFile.macros);
    }

    @Test
    public void readRegExps() {
        Vector<Pair<String, String>> expected = new Vector<>();
        expected.add(new Pair<>("\"/*\"", "{ comment(); }"));
        expected.add(new Pair<>("\"auto\"", "{ count(); return(AUTO); }"));
        assertEquals(expected.get(0), lexFile.regExps.get(0));
        assertEquals(expected.get(1), lexFile.regExps.get(1));
    }
}