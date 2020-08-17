package ru.spbstu

import org.kohsuke.args4j.*
import java.io.File

fun main(args: Array<String>) {
    LsLauncher().launch(args)
}

class LsLauncher {
    @Option(name = "-l", metaVar = "Long", required = false, usage = "Switches output to long format")
    private var isLong: Boolean = false

    @Option(name = "-h", metaVar = "HumanReadable", required = false, usage = "Switches output to humanreadable format")
    private var isHumanReadable: Boolean = false

    @Option(name = "-r", metaVar = "Reverse", required = false, usage = "Reverses output")
    private var isReversed: Boolean = false

    @Option(name = "-o", metaVar = "Output", required = false, usage = "Is output file needed")
    private var out: File? = null

    @Argument(required = true, metaVar = "Directory path", usage = "Analysed directory")
    private lateinit var path: File

    fun launch(args: Array<String>) {
        val parser = CmdLineParser(this)
        try {
            parser.parseArgument(*args)
        } catch (e: CmdLineException) {
            println(e.message)
            parser.printUsage(System.out)
        }
        Ls().work(isLong, isHumanReadable, isReversed, out, path)
    }
}
