package university.hits.tsugeo.core.domain.objects

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

object FileManager {
    fun getFile(
        context: Context,
        fileName: String,
        dirName: String = ""
    ): File {
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }
    fun saveBytes(
        context: Context,
        bytes: ByteArray,
        fileName: String,
        dirName: String = ""
    ): File {
        val file = getFile(context, fileName, dirName)
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    fun saveText(
        context: Context,
        text: String,
        fileName: String,
        dirName: String = "",
        charset: Charset = Charsets.UTF_8
    ): File {
        return saveBytes(
            context,
            text.toByteArray(charset),
            fileName,
            dirName
        )
    }

    fun readBytes(
        context: Context,
        fileName: String,
        dirName: String = ""
    ): ByteArray {
        val file = getFile(context, fileName, dirName)
        if (!file.exists()) {
            throw IllegalArgumentException("File does not exist: ${file.absolutePath}")
        }
        return FileInputStream(file).use { it.readBytes() }
    }

    fun readText(
        context: Context,
        fileName: String,
        dirName: String = "",
        charset: Charset = Charsets.UTF_8

    ): String {
        return readBytes(context, fileName, dirName).toString(charset)
    }

    fun exists(
        context: Context,
        fileName: String,
        dirName: String = ""
    ): Boolean {
        return getFile(context, fileName, dirName).exists()
    }

    fun delete(
        context: Context,
        fileName: String,
        dirName: String = ""
    ): Boolean {
        return getFile(context, fileName, dirName).deleteRecursively()
    }

    fun listFiles(
        context: Context,
        dirName: String = ""
    ): List<File> {
        val dir = getFile(context, dirName)
        return dir.listFiles()?.toList().orEmpty()
    }

    fun clearAll(context: Context): Boolean {
        return listFiles(context).all { it.deleteRecursively() }
    }
}