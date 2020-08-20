package ru.spbstu

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.*

class LsTest {

    private fun getRandomString(length: Int): String {
        val alphabet = ('A'..'Z') + ('a'..'z')
        return (1..length).map { alphabet.random() }.joinToString("")
    }

    private fun sizeConvert(s: Long): String {
        var size: Double = s.toDouble()
        if (size < 1024)
            return (round(size * 1000) / 1000).toString() + "B"
        size /= 1024
        if (size < 1024)
            return (round(size * 1000) / 1000).toString() + "KB"
        size /= 1024
        if (size < 1024)
            return (round(size * 1000) / 1000).toString() + "MB"
        size /= 1024
        if (size < 1024)
            return (round(size * 1000) / 1000).toString() + "GB"
        size /= 1024
        return (round(size * 1000) / 1000).toString() + "TB"
    }

    @Test
    fun test1() {
        val n = (0..100).random()
        val files = mutableListOf<File>()
        var longest = 0
        for (i in 0..n) {
            val name = "src/test/kotlin/ru/spbstu/ee/" + getRandomString((1..20).random()) + "." + getRandomString(3)
            longest = max(longest, name.length - 29)
            val file = File(name)
            files.add(file)
            val outputStream = file.bufferedWriter()
            for (j in 0..((0..1000000).random()))
                outputStream.write("dqwdccwqgrceee")    // filling the file with trash to increase file size
            outputStream.close()
        }
        Ls().work(
            isLong = true,
            isHumanReadable = true,
            isReversed = false,
            out = File("src/test/kotlin/ru/spbstu/result.txt"),
            path = File("src/test/kotlin/ru/spbstu/ee/")
        )
        files.sortBy { it.name }
        val inputStream = File("src/test/kotlin/ru/spbstu/result.txt").bufferedReader()
        try {
            for (i in 0 until files.size) {
                var string = files[i].name
                string += " ".repeat(longest - files[i].name.length + 2)
                string += if (files[i].canRead()) "r" else ""
                string += if (files[i].canWrite()) "w" else ""
                string += if (files[i].canExecute()) "x" else ""
                string += " ".repeat(string.length - (longest + 5))
                string += "  " + Date(files[i].lastModified())
                string += "  " + sizeConvert(files[i].length())
                assertEquals(string, inputStream.readLine())
            }
        } finally {
            for (i in files)
                i.delete()
        }
    }

    @Test
    fun test2() {
        if (File("src/test/kotlin/ru/spbstu/ee/").listFiles()!!.isEmpty()) {
            assertThrows(IllegalArgumentException::class.java) {
                Ls().work(
                    isLong = true,
                    isHumanReadable = true,
                    isReversed = false,
                    out = File("src/test/kotlin/ru/spbstu/result.txt"),
                    path = File("srveabtebavetbebebebe")
                )
            }
        }
    }
    @Test
    fun test3() {
        val n = (0..100).random()
        val files = mutableListOf<File>()
        for (i in 0..n) {
            val name = "src/test/kotlin/ru/spbstu/ee/" + getRandomString((1..20).random()) + "." + getRandomString(3)
            val file = File(name)
            files.add(file)
            val outputStream = file.bufferedWriter()
            for (j in 0..((0..1000000).random()))
                outputStream.write("dqwdccwqgrceee")    // filling the file with trash to increase file size
            outputStream.close()
        }
        Ls().work(
            isLong = false,
            isHumanReadable = false,
            isReversed = true,
            out = File("src/test/kotlin/ru/spbstu/result.txt"),
            path = File("src/test/kotlin/ru/spbstu/ee/")
        )
        files.sortByDescending { it.name }
        val inputStream = File("src/test/kotlin/ru/spbstu/result.txt").bufferedReader()
        try {
            for (i in 0 until files.size)
                assertEquals(files[i].name, inputStream.readLine())
        } finally {
            for (i in files)
                i.delete()
        }
    }
}