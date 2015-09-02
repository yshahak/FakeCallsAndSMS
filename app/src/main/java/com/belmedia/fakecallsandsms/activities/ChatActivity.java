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
import android.widget.TextView;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.Utils;
import com.belmedia.fakecallsandsms.sms.ChatMessage;
import com.belmedia.fakecallsandsms.sms.SmsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by B.E.L on 30/08/2015.
 */
public class ChatActivity extends ActionBarActivity {

    private EditText messageET;
    private RecyclerView messagesContainer;
    private ArrayList<ChatMessage> chatHistory = new ArrayList<>();
    private SmsAdapter adapter;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // define the toolBar as the actionBar of that Activity
        getSupportActionBar().setTitle(null);
        String body, sender, thumbNail;
        Intent intent = getIntent();
        number = intent.getStringExtra(FakeSMS.KEY_CONTACT_NUMBER);
        Utils.getNumberHistory(getApplication(), number, chatHistory);
        sender = intent.getStringExtra(FakeSMS.KEY_CONTACT_NAME);
        body = intent.getStringExtra(FakeSMS.KEY_BODY_SMS);
        thumbNail = intent.getStringExtra(FakeSMS.KEY_CONTACT_THUMBNAIL);
        ((TextView)toolbar.findViewById(R.id.sender)).setText(sender);
        initControls(sender, body, thumbNail);

    }


    @Override
    protected void onStop() {
        super.onStop();
        Utils.save(getApplication(), chatHistory);
    }

    private void initControls(String sender, String body, String thumbNail) {
        messagesContainer = (RecyclerView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        ImageButton sendBtn = (ImageButton) findViewById(R.id.chatSendButton);
        loadDummyHistory(body, thumbNail);

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

    private void loadDummyHistory(String body, String thumbNail){
        addMassage(body, thumbNail, false);
        adapter =  new SmsAdapter(this, chatHistory);
        messagesContainer.setHasFixedSize(true);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));
        messagesContainer.setAdapter(adapter);
        messagesContainer.scrollToPosition(adapter.getItemCount() - 1);
    }


    private void addMassage(String body, String thumbNail, boolean isMe){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm", Locale.US);
        Calendar calendar = Calendar.getInstance();
        ChatMessage msg;
        if (chatHistory.size() == 0)
            msg = new ChatMessage(number, format.format(calendar.getTime()), formatHour.format(calendar.getTime()), body, thumbNail, isMe);
        else {
            if (isMe){
                msg = new ChatMessage(number, null, formatHour.format(calendar.getTime()), body, thumbNail, true);
            } else {
                String valid_until = Utils.getLastDate(chatHistory);
                Calendar strCalendar = null;
                if (valid_until != null) {
                    Date strDate;
                    strCalendar = Calendar.getInstance();
                    try {
                        strDate = format.parse(valid_until);
                        strCalendar.setTime(strDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (strCalendar != null && calendar.get(Calendar.DAY_OF_YEAR) >  strCalendar.get(Calendar.DAY_OF_YEAR))
                    msg = new ChatMessage(number, format.format(calendar.getTime()), formatHour.format(calendar.getTime()), body, thumbNail, false);
                else
                    msg = new ChatMessage(number, null, formatHour.format(calendar.getTime()), body, thumbNail, false);
            }
        }
        chatHistory.add(msg);

    }

}