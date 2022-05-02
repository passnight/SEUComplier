package seu.nfa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

public class NFAUtil {

    /* Definition of ε */
    public final static char EPSILON = 128;
    /* Total columns of transition table */
    public final static int COLUMNS = 129;
    /* Definition of regular operators*/
    private final static Character BOTTOM = 10000;
    private final static Character CONCAT = 10001;

    /**
     * Generate a NFA from a regular expression.
     *
     * @param regExp Regular expression.
     * @return A NFA.
     */
    public static NFA regExpToNFA(String regExp) throws Exception {
        HashMap<Character, Integer> priorityTable = new HashMap<>();
        priorityTable.put(BOTTOM, 0);
        priorityTable.put('(', 1);
        priorityTable.put('|', 2);
        priorityTable.put(CONCAT, 3);
        priorityTable.put('*', 4);
        priorityTable.put('+', 4);
        priorityTable.put('?', 4);
        priorityTable.put(')', 5);
        /* stack for operations */
        Stack<Character> opStack = new Stack<>();
        Vector<Object> nfaQueue_infix = regExpToSubNFAs(regExp, priorityTable);
        addConcatForNFAs(nfaQueue_infix);
        Vector<Object> nfaQueue_suffix = new Vector<>();

        opStack.push(BOTTOM);
        for (Object obj : nfaQueue_infix) {
            if (obj.getClass().equals(NFA.class)) {
                nfaQueue_suffix.add(obj);
            } else {
                Character op = (Character) obj;
                if (op == '(')
                    opStack.push(op);
                else if (op == ')') {
                    while (!opStack.peek().equals('(')) {
                        nfaQueue_suffix.add(opStack.pop());
                    }
                    opStack.pop();
                } else if (priorityTable.get(op) <= priorityTable.get(opStack.peek())) {
                    nfaQueue_suffix.add(opStack.pop());
                    opStack.push(op);
                } else if (priorityTable.get(op) == -1)
                    nfaQueue_suffix.add(op);
                else
                    opStack.push(op);
            }
        }
        while (!opStack.empty()) {
            nfaQueue_suffix.add(opStack.pop());
        }

        Stack<NFA> nfaStack = new Stack<>();
        for (Object obj : nfaQueue_suffix) {
            if (obj.getClass().equals(NFA.class)) {
                nfaStack.push((NFA) obj);
            } else {
                Character op = (Character) obj;
                if (op.equals(CONCAT)) {
                    NFA right = nfaStack.pop();
                    nfaStack.push(concat(nfaStack.pop(), right));
                } else {
                    switch (op) {
                        case '*':
                            nfaStack.push(star(nfaStack.pop()));
                            break;
                        case '?':
                            nfaStack.push(question(nfaStack.pop()));
                            break;
                        case '+':
                            nfaStack.push(plus(nfaStack.pop()));
                            break;
                        case '|':
                            nfaStack.push(or(nfaStack.pop(), nfaStack.pop()));
                            break;
                    }
                }
            }
        }

        if (nfaStack.size() != 1)
            throw new Exception("Lex syntax error - Wrong regular expression");
        return nfaStack.pop();
    }

