package com.example.ramesh.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton
        ;

import java.util.ArrayList;

public class Chatbot extends AppCompatActivity {
    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

//code for sending the message
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                sendMessage(message);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }
    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
        if((chatMessage.getContent().contains("create")))
        {
            mimicOtherMessage("The album requested will be Created.");
            Intent in=new Intent(this,Create.class);
            startActivity(in);
        }
        else if((chatMessage.getContent().contains("show")))
        {
            mimicOtherMessage("The picture requested will be showed");
            Intent in=new Intent(this,Show.class);
            startActivity(in);
        }
        else
        {
            mimicOtherMessage("Hello");
        }

    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }
}