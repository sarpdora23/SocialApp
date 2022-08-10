package com.sinamekidev.socialapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sinamekidev.socialapp.R;
import com.sinamekidev.socialapp.databinding.SettingsFragmentBinding;
import com.sinamekidev.socialapp.models.User;
import com.sinamekidev.socialapp.models.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsFragment extends Fragment {
    private SettingsFragmentBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ActivityResultLauncher<String> permission;
    private ActivityResultLauncher<Intent> gallery_launcher;
    private Uri image_uri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SettingsFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    private void register(){
        gallery_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    image_uri = intent.getData();

                }
            }
        });
        permission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gallery_launcher.launch(intent);
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if(mAuth.getCurrentUser().getUid().equals(documentChange.getDocument().getId())){
                            User user = documentChange.getDocument().toObject(User.class);
                            binding.settingsEmail.setText(user.getUserInfo().getEmail());
                            binding.settingsUsername.setText(user.getUserInfo().getUsername());
                            binding.settingsPassword.setText(user.getUserInfo().getPassword());
                            binding.settingsRepassword.setText(user.getUserInfo().getPassword());
                            if(image_uri != null){
                                binding.settingsProfileImage.setImageURI(image_uri);
                            }
                            else{
                                if(user.getUserInfo().getProfile_url().equals("default")){
                                    binding.settingsProfileImage.setImageResource(R.drawable.user);
                                }
                                else{
                                    Picasso.get().load(user.getUserInfo().getProfile_url()).into(binding.settingsProfileImage);
                                }
                            }
                        }
                    }
                }
                else {
                    Snackbar.make(binding.settingsLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        binding.settingsUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.settingsUsername.getText().toString();
                String password = binding.settingsPassword.getText().toString();
                String re_password = binding.settingsRepassword.getText().toString();
                String email = binding.settingsEmail.getText().toString();
                if(username.isEmpty() || password.isEmpty()){
                    Snackbar.make(binding.settingsLayout,"Fields can not be empty",Snackbar.LENGTH_SHORT).show();
                }
                else{
                    if(password.equals(re_password)){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    if(image_uri != null){
                                        storageReference.child("Users/ProfileImages/"+firebaseUser.getUid()+".jpeg").putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    storageReference.child("Users/ProfileImages/"+firebaseUser.getUid()+".jpeg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            if(task.isSuccessful()){
                                                                updateInfo(email,username,password,task.getResult().toString(),firebaseUser);
                                                            }
                                                            else{
                                                                updateInfo(email,username,password,"default",firebaseUser);
                                                            }
                                                        }
                                                    });

                                                }
                                                else{
                                                    Snackbar.make(binding.settingsLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }

                                }
                                else{
                                    Snackbar.make(binding.settingsLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Snackbar.make(binding.settingsLayout,"Passwords not match",Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.settingsProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Snackbar.make(binding.settingsLayout,"Permission needed for access gallery",Snackbar.LENGTH_INDEFINITE).setAction("Allow", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }
                    else{
                        permission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gallery_launcher.launch(intent);
                }
            }
        });
    }
    private void updateInfo(String email,String username,String password,String profile_url,FirebaseUser firebaseUser){
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(password);
        userInfo.setUsername(username);
        userInfo.setEmail(email);
        userInfo.setProfile_url(profile_url);
        System.out.println(profile_url);
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update("userInfo",userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar.make(binding.settingsLayout,"Succesfully Updated",Snackbar.LENGTH_LONG).show();
                }
                else{
                    Snackbar.make(binding.settingsLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        register();
    }
    private void init(){
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}
