package seu.io;

import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class LexFile {

    private BufferedReader reader;

    /* Definitions start with %{ and end with %} */
    public StringBuffer headers = new StringBuffer();
    /* Macros with regular definition */
    public HashMap<String, String> macros = new HashMap<>();
    /* Regular expressions with action */
    public Vector<Pair<String, String>> regExps = new Vector<>();
    /* User segment */
    public StringBuffer userSeg = new StringBuffer();

    public LexFile(String filePath) throws Exception {
        reader = new BufferedReader(new FileReader(filePath));
        readMacros();
        readRegExps();
        readUserSeg();
    }

    private void readMacros() throws Exception {
        while (true) {
            String lineOfReader = reader.readLine();
            if (lineOfReader == null) throw new Exception("Lex format error - miss macro definitions");
            else if (lineOfReader.startsWith("%%")) return;
            else if (lineOfReader.startsWith("%{")) readHeaders();
            else {
                String[] splits = lineOfReader.split("[ \t]", 2);
                if (splits.length != 2) continue;
                String macro = splits[0];
                String definition = expandMacro(splits[1].trim());
                macros.put(macro, definition);
            }
        }
    }

    private void readHeaders() throws Exception {
        while (true) {
            String lineOfReader = reader.readLine();
            if (lineOfReader == null) throw new Exception("Lex format error - miss another \"%}\"");
            if (lineOfReader.startsWith("%}")) return;
            else headers.append(lineOfReader).append("\n");
        }
    }

    private void readRegExps() throws Exception {
        while (true) {
            String lineOfReader = reader.readLine();
            if (lineOfReader == null) throw new Exception("Lex format error - miss user segment");
            else if (lineOfReader.startsWith("%%")) return;
            else {
                String[] splits = lineOfReader.split("[ \t]+[{]", 2);
                if (splits.length != 2) continue;
                String regExp = expandMacro(splits[0]);
                String action = "{" + splits[1];
                regExps.add(new Pair<>(regExp, action));
            }
        }
    }

    private void readUserSeg() throws IOException {
        while (true) {
            String lineOfReader = reader.readLine();
            if (lineOfReader == null) {
                System.out.println("EOF");
                return;
            } else {
                userSeg.append(lineOfReader).append("\n");
            }
        }
    }

    private String expandMacro(String expWithMacro) {
        Integer indexOfLeftBrace = null;
        Integer indexOfRightBrace;
        for (int i = 0; i < expWithMacro.length(); i++) {
            if (expWithMacro.charAt(i) == '{') {
                indexOfLeftBrace = i;
            }
            if (expWithMacro.charAt(i) == '}') {
                indexOfRightBrace = i;
                if (indexOfLeftBrace != null) {
                    String key = expWithMacro.substring(indexOfLeftBrace + 1, indexOfRightBrace);
                    if (macros.containsKey(key)) {
                        expWithMacro = expWithMacro.replace("{" + key + "}", macros.get(key));
                    }
                }
                indexOfLeftBrace = null;
            }
        }
        return expWithMacro;
    }
}
