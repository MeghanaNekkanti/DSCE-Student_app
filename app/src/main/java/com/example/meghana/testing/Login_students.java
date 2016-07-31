package com.example.meghana.testing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Login_students extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "testing";
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String student_uid, Usn, item, section, Dept,num;
    EditText get_usn, number;
    Spinner spinner1, spinner2,get_dept;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_students);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        get_usn = (EditText) findViewById(R.id.enterusn);
        number = (EditText) findViewById(R.id.number);
        spinner1 = (Spinner) findViewById(R.id.semester);
        spinner2 = (Spinner) findViewById(R.id.Section);
        get_dept= (Spinner) findViewById(R.id.dept);

        ArrayAdapter<CharSequence> adapterdept = ArrayAdapter.createFromResource(this,
                R.array.department, android.R.layout.simple_spinner_item);
        adapterdept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        get_dept.setAdapter(adapterdept);
        get_dept.setPrompt("CHOOSE DEPARTMENT");
        get_dept.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setPrompt("CHOOSE SEMESTER");
        spinner1.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adaptersec = ArrayAdapter.createFromResource(this,
                R.array.section, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setPrompt("CHOOSE SECTION");
        spinner2.setAdapter(adaptersec);
        spinner2.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // User is signed in
                    student_uid = user.getUid();
                    Log.d("user", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("signout", "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Login_students.this, "Connection failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {

        Usn = get_usn.getText().toString();
        num = number.getText().toString();
        if (Usn.length() == 10 && Usn.substring(0, 3).equals("1ds")) {
            progressDialog = new ProgressDialog(Login_students.this);
            progressDialog.setMessage(" Signing in");
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            signIn();

            // Log.d("check",);

        } else {
            get_usn.requestFocus();
            get_usn.setError(" Enter proper usn");
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("signinsucess", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);

            Log.d("details", acct.getEmail() + " " + acct.getDisplayName());
        } else {

            Toast.makeText(Login_students.this, "Failed,try again", Toast.LENGTH_SHORT).show();
            // Signed out, show unauthenticated UI.
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("email", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("complete", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("fail", "signInWithCredential", task.getException());
                            Toast.makeText(Login_students.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login_students.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();

//firebase database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Students");


                            myRef.child(student_uid).child("Usn").setValue(Usn);
                            myRef.child(student_uid).child("Email").setValue(acct.getEmail());
                            myRef.child(student_uid).child("Semester").setValue(item);
                            myRef.child(student_uid).child("Section").setValue(section);
                            myRef.child(student_uid).child("Dept").setValue(Dept);
                            myRef.child(student_uid).child("Number").setValue(num);

//intent and shared preferences

                            Intent intent = new Intent(Login_students.this, NavDrawerActivity.class);
                            intent.putExtra("name", acct.getDisplayName());
                            intent.putExtra("email", acct.getEmail());
                            intent.putExtra("semester", item);
                            intent.putExtra("section", section);
                            intent.putExtra("dept", Dept);
                            intent.putExtra("number", num);
                            String image = acct.getPhotoUrl() + "";
                            intent.putExtra("image", image);
                            Log.d(TAG, "onComplete: " + image);
                            String Response = "true";
                            progressDialog.dismiss();
                            startActivity(intent);
                            SharedPreferences.Editor editor = getSharedPreferences("login", Context.MODE_PRIVATE).edit();
                            editor.putString("name", acct.getDisplayName());
                            editor.putString("email", acct.getEmail());
                            editor.putString("sem", item);
                            editor.putString("sec", section);
                            editor.putString("dept", Dept);
                            editor.putString("number", num);
                            editor.putString("imageurl", image);
                            editor.putString("login", Response);
                            editor.apply();
                            finish();


                        }
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner1 = (Spinner) parent;
        Spinner spinner2 = (Spinner) parent;
        Spinner spinner3 = (Spinner) parent;
        if (spinner1.getId() == R.id.semester)
            item = parent.getItemAtPosition(position).toString();
        if (spinner2.getId() == R.id.Section)
            section = parent.getItemAtPosition(position).toString();
        if (spinner3.getId() == R.id.dept)
            Dept = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}


