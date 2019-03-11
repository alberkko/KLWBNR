package com.example.alber.klwbnr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String ChatUser;
    private Toolbar ChatToolbar;
    private DatabaseReference RootRef;
    private FirebaseAuth Auth;
    private String CurrentUserId;
    private ImageButton ChatSendButton;
    private EditText CharMessageView;
    private RecyclerView MessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager LinearLayout;
    private MessageAdapter Adaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(ChatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    // the back arrow in the toolbar


        RootRef = FirebaseDatabase.getInstance().getReference();   // database reference to read and write data
        Auth = FirebaseAuth.getInstance();
        CurrentUserId = Auth.getCurrentUser().getUid();

        ChatUser = getIntent().getStringExtra("user_id");   //get id of the User you're in chat with

        RootRef.child("Users").child(ChatUser).addListenerForSingleValueEvent(new ValueEventListener() {    //read data at a path and listen for changes
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String chat_user_name = dataSnapshot.child("name").getValue().toString();    //get the id of the user you're texting with
                getSupportActionBar().setTitle(chat_user_name);                             //put it into the toolbar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setTitle(ChatUser);

        ChatSendButton = (ImageButton) findViewById(R.id.send_btn);
        CharMessageView = (EditText) findViewById(R.id.message_view);

        Adaper = new MessageAdapter(messagesList);                            // adapter to populate the messages List
        MessagesList = (RecyclerView) findViewById(R.id.messages_list);      //Initialize the RecyclerView : Kinda like ListView except better
        LinearLayout = new LinearLayoutManager(this);                // initialize the linear layout manager
        MessagesList.setHasFixedSize(true);                                //fixed size of recycler view layout
        MessagesList.setLayoutManager(LinearLayout);
        MessagesList.setAdapter(Adaper);

        loadMessages();         //method to load messages?

        ChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();   //so that user can send message by pressing Enter

            }
        });

    }

    private void loadMessages() {

        RootRef.child("messages").child(CurrentUserId).child(ChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);    //get message from the message class and add it to the database

                messagesList.add(message);             //add message to the list
                Adaper.notifyDataSetChanged();

                MessagesList.scrollToPosition(messagesList.size() - 1);     //scroll to the bottom of the list when sending a message

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        String message = CharMessageView.getText().toString();   // convert sent message to string
        if (!TextUtils.isEmpty(message)) {

            String currentUserRef = "messages/" + CurrentUserId + "/" + ChatUser;    //add the typed message to database under your own ID
            String chat_user_ref = "messages/" + ChatUser + "/" + CurrentUserId;     //add the typed message to database under the other user's ID

            DatabaseReference user_message_push = RootRef.child("messages").child(CurrentUserId).child(ChatUser).push(); //push() will return a reference to the new data path, which you can use to get the key or set data to it.
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("from", CurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


            CharMessageView.setText("");                                              //make texfield empty after sending message


            RootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {  //to simultaneously write to specific children of a node without overwriting other child nodes
                                                                                                 //you can update lower-level child values by specifying a path for the key.
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });

        }
    }
}
