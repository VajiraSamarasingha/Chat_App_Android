package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.databinding.ActivitySigninBinding;
import com.example.chatapp.utilities.Constrant;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding bining;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        bining = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(bining.getRoot());
        setListerner();
    }

    private void setListerner(){
        bining.TextCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignupActivity.class)));
        bining.buttonSignin.setOnClickListener(v -> {
            if(isValideSigninDetails()){
                signIn();
            }
        });
    }



    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constrant.KEY_COLLECTION_USERS)
                .whereEqualTo(Constrant.KEY_EMAIL, bining.inputEmail.getText().toString())
                .whereEqualTo(Constrant.KEY_PASSWORD,bining.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constrant.KEY_IS_SIGN_IN,true);
                        preferenceManager.putString(Constrant.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constrant.KEY_NAME,documentSnapshot.getString(Constrant.KEY_NAME));
                        preferenceManager.putString(Constrant.KEY_IMAGE,documentSnapshot.getString(Constrant.KEY_IMAGE));

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        loading(false);
                        showToast("Unable To Sign In");
                    }
                });
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            bining.buttonSignin.setVisibility(View.INVISIBLE);
            bining.progressBar.setVisibility(View.VISIBLE);
        }else{
            bining.progressBar.setVisibility(View.INVISIBLE);
            bining.buttonSignin.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValideSigninDetails(){
        if(bining.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(bining.inputEmail.getText().toString()).matches()) {
            showToast("Enter Valide Email");
            return false;
        } else if (bining.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }
        else {
            return true;
        }
    }

}