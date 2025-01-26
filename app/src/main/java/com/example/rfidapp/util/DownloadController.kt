package com.example.rfidapp.util

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.rfidapp.BuildConfig
import com.example.rfidapp.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.Locale
import java.util.TreeMap
import java.util.concurrent.Executors

fun downloadPDF(
    activity: Activity,
    fileUrl: String,
    mFileName: String? = null,
    isDownload: Boolean = false,
    onSuccessListener: (String) -> Unit,
    onFailureListener: (String) -> Unit,
) {
        if (fileUrl.isEmpty()) {
            onFailureListener.invoke("File URL cannot be empty")
            return
        }
        if (fileUrl.isValidUrl().not()) {
            onFailureListener.invoke("File URL is not in the proper format")
            return
        }
        val executor = Executors.newFixedThreadPool(1)
        val progressDialog = ProgressDialog(activity)
        progressDialog.max = 100
        progressDialog.setTitle(if (isDownload) "Downloading...." else "Opening....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setProgressNumberFormat("100")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        executor.execute {
          val dir = if(isDownload)
              File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"myWorkApp")
          else File(activity.cacheDir, "cache")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val url = URL(fileUrl)
            val fileName = mFileName ?: url.path.substring(url.path.lastIndexOf('/') + 1)

            val filepath = dir.absolutePath + File.separator + fileName
            val file = File(filepath)
            file.createNewFile()
            try {
                url.openStream().use { inp ->
                    BufferedInputStream(inp).use { bis ->
                        FileOutputStream(filepath).use { fos ->
                            val fileLength = url.openConnection().contentLength
                            val data = ByteArray(1024)
                            var count: Int
                            var uploaded: Long = 0
                            while (bis.read(data, 0, 1024).also { count = it } != -1) {
                                uploaded += count.toLong()
                                fos.write(data, 0, count)
                                Handler(Looper.getMainLooper()).post {
                                    progressDialog.progress =
                                        (100 * uploaded / fileLength).toInt()
                                }
                            }
                            onSuccessListener.invoke(filepath)
                            Handler(Looper.getMainLooper()).post {
                                progressDialog.cancel()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                onFailureListener.invoke(e.message?:"")
                Handler(Looper.getMainLooper()).post {
                    progressDialog.cancel()
                }
            }
        }
    }

fun String.isValidUrl(): Boolean {
    val urlPattern = Regex("""https?://\S+""")
    return urlPattern.matches(this)
}

fun Activity.openDocument(localImgPath: String) {
    val file = File(localImgPath)
    if (file.exists()) {
        val fileUri = getFileUri(this, localImgPath)
        val contentType = getContentType(file)
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, contentType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                this.getString(R.string.action_not_supported),
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "application/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.startActivity(intent)
        }
    } else {
        Toast.makeText(this, "File download error. Please download again", Toast.LENGTH_LONG).show()
    }
}

fun getFileUri(context: Context?, path: String?): Uri {
    val fileUri: Uri
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        fileUri = Uri.fromFile(File(path))
    } else {
        val authority: String = BuildConfig.APPLICATION_ID + ".provider"
        fileUri = FileProvider.getUriForFile(context!!, authority, File(path))
    }
    return fileUri
}

fun getContentType(file: File): String? {
    val contentTypeMap = TreeMap<String?, String>()
    contentTypeMap["xlsm"] = "application/vnd.ms-excel.sheet.macroEnabled.12"
    contentTypeMap["xlsb"] = "application/vnd.ms-excel.sheet.binary.macroEnabled.12"
    var contentType: String? = null
    val map = MimeTypeMap.getSingleton()
    val index = file.name.lastIndexOf('.') + 1
    val ext = if (index > 0) file.name.substring(index).lowercase(Locale.getDefault()) else ""
    if (ext != null && !ext.isEmpty()) contentType = map.getMimeTypeFromExtension(ext)
    if (contentType == null) {
        contentType = if (contentTypeMap.containsKey(ext)) contentTypeMap[ext]
        else "*/*"
    }
    return contentType
}