package com.example.conlang.conlang;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestDetail extends AppCompatActivity {

    private TextView mTextName;
    private TextView mTextEmail;
    private TextView mTextType;
    private Button mAccept;
    private Button mdecline;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db ;
    private DatabaseReference databaseReference;

    private Intent myData;
    private Map<String, Object> request;
    private ProgressDialog progressDialog;
    private boolean status ;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(RequestDetail.this,RequestList.class));
                    return true;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(RequestDetail.this,ManageAccount.class));
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(RequestDetail.this,Report.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        initElement();

    }

    private void initElement() {

        mTextName = (TextView) findViewById(R.id.name);
        mTextEmail=(TextView) findViewById(R.id.email);
        mTextType=(TextView) findViewById(R.id.type);
        mAccept=(Button) findViewById(R.id.Accept);
        mdecline=(Button) findViewById(R.id.Decline);
        myData = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        //user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        HashMap<String, String> r = (HashMap<String, String>)myData.getSerializableExtra("Request");
        request = (Map)r;
        status=false;
        displayData(request);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void displayData(final Map<String, Object> request) {
        mTextName.setText(request.get("Name").toString());
        mTextEmail.setText(request.get("Email").toString());
        mTextType.setText(request.get("Type").toString());
        // to accept request
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToAuth(request);
            }
        });
        mdecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 goodbye(request);
            }
        });
    }

    private void goodbye(Map<String, Object> request) {
        sendEmail(request);
        deleteRequest(request);
        progressDialog.dismiss();
    }

    private void addToAuth(Map<String, Object> request) {
        firebaseAuth.createUserWithEmailAndPassword(request.get("Email").toString(), request.get("Password").toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            SingUpSuccessfully();
                        } else {

                            SingUpUnsuccessfully(task);
                        }
                    }
                });
    }

    private void SingUpSuccessfully(){
        HashMap<String,Object> userInput=(HashMap<String,Object>)request;
        SaveUserInfo(userInput);
        status=true;
        sendEmail(request);
        deleteRequest(request);
        progressDialog.dismiss();

    }

    private void deleteRequest(Map<String, Object> request) {
        // delete request from request list via user email
        db.collection("Registration Request").whereEqualTo("Email", request.get("Email")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            }
        });

        //goToHome();
    }

    private void sendEmail(Map<String, Object> request) {
        String to = request.get("Email").toString();
        String subject = "Conlang";
        String body = (status)?"We would like to inform you that you have been accepted into the Conlang\n" +
                "You can now log in using your e-mail and password\n" +
                "You can now update your personal data in your personal account":"we not accept you";
        String mailTo = "mailto:" + to +
                "?&subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body);
        Intent emailIntento = new Intent(Intent.ACTION_VIEW);
        emailIntento.setData(Uri.parse(mailTo));
        startActivity(emailIntento);

    }
    private void SaveUserInfo(HashMap<String,Object> userInput){

        String collectionName = "translator";


        db.collection(collectionName).add(userInput).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            public void onSuccess(DocumentReference documentReference) {
                SaveSuccessfully();
            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                SaveUnsuccessfully(e);
            }
        });

    }
    private void goToHome() {

        Context context = RequestDetail.this;
        Class homeClass = RequestList.class; // where to go
        Intent intent = new Intent(context,homeClass);
        startActivity(intent);

    }

    /**/
    private void SingUpUnsuccessfully(Task<AuthResult> task){
        Toast.makeText(RequestDetail.this, "Could not register,please try again", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();

    }

    /**/
    private void SaveSuccessfully(){
        Toast.makeText(RequestDetail.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
    }



    /**/
    private void SaveUnsuccessfully(Exception e){

        String error = e.getMessage();
        Toast.makeText(RequestDetail.this, "Error" + error, Toast.LENGTH_SHORT).show();
    }


}
