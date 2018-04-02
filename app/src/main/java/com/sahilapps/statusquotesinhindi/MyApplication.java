package com.sahilapps.statusquotesinhindi;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, "ca-app-pub-9920563533082485~9322979305");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
