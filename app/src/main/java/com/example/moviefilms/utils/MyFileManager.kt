package com.example.moviefilms.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class MyFileManager {
    private val TAG = "MyFileManager"


    fun saveImage(context: Context, bitmapImage: Bitmap): String{
        val FILE_NAME = System.currentTimeMillis().toString() + ".bmp"
        return try {
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).let { fos ->
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, fos)
                try{
                    fos.close()
                } catch (e: IOException){
                    e.printStackTrace()
                }
            }
            context.getFileStreamPath(FILE_NAME).toString()
        } catch (e: FileNotFoundException){
            e.printStackTrace()
            ""
        }
    }

    fun deleteImageFromInternalStorage(context: Context, imageFileName: String): Boolean {
        val directory = context.filesDir
        val file = File(directory, imageFileName)
        return try {
            file.delete()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch(e: SecurityException ){
            e.printStackTrace()
            false
        }
    }
}