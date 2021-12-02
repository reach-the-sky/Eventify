package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SplashScreen extends AppCompatActivity {

    ImageView imageView,imageView_left,imageView_right;
    TextView textView;
    long animationTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.splash_image_view_top);
        imageView_left = findViewById(R.id.splash_image_view_left);
        imageView_right = findViewById(R.id.splash_image_view_right);
        textView = findViewById(R.id.splash_text_view);

        textView.setAlpha(0.0f);
        textView.animate().alpha(1.0f).setDuration(2000);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",500f);
        ObjectAnimator animatorleft = ObjectAnimator.ofFloat(imageView_left, "x", 400f);
        ObjectAnimator animatorright = ObjectAnimator.ofFloat(imageView_right, "x", 700f,400f);
        animatorY.setDuration(animationTime);
        animatorleft.setDuration(animationTime);
        animatorright.setDuration(animationTime);

//        animatorY.start();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,animatorleft,animatorright);
        animatorSet.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);

            }
        },4000);

    }
}