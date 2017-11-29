package com.example.gregg.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

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

        final Button pin = findViewById(R.id.pinButton);
        final Button account = findViewById(R.id.accountButton);


        pin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
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

        HashMap<String, Integer> pins = new HashMap<>();

        try {
            for(String[] pin : JDBCInterface.getPins()){
                pins.put(pin[0], 0);
            }
            System.out.println("!!!!!!!!!!!!" + pins + "!!!!!!!!!!!!");
            for(String[] pin : JDBCInterface.getPins()){
                int temp = pins.get(pin[0]);
                temp++;
                System.out.print(temp);
                pins.put(pin[0], temp);
            }
            System.out.println("!!!!!!!!!!!!" + pins+ "!!!!!!!!!!!/");

            for(String key : pins.keySet()){
                if(pins.get(key) >= 3){
                    System.out.println("!!!!!!!!!" + pins.get(key));
                    try {
                        String latLng = JDBCInterface.getBuildingLocation(key);
                        String[] split = latLng.split(",");
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]))).title(key);
                        mMap.addMarker(marker);
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            String type;
                            ArrayList<String> comments = new ArrayList<String>();
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                try {
                                    for (String[] pins : JDBCInterface.getPins()) {
                                        if (pins[0].equals(marker.getTitle())) {
                                            comments.add(pins[2]);
                                            type = pins[1];
                                        }
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                return false;
                            }

                        });

                } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        }
    }catch (Exception e){
            e.printStackTrace();
        }
    }}
