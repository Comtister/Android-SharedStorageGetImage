package org.oguzhanozturk.filekt

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.provider.MediaStore
import android.provider.UserDictionary.Words._ID
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    val imageList = mutableListOf<Foto>()

    lateinit var imageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)



    }




    private fun process(){

        val collection =
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                }else{
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

        val projection = arrayOf(MediaStore.Images.Media._ID,MediaStore.Images.Media.SIZE)

        val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

        val query = contentResolver.query(collection,projection,null,null,sortOrder)

        query.use { cursor ->

            val idColumn = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val sizeColumn = cursor!!.getColumnIndex(MediaStore.Images.Media.SIZE)

            while (cursor!!.moveToNext()){
                val id = cursor.getLong(idColumn)
                val size = cursor.getInt(sizeColumn)
                Log.i("xxx","Whileda")
                val contentUri : Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

                imageList += Foto(contentUri,size)
                Log.i("ZZZ",size.toString())
            }


        }


        contentResolver.openInputStream(imageList.get(0).uri).use {stream ->
            Log.i("Thread",Thread.currentThread().name)
            val bitmap = BitmapFactory.decodeStream(stream)
            imageView.setImageBitmap(bitmap)
        }

        //val thumb : Bitmap = contentResolver.loadThumbnail(imageList[0].uri, Size(640,480),null);




        print(imageList.get(0))

    }


    fun set(view : View){
        CheckPermission()
    }

    private fun CheckPermission() {

        when{

            ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    //Process
                Log.i("asd","asdas")
                process()
            } else -> {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),0)
        }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 0 && permissions.isNotEmpty() && grantResults.isNotEmpty()){
            //process
            Log.i("dada","dada")
            process()
        }

    }

}