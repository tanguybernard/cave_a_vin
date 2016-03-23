package istic.fr.cavevin;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String nameAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        nameAdress = getIntent().getExtras().getString(MainActivity.ADDRESS);
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
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> ad = geocoder.getFromLocationName(nameAdress,1);
            if(ad != null && ad.size()>0){
               Address currentAdress = ad.get(0);
                LatLng pos = new LatLng(currentAdress.getLatitude(), currentAdress.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(nameAdress));
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(pos,10,0,0)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
