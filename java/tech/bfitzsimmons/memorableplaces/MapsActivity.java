package tech.bfitzsimmons.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Intent intent;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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

    public void returnToList(View view){

//        if(userHasUpdatedMap){
//            //save the updated map to shared preferences via Gson. Thank god for open source libraries. And google's engineers
//            Gson gson = new Gson();
//            String serializedMap = gson.toJson(Places.placesMap);
//            MainActivity.sharedPreferences.edit().putString("serializedMap", serializedMap).apply();
//
//            //save the names to a serialized arraylist as well. Probably a better way to do this
//            String serializedNames = gson.toJson(Places.placesNames);
//            MainActivity.sharedPreferences.edit().putString("serializedNames", serializedNames).apply();
//        }

        //save the new places using sharedPreferences
        String serializedNames = new Gson().toJson(MainActivity.placeNames);
        MainActivity.sharedPreferences.edit().putString("placeNames", serializedNames).apply();

        //save new lat and long using sharedPreferences too
        String serializedLats = new Gson().toJson(MainActivity.placeLats);
        MainActivity.sharedPreferences.edit().putString("placeLats", serializedLats).apply();
        String serializedLongs = new Gson().toJson(MainActivity.placeLongs);
        MainActivity.sharedPreferences.edit().putString("placeLongs", serializedLongs).apply();

        //return to MainAcvitity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Type typeOfMap = new TypeToken<LinkedHashMap<String, Location>>() { }.getType();
//        String serializedMap = MainActivity.sharedPreferences.getString("serializedMap", new Gson().toJson(new LinkedHashMap<String, Location>()));
//        locationsMap = new Gson().fromJson(serializedMap, typeOfMap);


        //need to repopulate Places.placesMap now with saved data
//        for (Map.Entry<String, Location> entry: locationsMap.entrySet()) {
//            Places.placesMap.put(entry.getKey(), entry.getValue());
//        }

        mMap = googleMap;

        //set up long press listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Use Geocoder to get place name
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(listAddresses != null && listAddresses.size() > 0){
                        if(listAddresses.get(0).getAddressLine(0) != null && listAddresses.get(0).getAddressLine(0) != null){
                            String placeName = listAddresses.get(0).getAddressLine(0);
                            double placeLat = latLng.latitude;
                            double placeLong = latLng.longitude;

                            //add marker to map with correct name
                            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(placeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            marker.showInfoWindow();
                            Toast.makeText(MapsActivity.this, "Location saved", Toast.LENGTH_SHORT).show();

                            //save placeName, placeLat, and placeLon
                            MainActivity.placeNames.add(placeName);
                            MainActivity.placeLats.add(placeLat);
                            MainActivity.placeLongs.add(placeLong);

                            //notify the arrayAdapter so when we go back to the list, it is updated
                            MainActivity.adapter.notifyDataSetChanged();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //set up location mgmt
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                intent = getIntent();
                // Start on user location if there is nothing in the Intent! (During bootup or when clicking add new place
                if(intent.getStringExtra("place") == null){
                    Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    locationServices(userLocation.getLatitude(), userLocation.getLongitude(), "Your location");
                } else {
                    String locationKey = intent.getStringExtra("place");
                    double lat = intent.getDoubleExtra("lat", 0.0);
                    double lng = intent.getDoubleExtra("lng", 0.0);
                    locationServices(lat, lng, locationKey);
                }
            }
        }
    }

    public void locationServices(double lat, double lng, String title){
        //zoom in on clicked place from list
        LatLng coords = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(coords).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 10));
        marker.showInfoWindow();

        //now place markers on all over memorable places in map
        for(int i = 0; i<MainActivity.placeNames.size(); i++){
            String otherName = MainActivity.placeNames.get(i);
            double otherLat = MainActivity.placeLats.get(i);
            double otherLong = MainActivity.placeLongs.get(i);
            LatLng otherCoords = new LatLng(otherLat, otherLong);
            mMap.addMarker(new MarkerOptions().position(otherCoords).title(otherName));
        }
    }
}
