package com.example.dminh.loginui.features.note;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dminh.loginui.R;
import com.example.dminh.loginui.appcontraint.ContraintValue;
import com.example.dminh.loginui.features.edittor.EditorActivity;
import com.example.dminh.loginui.features.userdetail.UserDetailActivity;
import com.example.dminh.loginui.models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity implements ItemEventInterface, View.OnClickListener {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private List<Note> notes;
    private NoteAdapter adapter;
    private boolean isLongClicked = false;
    long back_pressed_time = 0;

    private RecyclerView rvNote;
    private Toolbar myToolbar;
    private ProgressBar progressBar;
    private ImageView imgAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        // add this after setContentView and before other code
        rvNote = findViewById(R.id.rvNote);
        myToolbar = findViewById(R.id.my_toolbar_note);
        imgAddNew = findViewById(R.id.imgAddNew);
        progressBar = findViewById(R.id.pbWait);

        //set up toolbar, set title
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Your Note");

        notes = new ArrayList<>();
        adapter = new NoteAdapter(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // setup recyclerview
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvNote.setHasFixedSize(true);
        rvNote.setLayoutManager(manager);
        rvNote.setAdapter(adapter);

        //get firebase path
        if (mAuth != null){
            myRef = database.getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            getListData();
        }

        progressBar.setVisibility(View.VISIBLE);
        imgAddNew.setOnClickListener(this);
    }

    // setup menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create menu item in res/menu package first
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgAddNew:
                startActivity(new Intent(NoteActivity.this, EditorActivity.class));
                break;
        }
    }

    // onclick interface implement for item in recyclerview clicked
    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(NoteActivity.this, EditorActivity.class).putExtra(ContraintValue.PUT_NOTE, notes.get(position)));

    }

    // onclick interface implement for item in recyclerview clicked
    @Override
    public void removeClicked(int position) {
        myRef.child(notes.get(position).getKey()).removeValue();
    }

    // double tap back button to exit app
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
    protected void onStop() {
        progressBar.setVisibility(View.GONE);
        super.onStop();
    }

    //get data from firebase
    private void getListData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if  (dataSnapshot.hasChildren()) {
                    notes = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        notes.add(data.getValue(Note.class));
                    }
                    imgAddNew.setVisibility(View.GONE);
                    rvNote.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    sortList();
                    adapter.setNotes(notes);
                    adapter.notifyDataSetChanged();
                }else {
                    notes = new ArrayList<>();
                    adapter.setNotes(notes);
                    progressBar.setVisibility(View.GONE);
                    imgAddNew.setVisibility(View.VISIBLE);
                    rvNote.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                notes = new ArrayList<>();
                adapter.setNotes(notes);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                imgAddNew.setVisibility(View.VISIBLE);
                rvNote.setVisibility(View.INVISIBLE);
                Log.w("Value", "Failed to read value.", error.toException());
            }
        });
    }

    void sortList(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        for (int i = 0; i < (notes.size()); i++){
            for (int j = 0; j < notes.size() - 1; j++){
                try {
                    java.util.Date date = dateFormat.parse(notes.get(j).getDateCreated());
                    java.util.Date date2 = dateFormat.parse(notes.get(j+1).getDateCreated());
                    if (date.compareTo(date2) < 0){
                        Note temp = notes.get(j);
                        notes.set(j, notes.get(j+1));
                        notes.set(j+1, temp);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
