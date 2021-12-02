package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateProfile extends AppCompatActivity {

    EditText etname,etbio,etprofession,etemail,etwebsite;
    Boolean imageAdded = Boolean.FALSE;
    String currentUserId;
    String imageUrlResult;
    Button saveButton;
    ImageView imageView;
    ProgressBar progressBar;
    Uri image_uri;
    UploadTask uploadTask;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://eventify-f35eb-default-rtdb.asia-southeast1.firebasedatabase.app/");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    private static final int PICK_IMAGE=1;

    All_UserMember member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        imageView = findViewById(R.id.iv_cp);
        saveButton = findViewById(R.id.profile_button);
        etname = findViewById(R.id.profile_name);
        etbio = findViewById(R.id.profile_bio);
        etprofession = findViewById(R.id.profile_profession);
        etemail = findViewById(R.id.profile_email);
        etwebsite = findViewById(R.id.profile_website);
        progressBar = findViewById(R.id.profile_progress);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance("gs://eventify-f35eb.appspot.com").getReference("Profile images");
        databaseReference = database.getReference("All Users");

        loadData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(CreateProfile.this,"Fill all",Toast.LENGTH_SHORT).show();
                uploadData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageAdded = Boolean.TRUE;
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent,PICK_IMAGE);
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){
                image_uri = data.getData();
                Picasso.get().load(image_uri).into(imageView);
            }
        }catch (Exception e){
            Toast.makeText(this,"Error"+e,Toast.LENGTH_LONG).show();
        }

    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    public void uploadData(){
        String name = etname.getText().toString();
        String web = etbio.getText().toString();
        String bio = etwebsite.getText().toString();
        String prof = etprofession.getText().toString();
        String email = etemail.getText().toString();
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(web) && !TextUtils.isEmpty(bio) && !TextUtils.isEmpty(prof) && !TextUtils.isEmpty(email)){

            if(!isValid(email)){
                Toast.makeText(this,"Add a valid email",Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            if(imageAdded == Boolean.TRUE){

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(image_uri));
            uploadTask = reference.putFile(image_uri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        Map<String,String> profile = new HashMap<>();
                        profile.put("name",name);
                        profile.put("prof",prof);
                        profile.put("bio",bio);
                        profile.put("url",downloadUri.toString());
                        profile.put("email",email);
                        profile.put("web",web);
                        profile.put("prof",prof);
                        profile.put("privacy","Public");

                        member = new All_UserMember();
                        member.setName(name);
                        member.setProf(prof);
                        member.setUid(currentUserId);
                        member.setUrl(downloadUri.toString());

                        databaseReference.child(currentUserId).setValue(member);
                        documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.setVisibility(View.VISIBLE);

                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    Intent intent = new Intent(CreateProfile.this,MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.fade_out);


//                                    getSupportFragmentManager().beginTransaction()
//                                            .add(android.R.id.content, new Fragment1()).commit();
                                },2000);
                            }
                        });
                    }
                }
            });

            }
            else{

                Map<String,String> profile = new HashMap<>();
                profile.put("name",name);
                profile.put("prof",prof);
                profile.put("bio",bio);
                profile.put("url",imageUrlResult);
                profile.put("email",email);
                profile.put("web",web);
                profile.put("prof",prof);
                profile.put("privacy","Public");

                member = new All_UserMember();
                member.setName(name);
                member.setProf(prof);
                member.setUid(currentUserId);
                member.setUrl(imageUrlResult);

                databaseReference.child(currentUserId).setValue(member);
                documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.VISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                                    Intent intent = new Intent(CreateProfile.this,MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
//                            getSupportFragmentManager().beginTransaction()
//                                    .add(android.R.id.content, new Fragment1()).commit();
//                            getSupportFragmentManager().beginTransaction()
//                                    .add(android.R.id.content, new Fragment1()).commit();
                        },2000);
                    }
                });
            }
        }
        else{
            Toast.makeText(this,"Fill all the fields",Toast.LENGTH_SHORT).show();
        }
    }

    public void loadData(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user").document(user.getUid());
        reference = firestore.collection("user").document(user.getUid());
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String nameResult = task.getResult().getString("name");
                            String bioResult = task.getResult().getString("bio");
                            String websiteResult = task.getResult().getString("web");
                            String emailResult = task.getResult().getString("email");
                            String profResult = task.getResult().getString("prof");
                            imageUrlResult = task.getResult().getString("url");

                            Picasso.get().load(imageUrlResult).into(imageView);
                            etname.setText(nameResult);
                            etbio.setText(bioResult);
                            etemail.setText(emailResult);
                            etprofession.setText(profResult);
                            etwebsite.setText(websiteResult);
                        }
                    }
                });
    }
    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}