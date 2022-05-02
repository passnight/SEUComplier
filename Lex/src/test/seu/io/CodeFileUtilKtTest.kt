package seu.io

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import seu.dfa.DFAUtil
import seu.nfa.IntegratedNFA
import seu.nfa.NFA
import seu.nfa.NFAUtil
import java.util.*

class CodeFileUtilKtTest {

    companion object {
        lateinit var codeFileUtil: CodeFileUtil
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val nfas = Vector<NFA>()
            nfas.add(NFAUtil.regExpToNFA("i*f"))
            nfas.add(NFAUtil.regExpToNFA("if*"))
            val infa = IntegratedNFA()
            infa.integrate(nfas[0], "cout << yytext << \" i*f\" << endl;")
            infa.integrate(nfas[1], "cout << yytext << \" if*\" << endl;")
            val dfa = DFAUtil.integratedNFAtoDFA(infa)
            codeFileUtil = CodeFileUtil(dfa)
        }
    }

    @Test
    @Ignore
    fun acceptFunctionsTest() {
        print(codeFileUtil.acceptFunctions())
    }

    @Test
    @Ignore
    fun transitionTableTest() {
        print(codeFileUtil.transitionTable())
    }

    @Test
    @Ignore
    fun actionMapTest() {
        print(codeFileUtil.actionMap())
    }

    @Test
    @Ignore
    fun generateTest() {
        print(codeFileUtil.generate())
    }
}