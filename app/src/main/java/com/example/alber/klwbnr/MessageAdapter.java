package com.example.alber.klwbnr;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())                                     //to retrieve a standard LayoutInflater instance that is already hooked up to the current context and correctly configured for the device you are running on.
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;

        public MessageViewHolder(View view) {
            super(view);                          //initialize my parent class before you initialize me

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);
        viewHolder.messageText.setText(c.getMessage());

        String from_user = c.getFrom();

        if (from_user.equals(current_user_id)) {     //if the message is coming from you

            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLUE);

        } else {

            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);    //if the messages is coming from anyone else
            viewHolder.messageText.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();

    }

}