package com.example.gregg.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        System.out.print("here in maps");
        final Button pin = findViewById(R.id.pinButton);
        final Button account = findViewById(R.id.accountButton);


        pin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                System.out.println("reached here");
                startActivity(new Intent(MapsActivity.this, CreatePin.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AccountActivity.class));
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        // Add a marker at Union and move the camera
        LatLng greenspace = new LatLng(40.817947, -96.7003121);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(greenspace, 18));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(40.817947, -96.7003121)));

        System.out.println("!!!!!!!!!!here!!!!!!!!");

        try {
            for(String[] pin : JDBCInterface.getPins()){
                System.out.println("!!!!!!!!yo!!!!!!");
                try {
                    System.out.print("!!!!!" + pin[0] + "!!!!!!!");
                    String latLng = JDBCInterface.getBuildingLocation(pin[0]);
                    String[] split = latLng.split(",");
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]))).title("Test");
                    mMap.addMarker(marker);
                }catch(Exception e){
                        e.printStackTrace();
                    }

            }
        } catch (Exception e) {
            System.out.print("failed");
            e.printStackTrace();
    }}}
