package com.example.mdasikulislam.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendLocation extends AppCompatActivity {
    public static double fLocLatitude;
    public static double fLocLongitude;
    Button btnmap;
    TextView flocation;
    TextView flocation1;
    DatabaseReference mDatabase;
    private String roll;
    private String u_roll;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_friend_location);
        Bundle bundle = getIntent().getExtras();
        this.u_roll = bundle.getString("u_roll");
        this.roll = bundle.getString("roll");
        this.flocation = (TextView) findViewById(R.id.textView5);
        this.flocation1 = (TextView) findViewById(R.id.textView6);
        this.btnmap = (Button) findViewById(R.id.button4);
        this.btnmap.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                FriendLocation.this.startActivity(new Intent(FriendLocation.this.getApplicationContext(), MapsActivity.class));
            }
        });
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mDatabase.child("user").child(this.roll).addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String flat = dataSnapshot.child("lat_d").getValue().toString();
                String flng = dataSnapshot.child("lng_d").getValue().toString();
                FriendLocation.fLocLatitude = Double.parseDouble(flat);
                FriendLocation.fLocLongitude = Double.parseDouble(flng);
                FriendLocation.this.flocation.setText("Latitude" + FriendLocation.fLocLatitude);
                FriendLocation.this.flocation1.setText("Longitude" + FriendLocation.fLocLongitude);
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
