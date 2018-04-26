package com.example.dminh.loginui.models;

import java.io.Serializable;

public class Note implements Serializable {

    private String noteTitle;
    private String noteContent;
    private String dateCreated;
    private String Key;

    public Note() {
    }

    public Note(String dateCreated, String noteContent, String noteTitle, String key) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.dateCreated = dateCreated;
        this.Key = key;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
