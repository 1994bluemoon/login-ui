package com.example.dminh.loginui.features.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dminh.loginui.R;
import com.example.dminh.loginui.features.signin.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private TextView tvSignIn;
    private TextInputEditText edtEmail, edtPassword, edtrePassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private String password, email;
    private ProgressDialog progressDialog;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initilizeId();

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.super.onBackPressed();
                finish();
            }
        });

        this.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();
                password = "";
                if (edtPassword.getText().toString().equals(edtrePassword.getText().toString())) {
                    password = edtPassword.getText().toString();
                    if (!email.equals("") && !password.equals("")){
                        disableUi();
                        signUp();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "2 of your password not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUp(){
        if (!email.equals("") && ! password.equals("")){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("create", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        progressDialog.cancel();
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("create", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        enableUi();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initilizeId() {
        tvSignIn = findViewById(R.id.tv_sign_in);
        edtPassword = findViewById(R.id.edt_password);
        edtrePassword = findViewById(R.id.edt_password2);
        edtEmail = findViewById(R.id.edt_email);
        btnSignUp = findViewById(R.id.btn_sign_up);
        myToolbar = findViewById(R.id.my_toolbar_sign_up);

        progressDialog = new ProgressDialog(this);
        enableUi();
    }

    private void disableUi(){
        tvSignIn.setEnabled(false);
        edtPassword.setEnabled(false);
        edtrePassword.setEnabled(false);
        btnSignUp.setEnabled(false);
        edtEmail.setEnabled(false);

        progressDialog.setTitle("Please wait");
        progressDialog.show();
    }

    private void enableUi(){
        tvSignIn.setEnabled(true);
        edtPassword.setEnabled(true);
        edtrePassword.setEnabled(true);
        btnSignUp.setEnabled(true);
        edtEmail.setEnabled(true);
        progressDialog.dismiss();
    }
}
