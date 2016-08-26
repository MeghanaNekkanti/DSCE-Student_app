package com.dsce.students.notify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText mUsnEditText, mNumberEditText;
    Spinner departmentSpinner, semesterSpinner, sectionSpinner;
    String mSection, mDept, mSemester, section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
        setOldValues();


    }

    private void init() {

        mUsnEditText = (EditText) findViewById(R.id.edit_usn);
        mNumberEditText = (EditText) findViewById(R.id.edit_number);
        departmentSpinner = (Spinner) findViewById(R.id.user_department);
        semesterSpinner = (Spinner) findViewById(R.id.semester);
        sectionSpinner = (Spinner) findViewById(R.id.section);
    }


    private void setOldValues() {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
        String usn = mSharedPreferences.getString(Constants.USN, "");
        String number = mSharedPreferences.getString(Constants.NUMBER, "");
        String department = mSharedPreferences.getString(Constants.DEPARTMENT, "");
        String semester = mSharedPreferences.getString(Constants.SEMESTER, "");
        section = mSharedPreferences.getString(Constants.SECTION, "");
        mUsnEditText.setText(usn);
        mNumberEditText.setText(number);

        ArrayAdapter<CharSequence> adapterDept = ArrayAdapter.createFromResource(this,
                R.array.department, android.R.layout.simple_spinner_item);
        adapterDept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int positionDept = adapterDept.getPosition(department);
        departmentSpinner.setSelection(positionDept);
        departmentSpinner.setAdapter(adapterDept);
        departmentSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int positionSem = adapter.getPosition(semester);
        semesterSpinner.setSelection(positionSem);
        semesterSpinner.setAdapter(adapter);
        semesterSpinner.setOnItemSelectedListener(this);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener1 =
            new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {

                    Spinner secSpinner = (Spinner) parent;
                    if (secSpinner.getId() == R.id.section)
                        mSection = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner deptSpinner = (Spinner) parent;

        if (deptSpinner.getId() == R.id.user_department)
            mDept = parent.getItemAtPosition(position).toString();

        Spinner semSpinner = (Spinner) parent;

        if (semSpinner.getId() == R.id.semester)
            mSemester = parent.getItemAtPosition(position).toString();

        String sp1 = String.valueOf(semesterSpinner.getSelectedItem());
        if (sp1.contentEquals("1") || sp1.contentEquals("2")) {

            ArrayAdapter<CharSequence> adapterCycle = ArrayAdapter.createFromResource(this,
                    R.array.cycle, android.R.layout.simple_spinner_item);
            adapterCycle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            int positionSec = adapterCycle.getPosition(section);
            sectionSpinner.setSelection(positionSec);
            sectionSpinner.setAdapter(adapterCycle);
            sectionSpinner.setOnItemSelectedListener(onItemSelectedListener1);

        } else {

            ArrayAdapter<CharSequence> adapterSec = ArrayAdapter.createFromResource(this,
                    R.array.section, android.R.layout.simple_spinner_item);
            adapterSec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            int positionSec = adapterSec.getPosition(section);
            sectionSpinner.setSelection(positionSec);
            sectionSpinner.setAdapter(adapterSec);
            sectionSpinner.setOnItemSelectedListener(onItemSelectedListener1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void updateDetails(View view) {

        String newUsn = mUsnEditText.getText().toString();
        String newNumber = mNumberEditText.getText().toString();

        FirebaseUser mFirebaseUser;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid= mFirebaseUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Students");

        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("Usn",newUsn);
        hashMap.put("Number",newNumber);
        hashMap.put("Department",mDept);
        hashMap.put("Semester",mSemester);
        hashMap.put("Section",mSection);

        myRef.child(userUid).updateChildren(hashMap);

        PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this)
                .edit()
                .putString(Constants.SEMESTER, mSemester)
                .putString(Constants.SECTION, mSection)
                .putString(Constants.DEPARTMENT, mDept)
                .putString(Constants.NUMBER, newNumber)
                .putString(Constants.USN,newUsn)
                .apply();
        Log.d("TAG", "userProfile: "+newUsn);

        Intent intent=new Intent(EditProfileActivity.this,NewsFeedActivity.class);
        startActivity(intent);




    }
}
