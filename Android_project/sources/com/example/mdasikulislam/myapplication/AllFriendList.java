package com.example.mdasikulislam.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class AllFriendList extends AppCompatActivity {
    private String data_lat;
    private String data_lng;
    private String data_name;
    private String data_phone;
    ArrayList<String> mList = new ArrayList();
    DatabaseReference mRef;
    private String u_roll;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_all_friend_list);
        this.u_roll = getIntent().getExtras().getString("roll");
        final ListView listView = (ListView) findViewById(R.id.listview);
        this.mRef = FirebaseDatabase.getInstance().getReference().child("user");
        this.mRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(AllFriendList.this, "check2", 0).show();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Toast.makeText(AllFriendList.this, "" + child.toString(), 0).show();
                    AllFriendList.this.data_name = child.child("name_d").getValue().toString();
                    AllFriendList.this.mList.add(AllFriendList.this.data_name);
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AllFriendList.this.getApplicationContext(), "Database error ", 1).show();
            }
        });
        listView.setAdapter(new ArrayAdapter(this, 17367043, this.mList));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AllFriendList.this.mRef.orderByChild("name_d").equalTo((String) listView.getItemAtPosition(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String rollkey = child.getKey();
                            Intent intent = new Intent(AllFriendList.this.getApplicationContext(), FriendLocation.class);
                            intent.putExtra("roll", rollkey);
                            intent.putExtra("u_roll", AllFriendList.this.u_roll);
                            AllFriendList.this.startActivity(intent);
                        }
                    }

                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}
