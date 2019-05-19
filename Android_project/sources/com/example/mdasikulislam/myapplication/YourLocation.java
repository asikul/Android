package com.example.mdasikulislam.myapplication;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.util.Date;

public class YourLocation extends AppCompatActivity implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
    private static final long FASTEST_INTERVAL = 500;
    private static final long INTERVAL = 1000;
    private static final String TAG = "LocationActivity";
    Button btnFusedLocation;
    Location mCurrentLocation;
    public GoogleApiClient mGoogleApiClient;
    String mLastUpdateTime;
    LocationRequest mLocationRequest;
    Button nextButton;
    private String roll;
    TextView tvLocation;

    /* Access modifiers changed, original: protected */
    public void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(100);
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        createLocationRequest();
        this.mGoogleApiClient = new Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        setContentView((int) R.layout.activity_your_location);
        this.tvLocation = (TextView) findViewById(R.id.tvLocation);
        this.btnFusedLocation = (Button) findViewById(R.id.button3);
        this.btnFusedLocation.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                YourLocation.this.updateUI();
            }
        });
        this.nextButton = (Button) findViewById(R.id.next);
        this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(YourLocation.this.getApplicationContext(), AllFriendList.class);
                intent.putExtra("roll", YourLocation.this.roll);
                YourLocation.this.startActivity(intent);
            }
        });
    }

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        this.mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        this.mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + this.mGoogleApiClient.isConnected());
    }

    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + this.mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    /* Access modifiers changed, original: protected */
    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this);
            Log.d(TAG, "Location update started ..............: ");
        }
    }

    public void onConnectionSuspended(int i) {
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        this.mCurrentLocation = location;
        this.mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (this.mCurrentLocation != null) {
            String lat = String.valueOf(this.mCurrentLocation.getLatitude());
            String lng = String.valueOf(this.mCurrentLocation.getLongitude());
            this.roll = getIntent().getExtras().getString("roll");
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            if (!this.roll.equals("")) {
                mRef.child("user").child(this.roll).child("lat_d").setValue(lat);
                mRef.child("user").child(this.roll).child("lng_d").setValue(lng);
            }
            this.tvLocation.setText("At Time: " + this.mLastUpdateTime + "\n" + "Latitude: " + lat + "\n" + "Longitude: " + lng + "\n" + "Accuracy: " + this.mCurrentLocation.getAccuracy() + "\n" + "Provider: " + this.mCurrentLocation.getProvider());
            return;
        }
        Log.d(TAG, "location is null ...............");
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /* Access modifiers changed, original: protected */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, (LocationListener) this);
        Log.d(TAG, "Location update stopped .......................");
    }

    public void onResume() {
        super.onResume();
        if (this.mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
}
