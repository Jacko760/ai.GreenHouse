package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.ktx.Firebase;

public class SigninActivity extends AppCompatActivity {
    private EditText etEmailAdress, etMobileNumber, etPassword, etConfirmpassword;
    public TextView tvSignIn;
    private Button btnRegister, btnGoogle;
    private FirebaseAuth maAuth;


    private static final int RC_SIGN_IN = 1;

    GoogleSignInClient mGoogleSignInClient;

    Dialog dialog;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        etEmailAdress = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmpassword = findViewById(R.id.etConfirmPassword);
        tvSignIn = findViewById(R.id.tvSignin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        progressBar = findViewById(R.id.pbLoading);
        maAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        dialog = new Dialog(SigninActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(etEmailAdress.getText().toString());
                String phone = String.valueOf(etMobileNumber.getText().toString());
                String password = String.valueOf(etPassword.getText().toString());
                String confirmPassword = String.valueOf(etConfirmpassword.getText().toString());
                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(email)) {
                    etEmailAdress.setError("Email is required");
                    etEmailAdress.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    etMobileNumber.setError("Mobile Number is required");
                    etMobileNumber.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmailAdress.setError("Please enter a valid email address");
                    etEmailAdress.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    etMobileNumber.setError("Please enter a valid email address");
                    etMobileNumber.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    etConfirmpassword.setError("Please confirm your password");
                    etConfirmpassword.requestFocus();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    etConfirmpassword.setError("Passwords do not match");
                    etConfirmpassword.requestFocus();
                    return;
                }

                maAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SigninActivity.this, "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SigninActivity.this, BottomNavActivity.class);
                                    startActivity(i);
                                    finish();


                                } else {
                                    Toast.makeText(SigninActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SigninActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                dialog.dismiss();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {


        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        maAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = maAuth.getCurrentUser();
                            Intent i = new Intent(SigninActivity.this, BottomNavActivity.class);
                            startActivity(i);
                            finish();
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(SigninActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
