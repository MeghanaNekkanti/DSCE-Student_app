package com.dsce.students.notify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class UserProfileActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName, userMail, userNumber, userUsn, userSemSec, userDepartment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userImage = (ImageView) findViewById(R.id.user_image);

        userName = (TextView) findViewById(R.id.user_name);
        userMail = (TextView) findViewById(R.id.user_mail);
        userNumber = (TextView) findViewById(R.id.user_number);
        userUsn = (TextView) findViewById(R.id.user_usn);
        userDepartment = (TextView) findViewById(R.id.user_department);
        userSemSec = (TextView) findViewById(R.id.user_sem_sec);

        userProfile();


    }

    private void userProfile() {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String image = mSharedPreferences.getString(Constants.IMAGE, "");
        String name = mSharedPreferences.getString(Constants.NAME, "");
        String email = mSharedPreferences.getString(Constants.EMAIL, "");
        String Usn = mSharedPreferences.getString(Constants.USN, "");
        String number = mSharedPreferences.getString(Constants.NUMBER, "");
        String department = mSharedPreferences.getString(Constants.DEPARTMENT, "");
        String section = mSharedPreferences.getString(Constants.SECTION, "");
        String semester = mSharedPreferences.getString(Constants.SEMESTER, "");

        //UIL initialisation
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                UserProfileActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .build();


        if (!(image.equals("")))
            imageLoader.displayImage(image, userImage, options);
        else
            userImage.setImageResource(R.drawable.profilepic);

        userName.setText(name);
        userMail.setText(email);
        userNumber.setText(number);
        userUsn.setText(Usn);
        userDepartment.setText(department);
        userSemSec.setText(semester + " / " + section);


    }
}
