package com.example.taller_3v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.taller_3v2.databinding.ActivityRegistroBinding;
import com.example.taller_3v2.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegistroActivity extends AppCompatActivity {
    private ActivityRegistroBinding binding;
    private FirebaseAuth mAuth;
    public static final String PATH_USERS = "users/";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    Usuario nUser = new Usuario();
    String msg;
    Usuario prueba = new Usuario();

    int SELECT_PICTURE = 200;
    int CAMERA_REQUEST = 100;

    String fotoS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();





        binding.fotodeperfil.setOnClickListener(v -> {

            final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
            AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
            builder.setTitle("Elige una opcion");
            builder.setItems(options, (dialog, item) -> {
                if (options[item].equals("Tomar foto")) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegistroActivity.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        if (checkAndRequestPermissions()) {
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_REQUEST);
                            binding.fotodeperfil.setImageURI(Uri.parse(android.provider.MediaStore.ACTION_IMAGE_CAPTURE));
                        }
                    }
                } else if (options[item].equals("Elegir de galeria")) {
                    if (checkAndRequestPermissionsStorage()) {
                        imageChooser();
                    } else {
                        Toast.makeText(this, "No se puede acceder a la galeria", Toast.LENGTH_SHORT).show();
                    }
                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        binding.RegistrarpersonaBTN.setOnClickListener(view -> {
            String email = binding.editTextTextEmail.getText().toString();
            String password = binding.editTextTextContrasena.getText().toString();
            createFirebaseAuthUser(email, password);
        });
    }

    void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout


                    try {
                        Bitmap img = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        fotoS = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        Log.d("imagen", "onActivityResult: " + fotoS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.fotodeperfil.setImageURI(selectedImageUri);
                }
            }
        }

        if (requestCode == CAMERA_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            fotoS = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("imagen", "onActivityResult: " + fotoS);

            binding.fotodeperfil.setImageBitmap(image);
        }
    }


    private void createFirebaseAuthUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveUser();
            }
        });
    }



    private Usuario createUserObject() {

        nUser.setNombre(binding.editTextTextNombre.getText().toString());
        nUser.setApellido(binding.editTextTextApellido.getText().toString());
        nUser.setCorreo(binding.editTextTextEmail.getText().toString());
        nUser.setFotodeperfil(fotoS);
        nUser.setNumerodeidentificacion(binding.editTextNumerodeidentificacion.getText().toString());
        return nUser;
    }


    private void saveUser() {
        Usuario Client = createUserObject();
        myRef = database.getReference(PATH_USERS + mAuth.getCurrentUser().getUid());
        myRef.setValue(Client).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Toast.makeText(getApplicationContext(), "Usuario registrado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    private boolean checkAndRequestPermissionsStorage() {
        int permissionWritestorage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWritestorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}