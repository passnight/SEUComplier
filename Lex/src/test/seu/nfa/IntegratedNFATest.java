package seu.nfa;

import org.junit.Ignore;
import org.junit.Test;
import seu.nfa.IntegratedNFA;
import seu.nfa.NFA;
import seu.nfa.NFAUtil;

public class IntegratedNFATest {

    @Test
    @Ignore
    public void integrate() {
        IntegratedNFA integratedNFA = new IntegratedNFA();
        integratedNFA.integrate(NFAUtil.concat(new NFA('i'), new NFA('f')), "{Action A}");
        integratedNFA.integrate(NFAUtil.concat(new NFA('i'), NFAUtil.star(new NFA('f'))), "{Action B}");
        System.out.println(integratedNFA.debugMessage());
    }
}