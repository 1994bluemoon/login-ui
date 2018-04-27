package com.example.dminh.loginui.features.edittor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dminh.loginui.R;
import com.example.dminh.loginui.appcontraint.ContraintValue;
import com.example.dminh.loginui.features.note.NoteActivity;
import com.example.dminh.loginui.features.userdetail.UserDetailActivity;
import com.example.dminh.loginui.models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Objects;

public class EditorActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    FirebaseDatabase database;

    private EditText edtTitle, edtContent;
    private Note note;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        edtContent = findViewById(R.id.edtContent);
        edtTitle = findViewById(R.id.edtTitle);
        Toolbar myToolbar = findViewById(R.id.my_toolbar_editor);

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Note");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            note = (Note) bundle.get(ContraintValue.PUT_NOTE);
            isEdit = true;
            assert note != null;
            edtTitle.setText(note.getNoteTitle());
            edtContent.setText(note.getNoteContent());
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (mAuth != null){
            myRef = database.getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        }
    }

    @Override
    protected void onStop() {
        saveNote();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_save_note, menu);
        getMenuInflater().inflate(R.menu.menu_action_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // action bar item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_user:
                startActivity(new Intent(EditorActivity.this, UserDetailActivity.class));
                return true;
            case R.id.action_save:
                startActivity(new Intent(EditorActivity.this, NoteActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        if (note == null){
            if (!edtContent.getText().toString().equals("")){
                String title;
                if (edtTitle.getText().toString().equals("")){
                    try {
                        title = edtContent.getText().toString().substring(0, 20);
                    } catch (Exception ex){
                        title = edtContent.getText().toString();
                    }
                } else title = edtTitle.getText().toString();

                String date = DateFormat.getDateTimeInstance().format(new java.util.Date());

                note = new Note(date, edtContent.getText().toString(), title, String.valueOf(System.currentTimeMillis()));

                uploadFireBase(note);
            }
        } else {
            if (!edtContent.getText().toString().equals("")) {
                if (!note.getNoteTitle().equals(edtTitle.getText().toString()) || !note.getNoteContent().equals(edtContent.getText().toString())) {
                    String title;
                    if (edtTitle.getText().toString().equals("")) {
                        try {
                            title = edtContent.getText().toString().substring(0, 20);
                        } catch (Exception ex) {
                            title = edtContent.getText().toString();
                        }
                    } else title = edtTitle.getText().toString();

                    String date = DateFormat.getDateTimeInstance().format(new java.util.Date());

                    note.setNoteTitle(title);
                    note.setNoteContent(edtContent.getText().toString());
                    note.setDateCreated(date);

                    uploadFireBase(note);
                }
            }else {
                myRef.child(note.getKey()).removeValue();
                Toast.makeText(EditorActivity.this, "Note removed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFireBase(Note note){
        myRef.child(note.getKey()).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(EditorActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                finish();
            }

        });
    }
}
