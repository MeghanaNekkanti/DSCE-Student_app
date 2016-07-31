package com.example.meghana.testing;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {

    RecyclerView recyclerView;

    ArrayList<DataModel> list = new ArrayList<>();

    RecAdapter adapter;

    String department, semester, section;

    DisplayImageOptions defaultOptions;

    ImageLoaderConfiguration config;

    private NotificationManager mNotifyManager;
    private Notification.Builder build;
    int id = 1;

    public NotesFragment() {
        // Required empty public constructor
    }

    public static NotesFragment getFragment() {
        return new NotesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =

                inflater.inflate(R.layout.fragment_notes, container, false);

        fetchFromSharedPrefs();

        fetchData();

        init(view);

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

                    if (user.child("type").getValue(String.class).equals("image")
                            || user.child("type").getValue(String.class).equals("file")) {

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
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecAdapter();
        recyclerView.setAdapter(adapter);

        //UIL initialisation
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        config = new ImageLoaderConfiguration.Builder(
                getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

    }

    private class RecAdapter extends RecyclerView.Adapter<RecAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(getActivity()).inflate(R.layout.single_notes_view, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {

            holder.teachersTv.setText(list.get(position).getTeachersName());
            holder.contentTv.setText(list.get(position).getText());

            if (list.get(position).getType().equals("file")) {
                holder.imageView.setImageResource(R.drawable.file_icon);
            } else {

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(list.get(position).getImage(), holder.imageView, defaultOptions);

            }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Download")
                            .setMessage(" Click OK to download")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    notificationBar(list.get(position).getFilename());

                                    new download().execute(list.get(position).getImage(), list.get(position).getFilename());
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView teachersTv, contentTv;

            public Holder(View itemView) {
                super(itemView);

                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                teachersTv = (TextView) itemView.findViewById(R.id.teachersNameTV);
                contentTv = (TextView) itemView.findViewById(R.id.contentTv);

            }
        }

    }

    public class download extends AsyncTask<String, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            build.setProgress(100, 0, false);
            mNotifyManager.notify(id, build.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            build.setProgress(100, values[0], false);
            mNotifyManager.notify(id, build.build());
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(String... params) {


            String file_path = Environment.getExternalStorageDirectory() +
                    "/download";
            Log.d("Done", file_path);
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            String pdf_file = file_path + "/" + params[1];
            Log.d("megsd", "doInBackground: " + params[1]);
            File file = new File(pdf_file);

            try {
                file.createNewFile();
                URL url = new URL(params[0]);
                Log.d("megsd", "onClick: " + params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[totalSize];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
                Log.d("megsd", "doInBackground:success ");
//                Toast.makeText(NavDrawerActivity.this, "Downloaded successfully", Toast.LENGTH_SHORT).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            build.setContentText("Download complete");
            // Removes the progress bar
            build.setProgress(0, 0, false);
            mNotifyManager.notify(id, build.build());
        }
    }


    private void notificationBar(String s) {

        String filePath = Environment.getExternalStorageDirectory() + "/download/" + s;
        File file = new File(filePath);
        Log.d("test.APP_TAG ", "File to download = " + String.valueOf(file));
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);


        Intent openFile = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        openFile.setDataAndType(Uri.fromFile(file), type);
        openFile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), 0, openFile, 0);

        mNotifyManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        build = new Notification.Builder(getActivity());
        build.setContentTitle("Download")
                .setContentText("Download in progress")
                .setContentIntent(mPendingIntent)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setAutoCancel(true);


    }

}
