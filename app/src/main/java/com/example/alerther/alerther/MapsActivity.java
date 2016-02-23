package com.example.alerther.alerther;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MobileServiceClient mClient;
    MobileServiceTable<IncidentItem> mIncidentTable;
    private ProgressBar mProgressBar;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set all markers here
        mMap = googleMap;
        try {
            /////----------------------------------Zooming camera to position user-----------------
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},11);
                mMap.setMyLocationEnabled(true);
            }
            /////----------------------------------Zooming camera to position user-----------------
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        AlertHerMobileServiceClient client = new AlertHerMobileServiceClient();
                        final ArrayList<IncidentItem> incidentItems = client.GetAllIncidentItems();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (Iterator<IncidentItem> incidentItem = incidentItems.iterator();
                                     incidentItem.hasNext();
                                        ) {
                                    IncidentItem item = incidentItem.next();
                                    LatLng latlng = new LatLng(item.mLatitude, item.mLongitude);
                                    mMap.addMarker(new MarkerOptions().position(latlng).title(item.mDescription).snippet(item.mTips));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                                }
                            }
                        });
                    } catch (Exception e) {
                        createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
                    }
                    return null;
                }
            };
            runAsyncTask(task);
        } catch (Exception e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent intent = new Intent(MapsActivity.this
                        , MarkerActivity.class);
                intent.putExtra("latlong", latLng);
                startActivity(intent);
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        createAndShowDialog(exception.toString(), title);
    }

    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public void showTimePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getFragmentManager(), "datePicker");
    }

}
