package com.example.alber.klwbnr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


               //MY MAIN ACTIVITY
public class UsersActivity extends AppCompatActivity {

    private Toolbar Toolbar;
    private RecyclerView UsersList;
    private DatabaseReference UsersDatabase;
    private LinearLayoutManager LayoutManager;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Toolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("RARYAB");

        UsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        LayoutManager = new LinearLayoutManager(this);

        UsersList = (RecyclerView) findViewById(R.id.users_list);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

    }


    @Override
    protected void onStart() {
        super.onStart();
        Auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = Auth.getCurrentUser();

        if (currentUser == null) {
            sendToStart();
        }

        Query query = UsersDatabase.orderByChild("name");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {


            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull Users model) {

                holder.setName(model.getName());

                final String List_user_id = getRef(position).getKey();

                UsersDatabase.child(List_user_id).addValueEventListener(new ValueEventListener() {      //
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("name").getValue().toString();

                        holder.setName(username);

                        // Identifying User profile On Which Clicked
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"open profile", "send message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);   //may be wrong
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item. I guess.
                                        if (i == 0) {
                                            Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                                            intent.putExtra("user_id", List_user_id);
                                            startActivity(intent);
                                        }

                                        if (i == 1) {

                                            Intent chatintent = new Intent(UsersActivity.this, ChatActivity.class);
                                            chatintent.putExtra("user_id", List_user_id);
                                            startActivity(chatintent);

                                        }


                                    }
                                });

                                builder.show();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
            }

        };

        UsersList.setAdapter(adapter);
        adapter.startListening();


    }


    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Context c;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView txtUserName = (TextView) mView.findViewById(R.id.user_single_name);
            txtUserName.setText(name);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if (item.getItemId() == R.id.main_settings_btn) {
            Intent settingsIntent = new Intent(UsersActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return true;
    }


    private void sendToStart() {
        Intent startIntent = new Intent(UsersActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

}
