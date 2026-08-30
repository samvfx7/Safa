package com.example.audio

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object QuranAudioDownloader {

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap()) // Key: "reciterId_surahNumber", Value: progress float (0.0f to 1.0f)
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress

    fun getDownloadProgress(reciterId: String, surahNumber: Int): Float? {
        return _downloadProgress.value["${reciterId}_$surahNumber"]
    }

    fun isDownloaded(context: Context, reciterId: String, surahNumber: Int): Boolean {
        val file = File(getAudioDir(context), "${reciterId}_$surahNumber.mp3")
        return file.exists() && file.length() > 50000 // Ensure it's larger than 50KB to avoid incomplete files
    }

    fun getAudioFile(context: Context, reciterId: String, surahNumber: Int): File? {
        val file = File(getAudioDir(context), "${reciterId}_$surahNumber.mp3")
        return if (file.exists() && file.length() > 50000) file else null
    }

    private fun getAudioDir(context: Context): File {
        val dir = File(context.filesDir, "quran_audio")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    suspend fun downloadSurahAudio(
        context: Context,
        reciter: QariReciter,
        surahNumber: Int
    ): Result<File> = withContext(Dispatchers.IO) {
        val key = "${reciter.id}_$surahNumber"
        try {
            val urlString = reciter.getFullSurahAudioUrl(surahNumber)
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("Server returned HTTP ${connection.responseCode}"))
            }

            val fileLength = connection.contentLength
            val audioDir = getAudioDir(context)
            val tempFile = File(audioDir, "${key}.tmp")
            val targetFile = File(audioDir, "${key}.mp3")

            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(tempFile)

            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int

            while (inputStream.read(data).also { count = it } != -1) {
                total += count
                if (fileLength > 0) {
                    val progress = total.toFloat() / fileLength
                    _downloadProgress.value = _downloadProgress.value.toMutableMap().apply {
                        put(key, progress)
                    }
                }
                outputStream.write(data, 0, count)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Rename temp to target
            if (tempFile.exists()) {
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                tempFile.renameTo(targetFile)
            }

            _downloadProgress.value = _downloadProgress.value.toMutableMap().apply {
                remove(key)
            }

            Result.success(targetFile)
        } catch (e: Exception) {
            val file = File(getAudioDir(context), "${key}.tmp")
            if (file.exists()) file.delete()
            _downloadProgress.value = _downloadProgress.value.toMutableMap().apply {
                remove(key)
            }
            Result.failure(e)
        }
    }

    fun deleteDownloadedAudio(context: Context, reciterId: String, surahNumber: Int): Boolean {
        val file = File(getAudioDir(context), "${reciterId}_$surahNumber.mp3")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}
