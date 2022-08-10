package com.sinamekidev.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sinamekidev.socialapp.databinding.ActivityRegisterBinding;
import com.sinamekidev.socialapp.models.User;
import com.sinamekidev.socialapp.models.UserInfo;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }
    private void register(){
        String mail = binding.registerEmail.getText().toString();
        String username = binding.registerUsername.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String re_password = binding.registerRepassword.getText().toString();
        boolean check = binding.registerTermsCheckbox.isChecked();
        if((mail.isEmpty() || username.isEmpty()) || (password.isEmpty() || re_password.isEmpty())){
            fieldClean();
            Snackbar.make(binding.registerLayout,"Fields can not be empty",Snackbar.LENGTH_LONG).show();
        }
        else{
            if(password.equals(re_password)){
                if(check){
                    mAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                User user = new User(new UserInfo(username,mail,"default",password));
                                firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                        else{
                                            Snackbar.make(binding.registerLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                                            fieldClean();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    fieldClean();
                    Snackbar.make(binding.registerLayout,"You must accept terms and conditions",Snackbar.LENGTH_LONG).show();
                }
            }
            else{
                fieldClean();
                Snackbar.make(binding.registerLayout,"Passwords not match",Snackbar.LENGTH_LONG).show();
            }

        }
    }
    private void fieldClean(){
        binding.registerEmail.setText("");
        binding.registerPassword.setText("");
        binding.registerRepassword.setText("");
        binding.registerUsername.setText("");
    }
}