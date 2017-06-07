package com.tryouts.courierapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tryouts.courierapplication.items.User;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextPhone;
    private EditText mEditTextName;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    // User is signed out
                }
            }
        };

        mEditTextEmail = (EditText) findViewById(R.id.login_edittext_email);
        mEditTextPassword = (EditText) findViewById(R.id.login_edittext_pass);
        mEditTextPhone = (EditText) findViewById(R.id.login_edittext_phone);
        mEditTextName = (EditText) findViewById(R.id.login_edittext_name);
        TextView mTextViewSignIn = (TextView) findViewById(R.id.login_textview_already);
        Button mSignUpButton = (Button) findViewById(R.id.login_button_signup);
        mProgressDialog = new ProgressDialog(this);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        mTextViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void registerUser() {
        final String mEmail = mEditTextEmail.getText().toString().trim();
        final String mPhone = mEditTextPhone.getText().toString().trim();
        final String mName = mEditTextName.getText().toString().trim();
        String mPass = mEditTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty(mEmail)){
            Toast.makeText(this,getString(R.string.login_hint_email), Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPass)){
            Toast.makeText(this,getString(R.string.login_hint_password),Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPhone) || mPhone.length()<9){
            Toast.makeText(this,getString(R.string.hint_phone),Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mName)){
            Toast.makeText(this,getString(R.string.hint_name),Toast.LENGTH_SHORT).show();
            return;
        }
        // If the email and password are not empty
        // display progress dialog
        mProgressDialog.setMessage(getString(R.string.login_progressbar_register));
        mProgressDialog.show();
        // Create a new User
        mAuth.createUserWithEmailAndPassword(mEmail, mPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                            mDatabaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User mUser = new User();
                                            mUser.setName(mName);
                                            mUser.setEmail(mEmail);
                                            mUser.setRole("customer");
                                            mUser.setPhone(mPhone);
                                            mUser.setUid(mAuth.getCurrentUser().getUid());
                                            mDatabaseReference.child("users").child(
                                                    mAuth.getCurrentUser().getUid())
                                                    .setValue(mUser);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        mProgressDialog.dismiss();
                        if(!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                    mEditTextPassword.setError(getString(R.string.login_weak_password));
                                    mEditTextPassword.requestFocus();
                            } catch(Exception e) {
                                if(e.toString().contains("WEAK_PASSWORD")) {
                                    mEditTextPassword.setError(getString(R.string.login_weak_password));
                                    mEditTextPassword.requestFocus();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthWeakPasswordException) {
                            mEditTextPassword.setError(getString(R.string.login_weak_password));
                            mEditTextPassword.requestFocus();
                        } else if(e instanceof FirebaseAuthInvalidCredentialsException) {
                            mEditTextEmail.setError(getString(R.string.login_bad_email));
                            mEditTextEmail.requestFocus();
                        } else if(e instanceof FirebaseAuthUserCollisionException) {
                            mEditTextEmail.setError(getString(R.string.login_user_exists));
                            mEditTextEmail.requestFocus();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
