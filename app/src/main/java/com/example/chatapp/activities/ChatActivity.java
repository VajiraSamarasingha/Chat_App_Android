package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivityChatBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constrant;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_chat);
        loadReceiveDetails();
    }

    private void loadReceiveDetails(){
        receiveUser = (User) getIntent().getSerializableExtra(Constrant.KEY_USER);
        binding.textName.setText(receiveUser.name);
    }

    private void setLiter(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }



}