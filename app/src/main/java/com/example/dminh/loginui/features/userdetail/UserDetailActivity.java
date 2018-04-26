package com.example.dminh.loginui.features.userdetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dminh.loginui.appcontraint.ContraintValue;
import com.example.dminh.loginui.R;
import com.example.dminh.loginui.features.edittor.EditorActivity;
import com.example.dminh.loginui.features.note.NoteActivity;
import com.example.dminh.loginui.features.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private TextView tvEmail;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initilizeId();

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Welcome!");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(UserDetailActivity.this, SignInActivity.class));
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences(ContraintValue.SHARE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ContraintValue.USER_EMAIL);
        editor.remove(ContraintValue.USER_TOKEN);
        editor.apply();
        finish();
    }

    private void initilizeId() {
        btnSignOut = findViewById(R.id.btn_sign_out);
        tvEmail = findViewById(R.id.tv_email);
        myToolbar = findViewById(R.id.my_toolbar_user);
    }
}
