package seu.dfa;

import seu.nfa.IntegratedNFA;
import seu.nfa.NFA;
import seu.nfa.NFAUtil;

import java.util.*;

public class DFA {

    /* The transition table of the DFA */
    public Vector<Vector<Integer>> transitionTable = new Vector<>();
    /* State to start */
    public int start = 0;
    /* State to accept and action */
    public HashMap<Integer, String> acceptAction = new HashMap<>();
    /* The closure table of dfa */
    private Vector<TreeSet<Integer>> closureTable = new Vector<>();
    /* The Integrated NFA */
    private IntegratedNFA nfa;

    public DFA(IntegratedNFA nfa) {
        this.nfa = nfa;

        closureTable.add(getClosuresOfStart());
        while (closureTable.size() > transitionTable.size())
            fillRowOfTransitionTable(transitionTable.size());

        fillActionTable();
    }

    public DFA(Vector<Vector<Integer>> transitionTable, HashMap<Integer, String> acceptAction, int start) {
        this.transitionTable = transitionTable;
        this.acceptAction = acceptAction;
        this.start = start;
    }

    /**
     * Get a closure of states which a NFA state can reach by 'ε's
     *
     * @return output states
     */
    private TreeSet<Integer> getClosuresOfStart() {
        TreeSet<Integer> startSet = new TreeSet<>();
        startSet.add(NFA.start);
        return getClosures(startSet);
    }

    /**
     * Get a closure of states which some NFA states can reach by 'ε's
     *
     * @param statesOfNFA some input state
     * @return output states
     */
    private TreeSet<Integer> getClosures(TreeSet<Integer> statesOfNFA) {
        Stack<Integer> unCalcStates = new Stack<>();
        TreeSet<Integer> result = new TreeSet<>(statesOfNFA);
        unCalcStates.addAll(statesOfNFA);
        while (!unCalcStates.empty()) {
            int unCalcState = unCalcStates.pop();
            TreeSet<Integer> closureOfUnCalcState = new TreeSet<>(nfa.transitionTable.elementAt(unCalcState).elementAt(NFAUtil.EPSILON));
            for (Integer state : closureOfUnCalcState) {
                if (!unCalcStates.contains(state)) {
                    result.add(state);
                    unCalcStates.add(state);
                }
            }
        }
        return result;
    }

    /**
     * Get a closure of states which some NFA states can reach by a character and any 'ε's
     *
     * @param statesOfNFA input states
     * @param ch          a character
     * @return output states
     */
    private TreeSet<Integer> move(TreeSet<Integer> statesOfNFA, Character ch) {
        TreeSet<Integer> out = new TreeSet<>();
        for (Integer stateOfNFA : statesOfNFA)
            out.addAll(nfa.transitionTable.elementAt(stateOfNFA).elementAt(ch));
        return getClosures(out);
    }

    /**
     * Fill a row of the transition table
     *
     * @param stateOfDFA a DFA state whose row in transition table needs to be added
     */
    private void fillRowOfTransitionTable(int stateOfDFA) {
        Vector<Integer> rowOfTransitionTable = new Vector<>();

        for (char i = 0; i < DFAUtil.COLUMNS; i++) {
            TreeSet<Integer> dfaState = new TreeSet<>(move(closureTable.get(stateOfDFA), i));
            if (dfaState.isEmpty()) {
                rowOfTransitionTable.add(null);
                continue;
            }
            // dfaState 是用来存经过i变换之后的，之后调用函数加上闭包整个的集合
            // 集合与之前的进行比较判断是否是新状态
            if (!closureTable.contains(dfaState)) {
                rowOfTransitionTable.add(closureTable.size());
                closureTable.add(dfaState);
            } else
                rowOfTransitionTable.add(closureTable.indexOf(dfaState));
        }
        transitionTable.add(rowOfTransitionTable);
    }

    /**
     * Fill action table from NFA to DFA by connection of closureTable,
     * when a DFA state refers to more than one NFA accept state, take the one who come first.
     */
    private void fillActionTable() {
        for (int i = 0; i < closureTable.size(); i++) {
            TreeSet<Integer> dfaStates = new TreeSet<>(closureTable.get(i));
            for (Integer state : dfaStates) {
                if (nfa.accept.get(state) != null) {
                    acceptAction.put(i, nfa.accept.get(state));
                    break;
                }
            }
        }
    }

    /**
     * Get a table of DFA transition table.
     *
     * @return String of the message.
     */
    public String debugMessage() {
        return DFAUtil.transitionTableDebugMessage(transitionTable)
                + "start : " + start + '\n'
                + "accept: " + acceptAction.toString() + '\n';
    }

}
