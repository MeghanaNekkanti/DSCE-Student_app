package com.example.meghana.testing;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CircularsFragment extends Fragment {

    ArrayList<DataModel> list = new ArrayList<>();

    RecyclerView recyclerView;

    RecAdapter adapter;

    String department, semester, section;


    public CircularsFragment() {
        // Required empty public constructor
    }

    public static CircularsFragment getFragment() {
        return new CircularsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =
                inflater.inflate(R.layout.fragment_circulars, container, false);

        fetchFromSharedPrefs();

        init(view);

        fetchData();

        return view;
    }

    private void fetchFromSharedPrefs() {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        department = mSharedPreferences.getString(Constants.DEPARTMENT, "NA");
        semester = mSharedPreferences.getString(Constants.SEMESTER, "NA");
        section = mSharedPreferences.getString(Constants.SECTION, "NA");

    }

    private void fetchData() {

        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("Post");

        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot user : dataSnapshot.child(department).child(semester).child(section).getChildren()) {
                    Log.d("testm", "onDataChange: idiot");

                    Log.d("test", "onDataChange: " + user.child("name").getValue());

                    if (user.child("type").getValue(String.class).equals("text")) {

                        DataModel dataModel = new DataModel(user.child("Url").getValue(String.class),
                                user.child("text").getValue(String.class),
                                user.child("type").getValue(String.class),
                                user.child("filename").getValue(String.class),
                                user.child("name").getValue(String.class));

                        list.add(dataModel);

                    }
                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void init(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleview);

        adapter = new RecAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }

    private class RecAdapter extends RecyclerView.Adapter<RecAdapter.Holder> {
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getActivity()).inflate(R.layout.single_circular_view, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            holder.teachersNameTv.setText(list.get(position).getTeachersName());
            holder.contentTv.setText(list.get(position).getText());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView teachersNameTv, contentTv;

            public Holder(View itemView) {
                super(itemView);

                teachersNameTv = (TextView) itemView.findViewById(R.id.teachersNameTV);
                contentTv = (TextView) itemView.findViewById(R.id.content);

            }
        }
    }


}
