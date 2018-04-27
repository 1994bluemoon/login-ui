package com.example.dminh.loginui.features.signin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dminh.loginui.appcontraint.ContraintValue;
import com.example.dminh.loginui.R;
import com.example.dminh.loginui.features.note.NoteActivity;
import com.example.dminh.loginui.features.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String email, password;
    private TextInputEditText edtEmail, edtPassword;
    private Button btnSignIn;
    private TextView tvCreateNew;
    private ProgressDialog progressDialog;
    private Toolbar myToolbar;
    long back_pressed_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initializeId();

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign In");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            showSignInSuccess(currentUser);
        }
        btnSignIn.setOnClickListener(this);
        tvCreateNew.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if ( back_pressed_time + 2000 > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else Toast.makeText(getBaseContext(), "Press back again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed_time = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_in:
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                if (!email.equals("") && !password.equals("")){
                    disableUi();
                    signIn();
                }
                break;
            case R.id.tv_create_new:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        }
    }

    private void showSignInSuccess(FirebaseUser currentUser) {
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignored){}
        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences(ContraintValue.SHARE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ContraintValue.USER_EMAIL, currentUser.getEmail());
        editor.putString(ContraintValue.USER_TOKEN, currentUser.getUid());
        editor.apply();
        startActivity(new Intent(SignInActivity.this, NoteActivity.class));
        progressDialog.cancel();
        finish();
    }

    private void initializeId() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        tvCreateNew = findViewById(R.id.tv_create_new);
        myToolbar = findViewById(R.id.my_toolbar_sign_in);
        progressDialog = new ProgressDialog(this);
        enableUi();
    }

    private void enableUi(){
        edtPassword.setEnabled(true);
        edtPassword.setEnabled(true);
        tvCreateNew.setEnabled(true);
        btnSignIn.setEnabled(true);
        progressDialog.dismiss();
    }

    private void disableUi(){
        edtPassword.setEnabled(false);
        edtPassword.setEnabled(false);
        tvCreateNew.setEnabled(false);
        btnSignIn.setEnabled(false);

        progressDialog.setTitle("Please wait");
        progressDialog.show();
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("logn in", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    Log.d("token", user.getUid());
                    showSignInSuccess(user);

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("log in", "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    enableUi();
                }
            }
        });
    }

}
