package com.example.dminh.loginui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initilizeId();

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(ContraintValue.SHARE_FILE, MODE_PRIVATE);
        String email = sharedPreferences.getString(ContraintValue.USER_EMAIL, "null");
        tvEmail.setText(email);

        btnSignOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_out:
                signOut();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        signOut();
    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(UserDetailActivity.this, MainActivity.class));
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initilizeId() {
        btnSignOut = findViewById(R.id.btn_sign_out);
        tvEmail = findViewById(R.id.tv_email);
    }
}
