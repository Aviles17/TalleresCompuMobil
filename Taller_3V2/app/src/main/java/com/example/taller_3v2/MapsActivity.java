package com.example.taller_3v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.taller_3v2.databinding.ActivityMapsBinding;
import com.example.taller_3v2.modelo.Usuario;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference myRef;
    //Variables de permisos
    private final int LOCATION_PERMISSION_ID = 103;
    public static final int REQUEST_CHECK_SETTINGS = 201;
    String locationPerm = android.Manifest.permission.ACCESS_FINE_LOCATION;

    private final static float INITIAL_ZOOM_LEVEL = 14.5f;
    //Variables de localizacion
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    public Location mCurrentLocation;

    public static final String CHANNEL_ID = "MyApp";
    private final static int NOTIFICACION_ID = 0;
    private NotificationManagerCompat notificationManager;
    Marker mymarker;

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setmCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    @Override
    public void onBackPressed() {

    }

    public static final String PATH_USERS = "users/";
    Usuario Client = new Usuario();
    Usuario Client2 = new Usuario();

    private String siguiendoa;

    public String getSiguiendoa() {
        return siguiendoa;
    }

    public void setSiguiendoa(String siguiendoa) {
        this.siguiendoa = siguiendoa;
    }

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = 0;
    private double currentLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        crearNotificacionChannel();
        notificationManager = NotificationManagerCompat.from(this);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        binding.toolbarMapas.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logout) {
                try {
                    stopLocationUpdates();
                    mAuth.signOut();
                    stopLocationUpdates();
                    Thread.sleep(1000);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    finishActivity(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        });

        binding.PDisponiblesBTN.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ListaDisponiblesActivity.class));
            finish();
        });
        binding.toolbarMapas.getMenu().findItem(R.id.disponible).getActionView().findViewById(R.id.switch2).setOnClickListener(v -> {
            Log.d("Switch", "Switch" + ((Switch) v).isChecked());
            if (((Switch) v).isChecked()) {
                myRef = database.getReference(PATH_USERS + Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                myRef.getDatabase().getReference(PATH_USERS + mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Client = task.getResult().getValue(Usuario.class);
                        Client.setIsdisponible(true);
                        myRef.setValue(Client);

                    } else {
                        Toast.makeText(getApplicationContext(), "Error al poner la disponibilidad del perfil", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                myRef = database.getReference(PATH_USERS + mAuth.getCurrentUser().getUid());
                myRef.getDatabase().getReference(PATH_USERS + mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Client = task.getResult().getValue(Usuario.class);
                        Client.setIsdisponible(false);
                        myRef.setValue(Client);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al poner la disponibilidad del perfil", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        myRef = database.getReference(PATH_USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Usuario user = child.getValue(Usuario.class);
                    if (user != null) {
                        if (!child.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            if (user.isIsdisponible()) {
                                crearNotificacion(user);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Location

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private double currentLatitude = 0;
    private double currentLongitude = 0;

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        // UI controls

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (checkPermissions()) {
            mostrarpuntosjson();

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        if (locationResult == null) {
                            return;
                        }
                        //Showing the latitude, longitude and accuracy on the home screen.
                        for (Location location : locationResult.getLocations()) {

                            setmCurrentLocation(location);
                            mCurrentLocation = location;

                            currentLat = location.getLatitude();
                            currentLong = location.getLongitude();


                        }
                    }
                }
            };


            myRef = database.getReference(PATH_USERS + mAuth.getCurrentUser().getUid());
            myRef.getDatabase().getReference(PATH_USERS + mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Client = task.getResult().getValue(Usuario.class);
                    // Enable touch gestures


                    Location location = new Location("locationA");
                    location.setLatitude(currentLat);
                    location.setLongitude(currentLong);
                    setmCurrentLocation(location);
                    mCurrentLocation = location;
                    if (Client.getLatitud() != null && Client.getLongitud() != null) {
                        if (!Client.getLatitud().equals(currentLat) && !Client.getLongitud().equals(currentLong)) {
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
                            LatLng clatlng = new LatLng(currentLat, currentLong);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(clatlng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLat, currentLong)));
                        } else {
                            Client.setLatitud(currentLat);
                            Client.setLongitud(currentLong);
                            myRef.setValue(Client);
                        }
                    }

                    if (Client.getSiguiendoa() != null) {
                        if (!Client.getSiguiendoa().isEmpty()) {
                            setSiguiendoa(Client.getSiguiendoa());
                            siguiendoa = Client.getSiguiendoa();
                            myRef = database.getReference(PATH_USERS + getSiguiendoa());

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Client2 = snapshot.getValue(Usuario.class);
                                    if (Client2 != null) {
                                        LatLng sydney = new LatLng(Client2.getLatitud(), Client2.getLongitud());
                                        mMap.addMarker(new MarkerOptions().position(sydney).title(Client2.getNombre()));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                        mMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
                                        Location location = new Location("locationA");
                                        location.setLatitude(Client2.getLatitud());
                                        location.setLongitude(Client2.getLongitud());

                                        float distance = mCurrentLocation.distanceTo(location);
                                        Toast.makeText(getApplicationContext(), "Distancia: " + distance, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Error al poner la disponibilidad del perfil", Toast.LENGTH_SHORT).show();
                }
            });
            mMap.setMyLocationEnabled(true);
            mMap.isMyLocationEnabled();
            Location location2 = mMap.getMyLocation();
            if (location2 != null) {
                LatLng latLng = new LatLng(location2.getLatitude(), location2.getLatitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM_LEVEL));
            }
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        if (locationResult == null) {
                            return;
                        }
                        //Showing the latitude, longitude and accuracy on the home screen.
                        for (Location location : locationResult.getLocations()) {

                            setmCurrentLocation(location);
                            mCurrentLocation = location;

                            currentLat = location.getLatitude();
                            currentLong = location.getLongitude();

                            myRef = database.getReference(PATH_USERS + mAuth.getCurrentUser().getUid());
                            myRef.getDatabase().getReference(PATH_USERS + mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    Client = task.getResult().getValue(Usuario.class);
                                    if (Client.getLatitud() != null && Client.getLongitud() != null) {

                                        if (Client.getLatitud().equals(currentLat) && Client.getLongitud().equals(currentLong)) {
                                        } else {
                                            Location locationA = new Location("point A");
                                            locationA.setLatitude(mCurrentLocation.getLatitude());
                                            locationA.setLongitude(mCurrentLocation.getLongitude());
                                            Location locationB = new Location("point B");
                                            locationB.setLatitude(Client.getLatitud());
                                            locationB.setLongitude(Client.getLongitud());
                                            float distance = locationA.distanceTo(locationB);
                                            float distanceKm = distance / 1000;
                                            if (distanceKm > 0.01) {
                                                mMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
                                                // Enable touch gestures
                                                mMap.getUiSettings().setAllGesturesEnabled(true);
                                                // UI controls

                                                mMap.getUiSettings().setCompassEnabled(true);
                                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                                LatLng clatlng = new LatLng(location.getLatitude(), location.getLongitude());
                                                mMap.animateCamera(CameraUpdateFactory.newLatLng(clatlng));

                                                Client.setLatitud(currentLat);
                                                Client.setLongitud(currentLong);
                                                myRef.setValue(Client);
                                            }

                                        }
                                    } else {
                                        Client.setLatitud(currentLat);
                                        Client.setLongitud(currentLong);
                                        myRef.setValue(Client);
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al poner la disponibilidad del perfil", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            };

            if (getSiguiendoa() != null) {

                myRef = database.getReference(PATH_USERS + getSiguiendoa());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Client2 = snapshot.getValue(Usuario.class);
                        if (Client2 != null) {
                            LatLng sydney = new LatLng(Client2.getLatitud(), Client2.getLongitud());
                            mMap.addMarker(new MarkerOptions().position(sydney).title(Client2.getNombre()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
                            Location location = new Location("locationA");
                            location.setLatitude(Client2.getLatitud());
                            location.setLongitude(Client2.getLongitud());
                            float distance = mCurrentLocation.distanceTo(location);
                            Toast.makeText(getApplicationContext(), "Distancia: " + distance, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        startLocationUpdates();
    }


    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void mostrarpuntosjson() {
        try {
            InputStream is = getAssets().open("locations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject root = new JSONObject(json);

            JSONArray array = root.getJSONArray("locationsArray");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String nombre = jsonObject.getString("name");
                double latitud = jsonObject.getDouble("latitude");
                double longitud = jsonObject.getDouble("longitude");
                LatLng sydney = new LatLng(latitud, longitud);
                mMap.addMarker(new MarkerOptions().position(sydney).title(nombre));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public void startnotifications() {

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }


    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(3000)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    private void requestPermission(Activity context, String permiso, String justificacion,
                                   int idCode) {
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, justificacion, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Ya hay permiso para acceder a la localizacion", Toast.LENGTH_LONG).show();
                    turnOnLocationAndStartUpdates();
                } else {
                    Toast.makeText(this, "Permiso de ubicacion denegado", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void turnOnLocationAndStartUpdates() {
        mLocationRequest = createLocationRequest();
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task =
                client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> {

            mLocationCallback = new LocationCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        if (locationResult == null) {
                            return;
                        }
                        //Showing the latitude, longitude and accuracy on the home screen.
                        for (Location location : locationResult.getLocations()) {
                            mMap.setMyLocationEnabled(true);
                            mMap.isMyLocationEnabled();
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
                            // Enable touch gestures
                            mMap.getUiSettings().setAllGesturesEnabled(true);
                            // UI controls

                            mMap.getUiSettings().setCompassEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            LatLng clatlng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(clatlng));

                            setmCurrentLocation(location);
                            mCurrentLocation = location;

                            myRef = database.getReference(PATH_USERS + mAuth.getCurrentUser().getUid());
                            myRef.getDatabase().getReference(PATH_USERS + mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    Client = task.getResult().getValue(Usuario.class);

                                    Client.setLatitud(mCurrentLocation.getLatitude());
                                    Client.setLongitud(mCurrentLocation.getLongitude());
                                    myRef.setValue(Client);


                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al poner la disponibilidad del perfil", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            };
            mostrarpuntosjson();
            startLocationUpdates(); //condiciones localizaciones
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void crearNotificacionChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void crearNotificacion(Usuario u) {
        Intent intent = new Intent(this, ListaDisponiblesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(u.getNombre() + " esta disponible")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
        /*
        Intent intent = new Intent(MapsActivity.this, ListaDisponiblesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("usuario", u);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT| PendingIntent.FLAG_MUTABLE);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), Integer.parseInt(u.getNumerodeidentificacion()), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT| PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle(u.getNombre() + " esta disponible");
        builder.setGroup(CHANNEL_ID);
        builder.setColor(Color.BLUE);
        builder.setContentIntent(pendingIntent);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       // nm.notify(Integer.parseInt(u.getNumerodeidentificacion()), builder.build());
        nm.notify(1, builder.build());*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Sin acceso a localizacion", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            turnOnLocationAndStartUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRestart() {
        super.onRestart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onStart() {
        super.onStart();
        crearNotificacionChannel();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

}