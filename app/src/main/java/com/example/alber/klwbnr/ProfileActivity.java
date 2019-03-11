package com.example.alber.klwbnr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView ProfileName;
    private DatabaseReference UsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   //run this code in addition to the existing code in the onCreate class.
        setContentView(R.layout.activity_profile);

        String userid = getIntent().getStringExtra("user_id");

        UsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        ProfileName = (TextView) findViewById(R.id.profile_displayName);

        UsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                ProfileName.setText(display_name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }



    }
