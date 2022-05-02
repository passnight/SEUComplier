package seu.nfa;

import java.util.HashSet;
import java.util.Vector;

import static seu.nfa.NFAUtil.*;

public class NFA {

    /* The transition table of the NFA */
    public Vector<Vector<HashSet<Integer>>> transitionTable = new Vector<>();
    /* State to start always 0 */
    public final static int start = 0;
    /* State to accept */
    public int accept;

    public NFA() {
        accept = 0;
        transitionTable.add(initStateRow());
    }

    public NFA(char ch) {
        Vector<HashSet<Integer>> startStateRow = initStateRow(ch, 1);
        Vector<HashSet<Integer>> acceptStateRow = initStateRow();
        transitionTable.add(startStateRow);
        transitionTable.add(acceptStateRow);
        accept = 1;
    }

    public NFA(char from, char to) throws Exception {
        if (from <= to) {
            Vector<Character> chars = new Vector<>();
            for (int i = 0; i < to - from + 1; i++) chars.add((char) (from + i));
            Vector<HashSet<Integer>> startStateRow = initStateRow(chars, 1);
            Vector<HashSet<Integer>> acceptStateRow = initStateRow();
            transitionTable.add(startStateRow);
            transitionTable.add(acceptStateRow);
            accept = 1;
        } else throw new Exception("Lex syntax error - In [x-y] x must be small than y");
    }

    public NFA(NFA nfa) {
        accept = nfa.accept;
        for (Vector<HashSet<Integer>> stateRow : nfa.transitionTable) {
            Vector<HashSet<Integer>> newStateRow = new Vector<>();
            for (HashSet<Integer> transitionCell : stateRow) {
                newStateRow.add(new HashSet<>(transitionCell));
            }
            transitionTable.add(newStateRow);
        }
    }

    public void addTransition(int index, char ch, int transition) {
        transitionTable.elementAt(index).elementAt(ch).add(transition);
    }

    public void addTransition(int index, char ch, HashSet<Integer> transitions) {
        transitionTable.elementAt(index).elementAt(ch).addAll(transitions);
    }

    public void addTransition(int index, Vector<Character> chars, int transition) {
        for (char ch : chars) addTransition(index, ch, transition);
    }

    /**
     * All state number add a value for every row shifting back.
     *
     * @param value Number to add.
     * @return A copy of origin transition table with state number added.
     */
    public Vector<Vector<HashSet<Integer>>> increasedStateNumber(int value) {
        Vector<Vector<HashSet<Integer>>> newTable = new Vector<>();
        for (Vector<HashSet<Integer>> stateRow : transitionTable) {
            Vector<HashSet<Integer>> newRow = new Vector<>();
            for (HashSet<Integer> transitionCell : stateRow) {
                HashSet<Integer> newCell = new HashSet<>();
                for (Integer transition : transitionCell) {
                    newCell.add(transition + value);
                }
                newRow.add(newCell);
            }
            newTable.add(newRow);
        }
        return newTable;
    }

    /**
     * Get a table of NFA transition table.
     *
     * @return String of the message.
     */
    public String debugMessage() {
        return transitionTableDebugMessage(transitionTable) + "accept: " + accept + '\n';
    }
}
