package com.example.notesapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailActivity extends AppCompatActivity {
    EditText titleEditText , contentEditText;
    ImageButton saveButton;
    TextView pageTitleTextView ;
    String title,content,docId;
    boolean isEditMode=false;
    TextView deleteNoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        titleEditText=findViewById(R.id.notes_title_text);
        contentEditText=findViewById(R.id.notes_content_text);
        saveButton=findViewById(R.id.save_id_btn);
        pageTitleTextView=findViewById(R.id.page_title_text_view);
        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
        docId=getIntent().getStringExtra("docId");
        deleteNoteTextView=findViewById(R.id.delete_btn);
        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }
        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Edit Your Notes");
            deleteNoteTextView.setVisibility(View.VISIBLE);
        }
        saveButton.setOnClickListener((v)->saveNote());
        deleteNoteTextView.setOnClickListener((v)->deleteNoteFromFirebase());
    }

    void saveNote(){
        String notesTitle = titleEditText.getText().toString();
        String notesContent = contentEditText.getText().toString();

        if(notesTitle==null || notesTitle.isEmpty()){
            titleEditText.setError("Title is empty");
            return;
        }
        Note note = new Note();
        note.setTitle(notesTitle);
        note.setContent(notesContent);
        note.setTimestamp(Timestamp.now());


        saveNoteToFirebase(note);


    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceFromNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailActivity.this,"Note deleted successfully");
                    finish();
                }else{

                    Utility.showToast(NoteDetailActivity.this,"Failed to delete the Note");
                }
            }
        });
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceFromNotes().document(docId);
        }else{
            documentReference = Utility.getCollectionReferenceFromNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailActivity.this,"Note added succesfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailActivity.this,"Failed while adding Note");
                }
            }
        });
    }
}