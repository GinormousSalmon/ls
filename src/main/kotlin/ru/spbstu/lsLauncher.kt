package ru.spbstu

import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.io.File
import java.util.*

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

    private fun getBinMask(file: File): String {
        return (if (file.canExecute()) "1" else "0") +
                (if (file.canRead()) "1" else "0") +
                (if (file.canWrite()) "1" else "0")
    }

    private fun getRwxMask(file: File): String {
        return "%3s".format(        //If one of the flags (r,w,x) is missing, output table will remain aligned
            (if (file.canExecute()) "x" else "") +
                    (if (file.canWrite()) "w" else "") +
                    (if (file.canRead()) "r" else "")
        ).reversed()
    }

    private fun convertSize(s: Long): String {
        val dim = listOf("B", "KB", "MB", "GB", "TB")
        var size: Double = s.toDouble()
        var count = 0
        while (size > 1023) {
            size /= 1024
            count++
        }
        return "%.3f".format(size) + dim[count]
    }

    fun launch(args: Array<String>) {
        val parser = CmdLineParser(this)
        try {
            parser.parseArgument(*args)
        } catch (e: CmdLineException) {
            println(e.message)
            parser.printUsage(System.out)
        }
        val files = when {
            path.isDirectory -> path.listFiles()!!.asList()
            path.isFile -> listOf(path)
            else -> throw IllegalArgumentException()
        }.toMutableList()
        val longestName = files.maxBy { it.name }!!.name.length + 5
        if (isReversed)
            files.sortBy { it.name }
        else
            files.sortByDescending { it.name }
        val result = if (isLong) {
            if (isHumanReadable)
                files.map {
                    it.name + "%${longestName - it.name.length}s".format(getRwxMask(it)) +
                            "  ${Date(it.lastModified())}  ${convertSize(it.length())}"
                }
            else
                files.map { "${it.name}, ${getBinMask(it)},  ${it.lastModified()},  ${it.length()}" }
        } else {
            files.map { it.name }.sorted()
        }
        if (out != null) {
            val outputStream = out!!.bufferedWriter()
            outputStream.write(result.joinToString("\n"))
            outputStream.close()
        }
        println(result.joinToString("\n"))
    }
}