    /**
     * Replace the atomic expression in regular expression with a sub Nfa.
     *
     * @param regExp regular expression
     * @return a vector consist of atomic operations    (|,*,+,?)
     * and atomic expressions  ([],"",[^] and common characters)
     */
    private static Vector<Object> regExpToSubNFAs(String regExp, HashMap<Character, Integer> priorityTable) throws Exception {
        Stack<Character> charStack = new Stack<>();
        Vector<Object> nfaQueue = new Vector<>();

        /* " */
        final Character QUOTE = 129;
        /* [] */
        final Character SQUARE = 130;
        /* [^] */
        final Character NOT = 131;

        Character lock = null;

        for (int i = 0; i < regExp.length(); i++) {
            char ch = regExp.charAt(i);
            if (lock != null) {
                if (lock == QUOTE) {
                    if (ch != '"' && ch != '\\') {
                        charStack.push(ch);
                        continue;
                    }
                }

                if (lock == SQUARE) {
                    if (ch != '^' && ch != '\\' && ch != ']') {
                        charStack.push(ch);
                        continue;
                    }
                }
                if (lock == NOT) {
                    if (ch != '\\' && ch != ']') {
                        charStack.push(ch);
                        continue;
                    }
                }
            }
            switch (ch) {
                case '\\':
                    i++;
                    if (lock != null) {
                        if (regExp.charAt(i) == 'r')
                            charStack.push('\r');
                        if (regExp.charAt(i) == 'n')
                            charStack.push('\n');
                        if (regExp.charAt(i) == 't')
                            charStack.push('\t');
                        charStack.push(regExp.charAt(i));
                    } else
                        nfaQueue.add(new NFA(regExp.charAt(i)));
                    break;
                case '[':
                    charStack.push(SQUARE);
                    lock = SQUARE;
                    break;
                case '^':
                    if (charStack.peek() == SQUARE) {
                        charStack.pop();
                        charStack.push(NOT);
                        lock = NOT;
                    } else
                        charStack.push(ch);
                    break;
                case ']':
                    Vector<Character> chs = new Vector<>();
                    if (charStack.search(SQUARE) == -1 && charStack.search(NOT) == -1)
                        throw new Exception("Lex syntax error - [] mismatch");
                    while (!charStack.peek().equals(lock)) {
                        Character topChar = charStack.pop();
                        if (charStack.peek().equals('-')) {
                            charStack.pop();
                            if (charStack.peek().equals(lock))
                                chs.add(topChar, '-');
                            else {
                                if (charStack.peek() > topChar)
                                    throw new Exception("Lex syntax error - ");
                                for (char j = charStack.pop(); j <= topChar; j++)
                                    chs.add(j);
                            }
                        } else
                            chs.add(topChar);
                    }
                    NFA nfa_g;
                    if (charStack.pop().equals(SQUARE)) nfa_g = square(chs);
                    else nfa_g = not(chs);
                    nfaQueue.add(nfa_g);
                    lock = null;
                    break;
                case '"':
                    if (charStack.search(QUOTE) != -1) {
                        NFA nfa_s = new NFA();
                        while (!charStack.peek().equals(QUOTE)) {
                            nfa_s = concat(new NFA(charStack.pop()), nfa_s);
                        }
                        charStack.pop();
                        nfaQueue.add(nfa_s);
                        lock = null;
                    } else {
                        charStack.push(QUOTE);
                        lock = QUOTE;
                    }
                    break;
                case '.':
                    nfaQueue.add(dot());
                    break;
                default:
                    if (priorityTable.containsKey(ch))
                        nfaQueue.add(ch);
                    else
                        nfaQueue.add(new NFA(ch));
                    break;
            }
        }
        return nfaQueue;
    }

    /**
     * add operator . for a vector of NFA
     *
     * @param nfaQueue NFAs generated by function regExpToSubNFAs
     */
    private static void addConcatForNFAs(Vector<Object> nfaQueue) {
        boolean flag = false;
        for (int i = 0; i < nfaQueue.size(); i++) {
            if (nfaQueue.get(i).getClass().equals(NFA.class)) {
                if (flag)
                    nfaQueue.insertElementAt(CONCAT, i++);
                flag = true;
            } else {
                Character op = (Character) nfaQueue.get(i);
                if (flag && op.equals('('))
                    nfaQueue.insertElementAt(CONCAT, i++);
                flag = op.equals('*') || op.equals('?') || op.equals('+') || op.equals(')');
            }
        }
    }

    /**
     * Init a state row with no transition.
     *
     * @return A state row.
     */
    public static Vector<HashSet<Integer>> initStateRow() {
        Vector<HashSet<Integer>> stateRow = new Vector<>();
        for (int i = 0; i < COLUMNS; i++) stateRow.add(new HashSet<>());
        return stateRow;
    }

