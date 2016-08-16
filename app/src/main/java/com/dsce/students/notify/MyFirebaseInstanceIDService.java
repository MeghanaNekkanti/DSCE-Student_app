package com.dsce.students.notify;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Rohan on 8/1/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TAG", "Refreshed token: " + refreshedToken);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putString("FIREBASE_CLOUD_MESSAGING_TOKEN",refreshedToken).apply();

        super.onTokenRefresh();
    }
}
