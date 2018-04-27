package com.example.dminh.loginui.features.note;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dminh.loginui.R;
import com.example.dminh.loginui.models.Note;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private List<Note> notes;
    private ItemEventInterface mActivityListener;

    NoteAdapter(ItemEventInterface activitiListener) {
        this.mActivityListener = activitiListener;
        this.notes = new ArrayList<>();
    }

    public void setNotes(List<Note> notes){
        this.notes = new ArrayList<>();
        this.notes.addAll(notes);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvTitle.setText(notes.get(position).getNoteTitle());
        holder.tvContent.setText(notes.get(position).getNoteContent());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
           java.util.Date date = dateFormat.parse(notes.get(position).getDateCreated());
           String result = dateFormat.format(date);
            holder.tvDateCreated.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.clNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityListener.onItemClick(position);
            }
        });

       holder.imgDelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mActivityListener.removeClicked(position);
           }
       });
    }

    @Override
    public int getItemCount() {
        return notes != null ? notes.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateCreated, tvContent;
        ConstraintLayout clNoteItem;
        ImageView imgDelete;
        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContentPreview);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            clNoteItem = itemView.findViewById(R.id.clNoteItem);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