    /**
     * Init a state row with a char transit to another state.
     *
     * @param ch         char.
     * @param transition Another state.
     * @return A state row.
     */
    public static Vector<HashSet<Integer>> initStateRow(char ch, int transition) {
        Vector<HashSet<Integer>> stateRow = initStateRow();
        addTransition(stateRow, ch, transition);
        return stateRow;
    }

    /**
     * Init a state row with a char transit to other states.
     *
     * @param ch          char.
     * @param transitions Other states.
     * @return A state row.
     */
    public static Vector<HashSet<Integer>> initStateRow(char ch, HashSet<Integer> transitions) {
        Vector<HashSet<Integer>> stateRow = initStateRow();
        addTransition(stateRow, ch, transitions);
        return stateRow;
    }

    /**
     * Init a state row transit to another state with chars.
     *
     * @param chars      chars.
     * @param transition Another state.
     * @return A state row.
     */
    public static Vector<HashSet<Integer>> initStateRow(Vector<Character> chars, int transition) {
        Vector<HashSet<Integer>> stateRow = initStateRow();
        addTransition(stateRow, chars, transition);
        return stateRow;
    }

    public static void addTransition(Vector<HashSet<Integer>> stateRow, char ch, int transition) {
        stateRow.elementAt(ch).add(transition);
    }

    public static void addTransition(Vector<HashSet<Integer>> stateRow, char ch, HashSet<Integer> transitions) {
        stateRow.elementAt(ch).addAll(transitions);
    }

    public static void addTransition(Vector<HashSet<Integer>> stateRow, Vector<Character> chars, int transition) {
        for (char ch : chars) addTransition(stateRow, ch, transition);
    }

    /**
     * Concatenate NFAs all together.
     *
     * @param nfas A set of NFAs.
     * @return NFA after concatenate.
     */
    public static NFA concat(NFA... nfas) {
        NFA result = new NFA();
        for (NFA nfa : nfas) {
            result.transitionTable.removeElementAt(result.accept);
            result.transitionTable.addAll(nfa.increasedStateNumber(result.accept));
            result.accept += nfa.accept;
        }
        return result;
    }

    /**
     * Parallel connect NFAs all together.
     *
     * @param nfas A set of NFAs.
     * @return NFA after parallel connection.
     */
    public static NFA or(NFA... nfas) {
        NFA result = new NFA();
        HashSet<Integer> starts = new HashSet<>();
        HashSet<Integer> accepts = new HashSet<>();
        int nextState = 1;
        for (NFA nfa : nfas) {
            starts.add(nextState);
            result.transitionTable.addAll(nfa.increasedStateNumber(nextState));
            accepts.add(nextState + nfa.accept);
            nextState += nfa.accept + 1;
        }
        addTransition(result.transitionTable.firstElement(), EPSILON, starts);
        result.transitionTable.add(initStateRow());
        result.accept = nextState;
        for (Integer state : accepts) {
            addTransition(result.transitionTable.elementAt(state), EPSILON, result.accept);
        }
        return result;
    }

    /**
     * Operation star(*).
     *
     * @param nfa NFA.
     * @return NFA after star.
     */
    public static NFA star(NFA nfa) {
        NFA result = new NFA();
        result.transitionTable.addAll(nfa.increasedStateNumber(1));
        result.transitionTable.add(initStateRow());
        result.accept += nfa.accept + 2;
        HashSet<Integer> startState = new HashSet<>();
        startState.add(1);
        startState.add(result.accept);
        addTransition(result.transitionTable.firstElement(), EPSILON, startState);
        HashSet<Integer> lastState = new HashSet<>();
        lastState.add(1);
        lastState.add(result.accept);
        addTransition(result.transitionTable.elementAt(result.accept - 1), EPSILON, lastState);
        return result;
    }

    /**
     * Operation plus(+).
     *
     * @param nfa NFA.
     * @return NFA after plus.
     */
    public static NFA plus(NFA nfa) {
        return concat(nfa, star(nfa));
    }

