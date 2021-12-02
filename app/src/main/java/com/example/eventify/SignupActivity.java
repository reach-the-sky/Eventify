package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignupActivity extends AppCompatActivity {

    EditText passwordEt,confirmPasswordEt,emailEt;
    Button signup,login;
    ProgressBar progressBar;
    CheckBox checkBox;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        passwordEt = findViewById(R.id.signup_password);
        confirmPasswordEt = findViewById(R.id.signup_confirm_password);
        emailEt = findViewById(R.id.signup_email);
        signup = findViewById(R.id.signup_button);
        login = findViewById(R.id.signup_login_button);
        progressBar = findViewById(R.id.signup_progressbar);
        checkBox = findViewById(R.id.signup_checkbox);
        mAuth = FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                String confirmPassword = confirmPasswordEt.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){
                    if(password.equals(confirmPassword)){

                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            sendToMain();
                                        } else {
                                            String error = task.getException().toString();
                                            Toast.makeText(SignupActivity.this, "Authentication failed." + error,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Passwords are not matching",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignupActivity.this,"Please Fill all the fields",Toast.LENGTH_SHORT).show();
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Sending to main activity
    private void sendToMain() {
        Intent intent = new Intent(SignupActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    // If user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }
}