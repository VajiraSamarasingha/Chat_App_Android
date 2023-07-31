package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatapp.R;
import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.databinding.ActivityUserBinding;
import com.example.chatapp.listerner.userListener;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constrant;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements userListener {

    private ActivityUserBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        getUser();
    }
    private void setListener(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUser(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constrant.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->{
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constrant.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constrant.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constrant.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constrant.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constrant.KEY_FCM_TOKEN);
                            users.add(user);
                        }
                        if (users.size()>0){
                            UserAdapter userAdapter =new UserAdapter(users,this);
                            binding.userRecycleView.setAdapter(userAdapter);
                            binding.userRecycleView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }


    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onUserClicked(User user){
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constrant.KEY_USER,user);
        startActivity(intent);
        finish();
    }

}