    /**
     * Operation dot(.).
     *
     * @return NFA with a dot.
     */
    public static NFA dot() {
        NFA result = new NFA();
        for (int i = 0; i < COLUMNS - 1; i++) result.addTransition(0, (char) i, 1);
        Vector<HashSet<Integer>> acceptStateRow = initStateRow();
        result.transitionTable.add(acceptStateRow);
        result.accept = 1;
        return result;
    }

    /**
     * Operation dash(from-to).
     *
     * @param from Left char.
     * @param to   Right char.
     * @return NFA with a dash.
     */
    public static NFA dash(char from, char to) throws Exception {
        return new NFA(from, to);
    }

    /**
     * Operation square brackets([]).
     *
     * @param chars A vector of characters brackets include in.
     * @return NFA with square brackets.
     */
    public static NFA square(Vector<Character> chars) {
        NFA result = new NFA();
        result.addTransition(0, chars, 1);
        Vector<HashSet<Integer>> acceptStateRow = initStateRow();
        result.transitionTable.add(acceptStateRow);
        result.accept = 1;
        return result;
    }

    /**
     * Operation not([^]).
     *
     * @param chars A vector of characters brackets include in.
     * @return NFA without transition of given characters.
     */
    public static NFA not(Vector<Character> chars) {
        NFA result = new NFA();
        for (char i = 0; i < COLUMNS - 1; i++)
            if (!chars.contains(i))
                result.addTransition(0, i, 1);
        Vector<HashSet<Integer>> acceptStateRow = initStateRow();
        result.transitionTable.add(acceptStateRow);
        result.accept = 1;
        return result;
    }

    /**
     * Repeat NFA for times.
     *
     * @param nfa NFA.
     * @param min Minimum repeat time.
     * @param max Maximum repeat time.
     * @return Repeated NFA.
     */
    public static NFA repeat(NFA nfa, int min, int max) throws Exception {
        if (max >= min) {
            NFA result = new NFA();
            for (int i = 0; i < max; i++) result = concat(result, nfa);
            result.transitionTable.add(initStateRow());
            result.accept = nfa.accept * max + 1;
            for (int i = min; i <= max; i++) {
                result.addTransition(i * nfa.accept, EPSILON, result.accept);
            }
            return result;
        } else throw new Exception("Lex syntax error - In {x,y} x must be small than y");
    }

    /**
     * Operation question mark(?).
     *
     * @param nfa NFA.
     * @return NFA with a question mark.
     */
    public static NFA question(NFA nfa) {
        NFA result = new NFA();
        result.transitionTable.addAll(nfa.increasedStateNumber(1));
        result.transitionTable.add(initStateRow());
        result.accept += nfa.accept + 2;
        HashSet<Integer> startState = new HashSet<>();
        startState.add(1);
        startState.add(result.accept);
        addTransition(result.transitionTable.firstElement(), EPSILON, startState);
        addTransition(result.transitionTable.elementAt(result.accept - 1), EPSILON, result.accept);
        return result;
    }

    public static String transitionTableDebugMessage(Vector<Vector<HashSet<Integer>>> transitionTable) {
        return transitionTableDebugMessage(transitionTable, false);
    }

    public static String transitionTableDebugMessage(Vector<Vector<HashSet<Integer>>> transitionTable, boolean verbose) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < transitionTable.size(); i++) {
            builder.append(i).append(": ");
            for (int ch = 0; ch < COLUMNS; ch++) {
                if (!verbose)
                    if (transitionTable.elementAt(i).elementAt(ch).isEmpty())
                        continue;
                if (ch >= 32 && ch <= 126) {
                    builder.append('\'').append((char) ch).append('\'');
                } else if (ch == 128) {
                    builder.append("\'ε\'");
                } else {
                    builder.append(ch);
                }
                builder.append("[");
                boolean first = true;
                for (Integer transition : transitionTable.elementAt(i).elementAt(ch)) {
                    if (!first) builder.append(',');
                    builder.append(transition);
                    first = false;
                }
                builder.append("] ");
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
