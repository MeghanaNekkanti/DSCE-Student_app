package com.example.meghana.testing;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView textView, textView1;
    RecyclerView recyclerView;
    ArrayList<String> text = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<String> dname = new ArrayList<>();
    ArrayList<String> fname = new ArrayList<>();

    private long backPressedTime = 0;
    private GoogleApiClient client;
    private NotificationManager mNotifyManager;
    private Notification.Builder build;
    int id = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //recycler view

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        final UpdateAdapter adapter = new UpdateAdapter();
        recyclerView.setAdapter(adapter);


        //UIL initialisation
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);


        // get items from previous activity

        String name = getSharedPreferences("login", Context.MODE_PRIVATE).getString("name", "123456789");
        String email = getSharedPreferences("login", Context.MODE_PRIVATE).getString("email", "123456789");
        String image = getSharedPreferences("login", Context.MODE_PRIVATE).getString("imageurl", "");
        final String sem = getSharedPreferences("login", Context.MODE_PRIVATE).getString("sem", "123456789");
        final String sec = getSharedPreferences("login", Context.MODE_PRIVATE).getString("sec", "123456789");
        final String dept = getSharedPreferences("login", Context.MODE_PRIVATE).getString("dept", "123456789");
        // getSharedPreferences("login", Context.MODE_PRIVATE).getString("number", "123456789");

        Log.d("url", "onCreate: " + image);

        //image display
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .build();

        //nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        //set
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textView = (TextView) view.findViewById(R.id.textView);
        textView1 = (TextView) view.findViewById(R.id.textView1);

        if (!(image == null))
            imageLoader.displayImage(image, imageView, options);

        textView.setText(name);
        textView1.setText(email);

        //firebase retrieving

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Post");
        Log.d("megs", "onCreate: " + myRef.child(dept));


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                text.clear();
                images.clear();
                type.clear();
                dname.clear();
                fname.clear();
                Log.d("testm", "onDataChange: idiot" + dataSnapshot.child(dept).child(sem).child(sec).child("type").getValue());
                for (DataSnapshot user : dataSnapshot.child(dept).child(sem).child(sec).getChildren()) {
                    Log.d("testm", "onDataChange: idiot");

                    Log.d("test", "onDataChange: " + user.child("name").getValue());

                    images.add(0,user.child("Url").getValue() + "");
                    text.add(0,user.child("text").getValue() + "");
                    type.add(0,user.child("type").getValue() + "");
                    dname.add(0,user.child("name").getValue() + "");
                    fname.add(0,user.child("filename").getValue() + "");


                }

                adapter.notifyItemInserted(0);
                adapter.notifyDataSetChanged();


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // forestRef.


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            long t = System.currentTimeMillis();
            if (t - backPressedTime > 2000) {
                backPressedTime = t;
                Toast.makeText(this, "Press back again to exit",
                        Toast.LENGTH_SHORT).show();
            } else {
                // clean up
                super.onBackPressed();
            }
        }
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NavDrawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.meghana.testing/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NavDrawer Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.meghana.testing/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.Holder> {


        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_card, parent, false);
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            holder.tv.setText(text.get(position));
            holder.displayname.setText("POSTED BY:" + dname.get(position));
            Log.d("test", "onBindViewHolder: " + type.get(position));


            if (type.get(position).equals("text")) {

                holder.file_name.setVisibility(View.GONE);
                holder.img.setVisibility(View.GONE);
                //  holder.button.setVisibility(View.GONE);
            } else if (type.get(position).equals("image")) {

                ImageLoader imagedisplay = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .build();
                holder.file_name.setText(fname.get(position));

                imagedisplay.displayImage(images.get(position), holder.img, options);

            } else {
                holder.file_name.setText(fname.get(position));
                holder.img.setImageResource(R.drawable.ic_menu_send);
            }

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(NavDrawerActivity.this)
                            .setTitle("Download")
                            .setMessage(" Click OK to download")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    notificationbar(fname.get(position));

                                    new download().execute(images.get(position), fname.get(position));
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return text.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView tv, file_name, displayname;
            // Button button;


            public Holder(View itemView) {
                super(itemView);
                img = (ImageView) itemView.findViewById(R.id.getimage);
                tv = (TextView) itemView.findViewById(R.id.gettext);
                // button = (Button) itemView.findViewById(R.id.download);
                displayname = (TextView) itemView.findViewById(R.id.displayname);
                file_name = (TextView) itemView.findViewById(R.id.name);
            }
        }

    }

    private void notificationbar(String s) {

        String filePath = Environment.getExternalStorageDirectory() + "/download/" + s;
        File file = new File(filePath);
        Log.d("test.APP_TAG ","File to download = " + String.valueOf(file));
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext=file.getName().substring(file.getName().lastIndexOf(".")+1);
        String type = mime.getMimeTypeFromExtension(ext);
        Intent openFile = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        openFile.setDataAndType(Uri.fromFile(file), type);
        openFile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, openFile, 0);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        build = new Notification.Builder(NavDrawerActivity.this);
        build.setContentTitle("Download")
                .setContentText("Download in progress")
                .setContentIntent(mPendingIntent)
                .setSmallIcon(R.drawable.ic_menu_send)
                 .setAutoCancel(true);



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


}
