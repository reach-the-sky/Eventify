package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class PostClickActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    NotificationCompat.Builder builder;
    ImageView imageView;
    TextView tvname,tvbio,tvemail;
    EditText tvTitle,tvDescription;
//    final MediaPlayer mp = MediaPlayer.create(PostClickActivity.this, R.raw.zapsplat_technology_studio_speaker_active_power_switch_click_003_68875);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_click);

        FloatingActionButton closeButton = findViewById(R.id.profile_close_button);
        Button shareButton = findViewById(R.id.post_share_button);
        Button saveButton = findViewById(R.id.post_save_button);

        imageView = findViewById(R.id.iv_cp);
        tvname = findViewById(R.id.profile_name);
        tvbio = findViewById(R.id.profile_bio);
//        tvprofession = findViewById(R.id.profile_profession);
        tvemail = findViewById(R.id.profile_email);
//        tvwebsite = findViewById(R.id.profile_website);

        tvTitle = findViewById(R.id.post_title);
        tvDescription = findViewById(R.id.post_description);

        tvTitle.setText(getIntent().getStringExtra("title"));
        tvDescription.setText(getIntent().getStringExtra("description"));

        // Notification


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(this,"My Notification")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Post Added")
                .setContentText("Hey you have a new post in your eventify app.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
//                .setContentTitle("Notification Alert, Click Me!")
//                .setContentText("Hi, This is Android Notification Detail!");

        if(!getIntent().getStringExtra("uuid").equals(auth.getUid().toString())) {
            tvTitle.setEnabled(false);
            tvDescription.setEnabled(false);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mp.start();
                sendMessage("*" + getIntent().getStringExtra("title") + "*\n" + getIntent().getStringExtra("description"));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getIntent().getStringExtra("uuid").equals(auth.getUid().toString())){
                    updatePostData(tvTitle.getText().toString(),tvDescription.getText().toString(),getIntent().getStringExtra("position"));

                    // Notificaiton
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(3, builder.build());

                    finish();
                }
                else{
                    Toast.makeText(PostClickActivity.this,"Not authorized",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(getIntent().getStringExtra("uuid"));
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
                            String imageUrlResult = task.getResult().getString("url");

                            Picasso.get().load(imageUrlResult).into(imageView);
                            tvname.setText(nameResult);
                            tvbio.setText(bioResult);
                            tvemail.setText(emailResult);
//                            tvprofession.setText(profResult);
//                            tvwebsite.setText(websiteResult);
                        }
                        else{
                            Toast.makeText(PostClickActivity.this,"No data found",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void openWhatsApp(String mensaje){
        String numero = "6281344620";

        try{
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone="+ numero +"&text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
//            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
//            }else {
//                Toast.makeText(this,"error bro",Toast.LENGTH_SHORT).show();
//            }
        } catch(Exception e) {
            Log.e("ERROR WHATSAPP",e.toString());
            Toast.makeText(this,"catch error",Toast.LENGTH_SHORT).show();
        }

    }

    private void sendMessage(String message)
    {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");

        intent.putExtra(Intent.EXTRA_TEXT, message);
//        if (intent
//                .resolveActivity(
//                        getPackageManager())
//                == null) {
//            Toast.makeText(
//                    this,
//                    "Please install whatsapp first.",
//                    Toast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
        try {
            startActivity(intent);
        }
        catch(Exception err){
            Toast.makeText(this,"Error: " + err,Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePostData(String title,String description,String position){
        FirebaseDatabase database =  FirebaseDatabase.getInstance("https://eventify-f35eb-default-rtdb.asia-southeast1.firebasedatabase.app/");
        Item newItem = new Item();
        String newItemTitle = title;
        String newItemDescription = description;
        newItem.setTitle( newItemTitle.substring(0,1).toUpperCase(Locale.ROOT) + newItemTitle.substring(1));
        newItem.setDescription(newItemDescription.substring(0,1).toUpperCase(Locale.ROOT) + newItemDescription.substring(1));
        ArrayList categories = (ArrayList<String>) getIntent().getSerializableExtra("categoryList");
        newItem.setCategory(categories);
        newItem.setUuid(getIntent().getStringExtra("uuid"));
        database.getReference("posts/" + position).setValue(newItem);
    }
    
    
}