package com.example.mdasikulislam.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {
    private Button btn1;
    private Button btn2;
    DatabaseReference mDatabase;
    private EditText namefield;
    private EditText phonefield;
    private EditText rollfield;
    public String u_roll;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.namefield = (EditText) findViewById(R.id.editText2);
        this.phonefield = (EditText) findViewById(R.id.editText3);
        this.rollfield = (EditText) findViewById(R.id.editText6);
        this.btn1 = (Button) findViewById(R.id.button1);
        this.btn2 = (Button) findViewById(R.id.button2);
        this.btn1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String name = MainActivity.this.namefield.getText().toString().trim();
                String phone = MainActivity.this.phonefield.getText().toString().trim();
                MainActivity.this.u_roll = MainActivity.this.rollfield.getText().toString().trim();
                if (!(name.equals("") || phone.equals("") || MainActivity.this.u_roll.equals(""))) {
                    MainActivity.this.mDatabase.child("user").child(MainActivity.this.u_roll).child("name_d").setValue(name);
                    MainActivity.this.mDatabase.child("user").child(MainActivity.this.u_roll).child("phone_d").setValue(phone);
                }
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), YourLocation.class);
                intent.putExtra("roll", MainActivity.this.u_roll);
                Toast.makeText(MainActivity.this, "user " + MainActivity.this.u_roll, 0).show();
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
