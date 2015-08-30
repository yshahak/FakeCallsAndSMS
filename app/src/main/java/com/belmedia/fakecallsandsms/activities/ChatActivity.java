package com.belmedia.fakecallsandsms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.sms.ChatMessage;
import com.belmedia.fakecallsandsms.sms.SmsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by B.E.L on 30/08/2015.
 */
public class ChatActivity extends ActionBarActivity {

    private EditText messageET;
    private RecyclerView messagesContainer;
    private ImageButton sendBtn;
    //private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private SmsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // define the toolBar as the actionBar of that Activity

        String body, sender, thumbNail;
        Intent intent = getIntent();
        sender = intent.getStringExtra(FakeSMS.KEY_CONTACT_NUMBER);
        body = intent.getStringExtra(FakeSMS.KEY_BODY_SMS);
        thumbNail = intent.getStringExtra(FakeSMS.KEY_CONTACT_THUMBNAIL);

        initControls(sender, body, thumbNail);
    }

    private void initControls(String sender, String body, String thumbNail) {
        messagesContainer = (RecyclerView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);
        loadDummyHistory(sender, body, thumbNail);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                int size = chatHistory.size();
                addMassage(messageText, null, true);
                adapter.notifyDataSetChanged();
                messageET.setText("");
                messagesContainer.scrollToPosition(size);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageET.getWindowToken(), 0);
            }
        });
    }

    private void loadDummyHistory(String sender, String body, String thumbNail){
        getSupportActionBar().setTitle(sender);

        chatHistory = new ArrayList<>();
        addMassage(body, thumbNail, false);
        adapter =  new SmsAdapter(this, chatHistory);
        messagesContainer.setHasFixedSize(true);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));
        //messagesContainer.addItemDecoration(new SimpleDividerItemDecoration(getResources()));

        messagesContainer.setAdapter(adapter);

    }

    private void addMassage( String body, String thumbNail, boolean isMe){

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy E", Locale.US);
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm", Locale.US);

        //DateFormat format = DateFormat.getDateTimeInstance();
        Date date = new Date();
        ChatMessage msg = new ChatMessage(format.format(date), formatHour.format(date), body, thumbNail, isMe);
        chatHistory.add(msg);

    }
}