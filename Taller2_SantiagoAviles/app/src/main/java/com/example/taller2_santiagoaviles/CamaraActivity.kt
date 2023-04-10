package com.example.taller2_santiagoaviles

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.taller2_santiagoaviles.databinding.ActivityCamaraBinding
import java.io.File

class CamaraActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCamaraBinding
    private lateinit var uriCamara : Uri

    //Requests a permisos
    private val galleryRequest = registerForActivityResult(ActivityResultContracts.GetContent(),
    ActivityResultCallback {
        result : Uri? -> loadImage(result)
    })
    private val camaraRequest = registerForActivityResult(ActivityResultContracts.TakePicture(),
        ActivityResultCallback { loadImage(uriCamara) }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamaraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        InitializeFileC()

        binding.galeria.setOnClickListener{
            galleryRequest.launch("image/*")
        }
        binding.camara.setOnClickListener {
            camaraRequest.launch(uriCamara)

        }
    }

    private fun loadImage(result : Uri?){
        val Stream = contentResolver.openInputStream(result!!)
        val Imagen = BitmapFactory.decodeStream(Stream)
        binding.imagen.setImageBitmap(Imagen)
    }

    private fun InitializeFileC(){
        val file = File(filesDir, "CamaraPic")
        uriCamara = FileProvider.getUriForFile(this,applicationContext.packageName+".fileprovider",file)
    }
}