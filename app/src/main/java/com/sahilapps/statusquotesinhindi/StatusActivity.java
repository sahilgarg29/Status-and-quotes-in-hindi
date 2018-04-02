package com.sahilapps.statusquotesinhindi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {

    private TextView statusTextView;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ImageButton shareButton;
    private Status status;
    private ImageButton copyToClipboardButton;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        String categoryKey = intent.getStringExtra("category-key");
        String statusKey = intent.getStringExtra("status-key");

        statusTextView = findViewById(R.id.tv_status);
        shareButton = findViewById(R.id.share_button);
        copyToClipboardButton = findViewById(R.id.copy_to_clipboard);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstistial_ad_unit_id));

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("status").child(categoryKey).child(statusKey);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(Status.class);
                statusTextView.setText(status.getText());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("StatusActivity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addValueEventListener(listener);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, status.getText());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "sahil");
                startActivity(Intent.createChooser(shareIntent, "share via"));
            }
        });

        copyToClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("status", status.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(StatusActivity.this, "Status Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AdRequest interstistialadRequest = new AdRequest.Builder().build();

        interstitialAd.loadAd(interstistialadRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });
    }


}
