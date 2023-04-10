package com.example.taller2_santiagoaviles


import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.CursorAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.taller2_santiagoaviles.adaptadores.ContactAdapter
import com.example.taller2_santiagoaviles.databinding.ActivityContactsBinding



class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private val projection = arrayOf(ContactsContract.Profile._ID,ContactsContract.Profile.DISPLAY_NAME_PRIMARY)
    private lateinit var cursor: Cursor
    private lateinit var adapter: CursorAdapter
    private var contactPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback {
            granted : Boolean ->
            Log.i("Permisos",granted.toString())
            GestionView()
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ContactAdapter(this,null,0) //nulo hasta que que el permiso sea dado
        binding.ContactList.adapter = adapter
    }
    override fun onStart(){
        super.onStart()
        GestionPermiso()
    }
    fun GestionPermiso(){
        if(ContextCompat.checkSelfPermission(baseContext, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS)){
                Toast.makeText(baseContext,"App requiere contactos para visualizar la lista de contactos",Toast.LENGTH_SHORT).show()
            }
            contactPermission.launch(android.Manifest.permission.READ_CONTACTS)
        }
        else{
            GestionView()
        }
    }

    fun GestionView(){
        if(ContextCompat.checkSelfPermission(baseContext,android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection,null,null,null)!!
            adapter.changeCursor(cursor)
        }
        else{
            startActivity(Intent(baseContext,MainActivity::class.java))
        }
    }
}
