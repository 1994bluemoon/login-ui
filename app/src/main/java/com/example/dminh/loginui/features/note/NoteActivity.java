package com.example.dminh.loginui.features.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dminh.loginui.R;
import com.example.dminh.loginui.appcontraint.ContraintValue;
import com.example.dminh.loginui.features.edittor.EditorActivity;
import com.example.dminh.loginui.features.userdetail.UserDetailActivity;
import com.example.dminh.loginui.models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity implements ItemEventInterface {

    private DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    private List<Note> notes;
    private NoteAdapter adapter;
    private RecyclerView rvNote;
    private Toolbar myToolbar;
    private boolean isLongClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        rvNote = findViewById(R.id.rvNote);
        myToolbar = findViewById(R.id.my_toolbar_note);

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Your Note");
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notes = new ArrayList<>();
        adapter = new NoteAdapter(this);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvNote.setHasFixedSize(true);
        rvNote.setLayoutManager(manager);
        rvNote.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        if (mAuth != null){
            myRef = database.getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            getListData();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_new_note, menu);
        getMenuInflater().inflate(R.menu.menu_action_user, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // action bar item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user:
                startActivity(new Intent(NoteActivity.this, UserDetailActivity.class));
                return true;
            case R.id.action_create_new:
                startActivity(new Intent(NoteActivity.this, EditorActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (!isLongClicked){
            startActivity(new Intent(NoteActivity.this, EditorActivity.class).putExtra(ContraintValue.PUT_NOTE, notes.get(position)));
        } else isLongClicked = false;
    }

    @Override
    public void removeClicked(int position) {
        myRef.child(notes.get(position).getKey()).removeValue();
    }

    private void getListData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if  (dataSnapshot.hasChildren()) {
                    notes = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        notes.add(data.getValue(Note.class));
                    }
                    adapter.setNotes(notes);
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(NoteActivity.this, "create new note now", Toast.LENGTH_SHORT).show();
                    notes = new ArrayList<>();
                    adapter.setNotes(notes);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                notes = new ArrayList<>();
                adapter.setNotes(notes);
                adapter.notifyDataSetChanged();
                Log.w("Value", "Failed to read value.", error.toException());
                Toast.makeText(NoteActivity.this, "create new note now", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
