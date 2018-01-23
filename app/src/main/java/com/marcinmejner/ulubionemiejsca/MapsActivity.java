package com.marcinmejner.ulubionemiejsca;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;



    public void centerMapLocation(Location location, String title) {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        if (title != "Twoja Lokacja") {
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapLocation(lastKnownLocation, "Twoja Lokacja");

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        if (intent.getIntExtra("numerMiejsca", 10) == 0) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centerMapLocation(location, "Twoja Lokacja");

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (Build.VERSION.SDK_INT < 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20, 10, locationListener);


                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapLocation(lastKnownLocation, "Twoja Lokacja");

                }
            } else {


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Musimy poprosić o pozwolenie
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapLocation(lastKnownLocation, "Twoja Lokacja");

                }


            }

        }
        else{
            Location placeLocation = new Location(locationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.lokacje.get(intent.getIntExtra("numerMiejsca", 0)).latitude);
            placeLocation.setLongitude(MainActivity.lokacje.get(intent.getIntExtra("numerMiejsca", 0)).longitude);

            centerMapLocation(placeLocation, MainActivity.miejsca.get(intent.getIntExtra("numerMiejsca", 0)));



        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {
            List<Address> listAdresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (listAdresses != null && listAdresses.size() > 0) {
                if (listAdresses.get(0).getThoroughfare() != null) {
                    if (listAdresses.get(0).getSubThoroughfare() != null) {
                        address += listAdresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAdresses.get(0).getThoroughfare() + " ";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (address == "") {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Data: yyyy.MM.dd Czas: HH:mm");
//            address = simpleDateFormat.format(new Date());
//        }


        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        SharedPreferences sharedPreferences =  this.getSharedPreferences("com.marcinmejner.ulubionemiejsca", Context.MODE_PRIVATE);

        MainActivity.miejsca.add(address);
        MainActivity.lokacje.add(latLng);
        try {

            ArrayList<String> latitude = new ArrayList<>();
            ArrayList<String> longiture = new ArrayList<>();

            for(LatLng coordinates : MainActivity.lokacje){
                latitude.add(Double.toString(coordinates.latitude));
                longiture.add(Double.toString(coordinates.longitude));


            }

            sharedPreferences.edit().putString("miejsca", ObjectSerializer.serialize(MainActivity.miejsca)).apply();
            sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(latitude)).apply();
            sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(longiture)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.adapter.notifyDataSetChanged();
    }
}
