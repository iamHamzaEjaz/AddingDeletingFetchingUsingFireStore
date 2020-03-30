package com.example.assigment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore objfb;
    private EditText DET, SET, RET;

    private Dialog objectDialog;
    private DocumentReference objectDocumentReference;

    private static final String COLLECTION_NAME = "Students";
    private TextView downloadedDataTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objfb = FirebaseFirestore.getInstance();
        objectDialog = new Dialog(this);

        objectDialog.setContentView(R.layout.wrap_dialouge);
        objectDialog.setCancelable(false);

        DET = findViewById(R.id.documentIDET);
        SET = findViewById(R.id.stdnameET);

        RET = findViewById(R.id.stdrollnoET);
        downloadedDataTV = findViewById(R.id.downloadedDataTV);
    }

//Deleting the complete doucment
    public void deleteDocument(View v){
        if(DET.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter the document id", Toast.LENGTH_SHORT).show();
        }else {
            objectDialog.show();
            objectDocumentReference = objfb.collection(COLLECTION_NAME)
                    .document(DET.getText().toString());
            objectDocumentReference.delete() .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    objectDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Data Doucment deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            objectDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Fails to delete filed data:"
                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
//Checking the Doucment and adding the value
    public void addUniqueDocument(View v) {
        try {
            if (DET.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter valid document id", Toast.LENGTH_SHORT).show();
            } else {
                objectDialog.show();
                objectDocumentReference = objfb.collection(COLLECTION_NAME)
                        .document(DET.getText().toString());

                objectDocumentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                objectDialog.dismiss();
                                if (documentSnapshot.exists()) {
                                    Toast.makeText(MainActivity.this, "No Document Retrieved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> objmap = new HashMap<>();

                                    objmap.put("Student_name", SET.getText().toString());
                                    objmap.put("Roll_no", RET.getText().toString());
                                    objfb.collection("Students")
                                            .document(DET.getText().toString()).set(objmap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Data Added", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    objectDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Data not Added" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        objectDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Fails to retrieve data:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            objectDialog.dismiss();
            Toast.makeText(this, "getValuesFromFirebaseFirstore:" +
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


 //Fetching the complete collection
 public void getAllDoucment(View v) {
     try {
         CollectionReference notebookRef = objfb.collection("Students");
         notebookRef.get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         String data = "";

                         for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                             String documentID = documentSnapshot.getId();
                             DET.requestFocus();
                             String StudentName = documentSnapshot.getString("Student_name");
                             String RollNo = documentSnapshot.getString("Roll_no");

                             data += "ID: " + documentID
                                     + "\nTitle: " + StudentName + "\nDescription: " + RollNo + "\n\n";
                         }

                         downloadedDataTV.setText(data);
                     }
                 });
     }catch (Exception e){
         Toast.makeText(this, "getValuesFromFirebaseFirstore:" +
                 e.getMessage(), Toast.LENGTH_SHORT).show();

     }

 }

 //Deleting complete Collection
 public void deletingCompleteCollection(View v) {
        try {
            CollectionReference notebookRef = objfb.collection("Students");
            notebookRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String data = "";

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String documentID = documentSnapshot.getId();

                                objectDocumentReference = objfb.collection(COLLECTION_NAME)
                                        .document(documentID);
                                objectDocumentReference.delete();

                            }


                        }
                    });
        }catch (Exception e){
            Toast.makeText(this, "deletingCompleteCollection:" +
                    e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }


}
