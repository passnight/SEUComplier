package seu

import seu.io.CodeFile
import seu.io.YaccFile
import seu.lr.LR

fun main(args: Array<String>) {
    val inputFilePath = "C:\\Project\\SEUCompiler\\Yacc\\src\\main\\resources\\example2.y"
    val outputFilePath = "C:\\Project\\SEUCompiler\\Yacc\\src\\main\\resources\\example2.cpp"
    val yaccFile = YaccFile(inputFilePath)
    val lr = LR(yaccFile.rules.toList(), yaccFile.start)
    val codeFile = CodeFile(yaccFile, lr)
    codeFile.writeFile(outputFilePath)
}
