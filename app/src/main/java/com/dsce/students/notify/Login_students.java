package com.dsce.students.notify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.Serializable;
import java.util.HashMap;

public class Login_students extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "testing";
    public static GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String studentUid, Usn, semester, section, Dept, num;
    EditText get_usn, number;
    Spinner spinner1, spinner2, getDept;
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
        getDept = (Spinner) findViewById(R.id.dept);

        ArrayAdapter<CharSequence> adapterDept = ArrayAdapter.createFromResource(this,
                R.array.department, android.R.layout.simple_spinner_item);
        adapterDept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getDept.setAdapter(adapterDept);
        getDept.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(this);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    studentUid = user.getUid();

                    Log.d("user", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
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
            progressDialog.dismiss();
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


//firebase database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Students");

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Usn", Usn);
                            hashMap.put("Number", num);
                            hashMap.put("Department", Dept);
                            hashMap.put("Semester", semester);
                            hashMap.put("Section", section);
                            hashMap.put("Email", acct.getEmail());
                            hashMap.put("Token", PreferenceManager.getDefaultSharedPreferences(Login_students.this).getString("FIREBASE_CLOUD_MESSAGING_TOKEN", "NA"));

                            myRef.child(studentUid).setValue(hashMap);
//intent and shared preferences


                            Intent intent = new Intent(Login_students.this, NewsFeedActivity.class);
                            intent.putExtra("name", acct.getDisplayName());
                            intent.putExtra("email", acct.getEmail());
                            intent.putExtra("semester", semester);
                            intent.putExtra("section", section);
                            intent.putExtra("dept", Dept);
                            intent.putExtra("number", num);
                            String image = acct.getPhotoUrl() + "";
                            intent.putExtra("image", image);
                            Log.d(TAG, "onComplete: " + image);
                            String Response = "true";
                            progressDialog.dismiss();
                            startActivity(intent);

                            /*SharedPreferences.Editor editor = getSharedPreferences("login", Context.MODE_PRIVATE).edit();
                            editor.putString("name", acct.getDisplayName());
                            editor.putString("email", acct.getEmail());
                            editor.putString("sem", item);
                            editor.putString("sec", section);
                            editor.putString("dept", Dept);
                            editor.putString("number", num);
                            editor.putString("imageurl", image);
                            editor.putString("login", Response);
                            editor.apply();*/


                            PreferenceManager.getDefaultSharedPreferences(Login_students.this)
                                    .edit()
                                    .putString(Constants.NAME, acct.getDisplayName())
                                    .putString(Constants.EMAIL, acct.getEmail())
                                    .putString(Constants.SEMESTER, semester)
                                    .putString(Constants.SECTION, section)
                                    .putString(Constants.DEPARTMENT, Dept)
                                    .putString(Constants.NUMBER, num)
                                    .putString(Constants.IMAGE, image)
                                    .putString(Constants.USN, Usn)
                                    .putBoolean(Constants.LOGIN_PREF, true)
                                    .apply();

                            finish();

                        }
                    }
                });

    }

    AdapterView.OnItemSelectedListener onItemSelectedListener1 =
            new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {

                    Spinner spinner2 = (Spinner) parent;
                    if (spinner2.getId() == R.id.Section)
                        section = parent.getItemAtPosition(position).toString();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        Spinner spinner3 = (Spinner) parent;

        if (spinner3.getId() == R.id.dept)
            Dept = parent.getItemAtPosition(position).toString();
        if (spinner.getId() == R.id.semester)
            semester = parent.getItemAtPosition(position).toString();

        String sp1 = String.valueOf(spinner1.getSelectedItem());
//        Log.d(TAG, "onItemSelected: "+sp1);
        if (sp1.contentEquals("1") || sp1.contentEquals("2")) {
            ArrayAdapter<CharSequence> adapterCycle = ArrayAdapter.createFromResource(this,
                    R.array.cycle, android.R.layout.simple_spinner_item);
            adapterCycle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapterCycle);
            spinner2.setOnItemSelectedListener(onItemSelectedListener1);

        } else {

            ArrayAdapter<CharSequence> adapterSec = ArrayAdapter.createFromResource(this,
                    R.array.section, android.R.layout.simple_spinner_item);
            adapterSec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapterSec);
            spinner2.setOnItemSelectedListener(onItemSelectedListener1);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}




