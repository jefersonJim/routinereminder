package br.com.jry.routinereminder.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.jry.routinereminder.R;

public class MapsActivity extends AppCompatActivity implements PlaceSelectionListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Boolean preencheInFoEdit = false;

    private TextView mPlaceDetailsText;
    private TextView mPlaceAttribution;
    private GoogleMap map;

    private MarkerOptions markerOptions;
    private Circle circle;
    private SeekBar areaSeekBar;
    private GoogleApiClient mGoogleApiClient;
    private LatLng myLocation;
    private Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setTitle("Routine Reminder - Selecionar Local");

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        areaSeekBar = (SeekBar) findViewById(R.id.areaSeekBar);
        areaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (circle != null) {
                    circle.setRadius(progress + 100);
                    TextView tvDistancia = (TextView) findViewById(R.id.tvDistancia);
                    tvDistancia.setText(progress + 100 + "m");
                    int x = ((areaSeekBar.getRight() - areaSeekBar.getLeft()) * areaSeekBar.getProgress()) / areaSeekBar.getMax();
                    tvDistancia.setPadding((int) (x - (0.097 * x)), 0, 0, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if(getIntent().getSerializableExtra("endereco") != null){
            preencheInFoEdit = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar =getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        geocoder = new Geocoder(this, Locale.getDefault());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                if(markerOptions.getTitle()== null){
                    try {
                        addresses = geocoder.getFromLocation(markerOptions.getPosition().latitude, markerOptions.getPosition().longitude, 1);
                        intent.putExtra("endereco",addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    intent.putExtra("endereco",markerOptions.getTitle());
                }

                intent.putExtra("lat",markerOptions.getPosition().latitude);
                intent.putExtra("lng",markerOptions.getPosition().longitude);
                intent.putExtra("distancia", areaSeekBar.getProgress()+100);
                setResult(RESULT_OK, intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPlaceSelected(Place place) {
        setLocalMap(place.getLatLng());
        markerOptions.title(place.getAddress().toString());
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (markerOptions != null) {
            map.clear();
            map.addMarker(markerOptions).showInfoWindow();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15);
            map.moveCamera(cameraUpdate);
            map.animateCamera(cameraUpdate);

            circle = map.addCircle(new CircleOptions()
                    .center(markerOptions.getPosition())
                    .radius(areaSeekBar.getProgress() + 100)
                    .strokeColor(Color.parseColor("#45d3b0")));

            TextView tvDistancia = (TextView) findViewById(R.id.tvDistancia);
            tvDistancia.setText(areaSeekBar.getProgress() + 100 + "m");
            int x = ((areaSeekBar.getRight() - areaSeekBar.getLeft()) * areaSeekBar.getProgress()) / areaSeekBar.getMax();
            tvDistancia.setPadding(x, 0, 0, 0);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setLocalMap(latLng);
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (myLocation != null) {
                    setLocalMap(myLocation);
                }
                return false;
            }
        });

        if(preencheInFoEdit){
            setEditLocal();
            preencheInFoEdit = false;
        }
    }

    public void setLocalMap(LatLng localMap) {
        map.clear();
        markerOptions = new MarkerOptions().position(localMap);
        map.addMarker(markerOptions).showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(localMap, 15);
        map.moveCamera(cameraUpdate);
        map.animateCamera(cameraUpdate);

        circle = map.addCircle(new CircleOptions()
                .center(localMap)
                .radius(100)
                .strokeColor(Color.parseColor("#45d3b0")));

        areaSeekBar.setProgress(0);
    }

    public void setEditLocal(){
        Intent data = getIntent();
        String endereco = (String) data.getSerializableExtra("endereco");
        Double latitude = (Double) data.getSerializableExtra("lat");
        Double longitude = (Double) data.getSerializableExtra("lng");
        int distancia = (Integer) data.getSerializableExtra("distancia");

        LatLng localMap = new LatLng(latitude, longitude);

        map.clear();
        markerOptions = new MarkerOptions().position(localMap).title(endereco);
        map.addMarker(markerOptions).showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(localMap, 21);
        map.moveCamera(cameraUpdate);
        map.animateCamera(cameraUpdate);

        circle = map.addCircle(new CircleOptions()
                .center(localMap)
                .radius(distancia)
                .strokeColor(Color.parseColor("#45d3b0")));

        areaSeekBar.setProgress(distancia-100);


    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location mLastLocation =  LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null && markerOptions == null) {
            myLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            setLocalMap(myLocation);
        }

        if(preencheInFoEdit){
            setEditLocal();
            preencheInFoEdit = false;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
