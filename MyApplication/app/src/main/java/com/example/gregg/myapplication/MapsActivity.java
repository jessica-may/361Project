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
    static public int pinID;
    static public String type;
    static public ArrayList<String> comments = new ArrayList<>();
    static public String position;

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
            ArrayList<Pin> pinList = JDBCInterface.getPinList();
            for(Pin pin : pinList){
                pins.put(pin.position, 0);
            }
            for(Pin pin : pinList){
                int temp = pins.get(pin.position);
                temp++;
                System.out.print(temp);
                pins.put(pin.position, temp);
            }

            for(String key : pins.keySet()){
                if(pins.get(key) >= 3){
                    try {
                        String latLng = JDBCInterface.getBuildingLocation(key);
                        String[] split = latLng.split(",");
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]))).title(key);
                        mMap.addMarker(marker);
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                comments.clear();
                                try {
                                    for (Pin pins : JDBCInterface.getPinList()) {
                                        if (pins.position.equals(marker.getTitle())) {
                                            comments.add(pins.description);
                                            type = pins.category;
                                            position = pins.position;
                                            pinID = pins.pinID;

                                        }
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                                startActivity(new Intent(MapsActivity.this, PinDisplay.class));
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
