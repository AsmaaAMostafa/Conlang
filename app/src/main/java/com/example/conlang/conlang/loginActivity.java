package com.example.conlang.conlang;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;

//firebase auth importing
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthException;

// FireBseDatabase importing
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference ;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;



public class loginActivity extends AppCompatActivity {


    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private DatabaseReference RefDatabase;
    private String userID, userType;
    private ProgressBar progressBar;
    private Button btnLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(loginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);


        inputEmail = (EditText) findViewById(R.id.Email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        btnLogin = (Button) findViewById(R.id.login_button);





        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();



           // check if one of the field missing
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), " email address is required !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), " password is required !", Toast.LENGTH_SHORT).show();
                    return;
                }
        // check if email contain @ for valid Email format
                progressBar.setVisibility(View.VISIBLE);
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError(" incorrect Email format ");
                    inputEmail.requestFocus();
                    return;
                }
                //distinguish between Customer,translator,admn after successful login and assign them to their respective activities

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {


                                        Toast.makeText(loginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                }
                                if (task.isSuccessful()) {
                                    Toast.makeText(loginActivity.this, "login successfully", Toast.LENGTH_LONG).show();
                                    //get the current user Id
                                    FirebaseUser currentUser  = FirebaseAuth.getInstance().getCurrentUser();
                                    userID = currentUser.getUid();

                                    //according to their user id get the user type
                                    RefDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                    RefDatabase.addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                             userType = dataSnapshot.child("type").getValue().toString();



                                            if (userType.equals("Customer")) {
                                                Intent i = new Intent(loginActivity.this, customerActivity.class);
                                                startActivity(i);
                                                finish();


                                            } else if (userType.equals("translator")) {
                                                Intent i = new Intent(loginActivity.this, translatorActivity.class);
                                                startActivity(i);
                                                finish();

                                          // this is the version with admin interface only for developers and testers !
                                            } else if (userType.equals("admin")) {
                                                Intent i = new Intent(loginActivity.this, adminActivity.class);
                                                startActivity(i);
                                                finish();


                                            } else {
                                                Toast.makeText(loginActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }


                                    });

                                }
                            }

                        });
            }


        });
    }
}
