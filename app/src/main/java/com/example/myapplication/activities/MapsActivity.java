package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.example.myapplication.mapUtils.FetchURL;
import com.example.myapplication.mapUtils.TaskLoadedCallback;
import com.example.myapplication.models.Result;
import com.example.myapplication.models.RootObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, TaskLoadedCallback {

    private LocationManager locationManager;
    private GoogleMap mMap;
    private Button navigation_btn;
    private Polyline currentPolyline;
    private RelativeLayout map_container;
    private RootObject rootObject;
    private ActivityMapsBinding binding;
    private LatLng selectedLatLng;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static String PLACES_SEARCH_URL;
    private static final boolean PRINT_AS_STRING = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        onLocationChanged(location);


        PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?" +
                "location=" + SplashActivity.latitude + "," + SplashActivity.longitude + "&radius=5000&types=restaurant&sensor=true&key=AIzaSyCdjm2w0PZxzMH_HQXzT1GQGQM2IU0uOVQ";


        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, HomeFragment.class));

            }
        });
        navigation_btn = findViewById(R.id.navigation_btn);
        navigation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedLatLng!=null) {




                    Intent mobIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + selectedLatLng.latitude + "," + selectedLatLng.longitude + "&mode=d"));
                    mobIntent.setPackage("com.google.android.apps.maps");


                    if(mobIntent.resolveActivity(getPackageManager())!=null){
                        startActivity(mobIntent);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=33.489954,73.098888 &daddr=33.499009,73.101076 &dirflg=d"));
                        startActivity(intent);
                    }


                }else{
                    Toast.makeText(MapsActivity.this, "Cannot Start navigating please select a valid marker" , Toast.LENGTH_LONG).show();
                }
            }
        });

        map_container = findViewById(R.id.map_container);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.setTimeout(36000);
        client.get(PLACES_SEARCH_URL, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                map_container.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                map_container.setVisibility(View.VISIBLE);
                String responce = new String(responseBody);
                try {

                    Gson gson = new Gson();
                    if (statusCode == 200) {
                        rootObject = gson.fromJson(responce, RootObject.class);
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.mapAct);
                        mapFragment.getMapAsync(MapsActivity.this);


                    } else {
                        Toast.makeText(MapsActivity.this, "" + statusCode, Toast.LENGTH_LONG).show();
                    }


                } catch (Exception ex) {
                    Toast.makeText(MapsActivity.this, "" + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(MapsActivity.this, "" + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyCdjm2w0PZxzMH_HQXzT1GQGQM2IU0uOVQ";
        return url;
    }

    private void showMapContent(boolean drawRoute,Marker destMarker){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker marker = mMap.addMarker(new MarkerOptions().position(
                new LatLng(SplashActivity.latitude,SplashActivity.longitude)));
        builder.include(marker.getPosition());

        if(!drawRoute){
            for(Result result : rootObject.results){


                Gson gson = new Gson();
                String json = gson.toJson(result.name);

                Marker markJason = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(result.geometry.location.lat,result.geometry.location.lng)).title(json).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                builder.include(markJason.getPosition());
            }
        }else{


            Marker markJason = mMap.addMarker(new MarkerOptions().position(
                    destMarker.getPosition()).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            builder.include(markJason.getPosition());
        }


        int widthNew = getResources().getDisplayMetrics().widthPixels;
        int heightNew = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (widthNew * 0.10); // offset from edges of the map 10% of screen



        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);


        mMap.setMaxZoomPreference(17.0f);
        //mMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f));
        mMap.animateCamera(cu);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // Add a marker in Sydney and move the camera
        //LatLng userLatLong = new LatLng(SplashActivity.latitude, SplashActivity.longitude);
        //mMap.addMarker(new MarkerOptions().position(userLatLong).title("Current Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));



        showMapContent(false,null);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                mMap.clear();
                selectedLatLng = marker.getPosition();
                showMapContent(true,marker);
                new FetchURL(MapsActivity.this)
                        .execute(getUrl(marker.getPosition(),
                                new LatLng(SplashActivity.latitude, SplashActivity.longitude), "driving"), "driving");
            }
        });

    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        navigation_btn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        SplashActivity.longitude = location.getLongitude();
        SplashActivity.latitude = location.getLatitude();

        Toast.makeText(MapsActivity.this, "Long: " +SplashActivity.longitude + " , " + "Lat: " + SplashActivity.latitude, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}