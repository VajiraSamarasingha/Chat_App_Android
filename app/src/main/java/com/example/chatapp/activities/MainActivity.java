package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.chatapp.databinding.ActivityMainBinding;
import com.example.chatapp.utilities.Constrant;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

   private ActivityMainBinding binding;
   private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListerners();
    }

    private void setListerners(){

        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.favNewChart.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));
    }

    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constrant.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constrant.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
            database.collection(Constrant.KEY_COLLECTION_USERS).document(
                    preferenceManager.getString(Constrant.KEY_USER_ID)
            );
        documentReference.update(Constrant.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e -> showToast("Unable to Update Token"));


    }
    private void signOut() {
        showToast("Sign out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference=
                database.collection(Constrant.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constrant.KEY_USER_ID)
                );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constrant.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable To Sign Out"));


    }

}