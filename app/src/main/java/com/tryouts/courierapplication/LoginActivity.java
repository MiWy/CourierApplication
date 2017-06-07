package com.tryouts.courierapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPass;
    private String mEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        // If the objects getcurrentuser method is not null
        // then user is already signed in

        // Quicker and worse way of checking whether there is a user signed in.
        // If Activity will be paused and them resumed with onResume(), then there will be no
        // onCreate() call, and there will be no check. So if user is not signed in at that moment
        // Houston we have a problem. Solution from RegisterActivity is better :).
        if(mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        mEditTextEmail = (EditText) findViewById(R.id.login_edittext_email);
        mEditTextPass = (EditText) findViewById(R.id.login_edittext_pass);
        Button mButtonLogin = (Button) findViewById(R.id.login_button_login);
        TextView mTextViewNotYet = (TextView) findViewById(R.id.login_textview_not_yet);
        TextView mTextViewForgot = (TextView) findViewById(R.id.login_textview_forgot_pass);
        mProgressDialog = new ProgressDialog(this);

        // Signing in Button's listener.
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        // If user doesn't have an account, go to RegisterActivity
        mTextViewNotYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        // If user forgot his password, send a default e-mail with reset instructions.
        mTextViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Show Toast based on whether password reset email was sent or not
                        if(task.isSuccessful()) {
                            Toast toast = Toast.makeText(LoginActivity.this, getString(
                                    R.string.login_forgot_pass_toast), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(LoginActivity.this, getString(
                                    R.string.login_forgot_pass_toast_unsucc), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
            }
        });
    }

    private void userLogin() {
        mEmail = mEditTextEmail.getText().toString().trim();
        String mPass = mEditTextPass.getText().toString().trim();
        if(TextUtils.isEmpty(mEmail)){
            Toast.makeText(this,getString(R.string.login_hint_email), Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPass)){
            Toast.makeText(this,getString(R.string.login_hint_password),Toast.LENGTH_LONG).show();
            return;
        }
        mProgressDialog.setMessage(getString(R.string.login_progressbar_login));
        mProgressDialog.show();
        //Signing the user in
        mAuth.signInWithEmailAndPassword(mEmail, mPass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if(task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                });

    }
}
