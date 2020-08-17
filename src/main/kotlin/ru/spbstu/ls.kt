package ru.spbstu

import java.io.File
import java.util.*

class Ls {
    private fun getBinMask(file: File): String {
        return (if (file.canRead()) "1" else "0") +
                (if (file.canWrite()) "1" else "0") +
                (if (file.canExecute()) "1" else "0")
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

    fun work(isLong: Boolean, isHumanReadable: Boolean, isReversed: Boolean, out: File?, path: File) {
        val files = when {
            path.isDirectory -> path.listFiles()!!.asList()
            path.isFile -> listOf(path)
            else -> throw IllegalArgumentException("directory or file doesn't exists")
        }.toMutableList()
        if (files.isEmpty()) {
            println("directory is empty")
            return
        }
        val longestName = files.maxBy { it.name }!!.name.length + 5
        if (isReversed)
            files.sortByDescending { it.name }
        else
            files.sortBy { it.name }
        val result = if (isLong) {
            if (isHumanReadable)
                files.map {
                    it.name + "%${longestName - it.name.length}s".format(getRwxMask(it)) +
                            "  ${Date(it.lastModified())}  ${convertSize(it.length())}"
                }
            else
                files.map { it.name + "%${longestName - it.name.length}s".format(getBinMask(it)) + "  ${it.lastModified()}  ${it.length()}B" }
        } else {
            files.map { it.name }.sorted()
        }
        if (out != null) {
            val outputStream = out.bufferedWriter()
            outputStream.write(result.joinToString("\n"))
            outputStream.close()
        }
        println(result.joinToString("\n"))
    }
}