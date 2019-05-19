package com.example.mdasikulislam.myapplication;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LatLng ClatLng;
    ArrayList<LatLng> MarkerPoints;
    LatLng latLng;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;

    private class FetchUrl extends AsyncTask<String, Void, String> {
        private FetchUrl() {
        }

        /* synthetic */ FetchUrl(MapsActivity x0, AnonymousClass1 x1) {
            this();
        }

        /* Access modifiers changed, original: protected|varargs */
        public String doInBackground(String... url) {
            String data = "";
            try {
                data = MapsActivity.this.downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
                return data;
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
                return data;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask(MapsActivity.this, null).execute(new String[]{result});
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        private ParserTask() {
        }

        /* synthetic */ ParserTask(MapsActivity x0, AnonymousClass1 x1) {
            this();
        }

        /* Access modifiers changed, original: protected|varargs */
        public List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            List<List<HashMap<String, String>>> routes = null;
            try {
                JSONObject jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
                return routes;
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
                return routes;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(List<List<HashMap<String, String>>> result) {
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                ArrayList<LatLng> points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = (List) result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = (HashMap) path.get(j);
                    points.add(new LatLng(Double.parseDouble((String) point.get("lat")), Double.parseDouble((String) point.get("lng"))));
                }
                lineOptions.addAll(points);
                lineOptions.width(10.0f);
                lineOptions.color(SupportMenu.CATEGORY_MASK);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }
            if (lineOptions != null) {
                MapsActivity.this.mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (VERSION.SDK_INT >= 23) {
            checkLocationPermission();
        }
        this.MarkerPoints = new ArrayList();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (VERSION.SDK_INT < 23) {
            buildGoogleApiClient();
            this.mMap.setMyLocationEnabled(true);
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            buildGoogleApiClient();
            this.mMap.setMyLocationEnabled(true);
        }
        this.mMap.setOnMapClickListener(new OnMapClickListener() {
            public void onMapClick(LatLng point) {
                MapsActivity.this.mMap.clear();
                Double lat = Double.valueOf(FriendLocation.fLocLatitude);
                Double lng = Double.valueOf(FriendLocation.fLocLongitude);
                LatLng origin = MapsActivity.this.ClatLng;
                LatLng dest = new LatLng(lat.doubleValue(), lng.doubleValue());
                Toast.makeText(MapsActivity.this, "my lat=" + MapsActivity.this.ClatLng.latitude + "and lng=" + MapsActivity.this.ClatLng.longitude, 0).show();
                Toast.makeText(MapsActivity.this, "dest lat=" + dest.latitude + "and lng=" + dest.longitude, 1).show();
                MarkerOptions options = new MarkerOptions();
                options.position(origin).icon(BitmapDescriptorFactory.defaultMarker(330.0f));
                MapsActivity.this.mMap.addMarker(options);
                options.position(dest).icon(BitmapDescriptorFactory.defaultMarker(60.0f));
                MapsActivity.this.mMap.addMarker(options);
                Log.d("onMapClick", MapsActivity.this.getUrl(origin, dest).toString());
                new FetchUrl(MapsActivity.this, null).execute(new String[]{url});
                Builder builder = new Builder();
                builder.include(origin);
                builder.include(dest);
                MapsActivity.this.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50), new CancelableCallback() {
                    public void onCancel() {
                    }

                    public void onFinish() {
                        MapsActivity.this.mMap.animateCamera(CameraUpdateFactory.zoomBy(-3.0f));
                    }
                });
            }
        });
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(strUrl).openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while (true) {
                str = br.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /* Access modifiers changed, original: protected|declared_synchronized */
    public synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        this.mGoogleApiClient.connect();
    }

    public void onConnected(Bundle bundle) {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(1000);
        this.mLocationRequest.setFastestInterval(1000);
        this.mLocationRequest.setPriority(102);
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this);
        }
    }

    public void onConnectionSuspended(int i) {
    }

    public void onLocationChanged(Location location) {
        this.mLastLocation = location;
        if (this.mCurrLocationMarker != null) {
            this.mCurrLocationMarker.remove();
        }
        this.ClatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(this.ClatLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(120.0f));
        this.mCurrLocationMarker = this.mMap.addMarker(markerOptions);
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.ClatLng));
        this.mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        if (this.mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, (LocationListener) this);
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.ACCESS_FINE_LOCATION")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 99);
            return false;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 99);
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 99:
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    Toast.makeText(this, "permission denied", 1).show();
                    return;
                } else if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
                    if (this.mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
                    this.mMap.setMyLocationEnabled(true);
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }
}
