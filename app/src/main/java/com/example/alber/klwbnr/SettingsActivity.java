package com.example.alber.klwbnr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference UserDatabase;
    private FirebaseUser CurrentUser;
    private TextView Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Name = (TextView) findViewById(R.id.settings_name);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = CurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();

                Name.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
