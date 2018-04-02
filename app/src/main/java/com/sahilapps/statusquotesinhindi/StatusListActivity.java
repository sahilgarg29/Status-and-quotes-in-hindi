package com.sahilapps.statusquotesinhindi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusListActivity extends AppCompatActivity {

    private RecyclerView statusListRecyclerView;
    private AdView statusBannerAdView;
    private FirebaseDatabase statusdatabase;
    private DatabaseReference statusReference;
    private FirebaseRecyclerAdapter statusfirebaseRecyclerAdapter;
    private InterstitialAd interstitialAd;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);

        Intent intent = getIntent();
        final String key = intent.getStringExtra("key");



        statusBannerAdView = findViewById(R.id.status_adView);
        progressBar = findViewById(R.id.bar);
        statusListRecyclerView = findViewById(R.id.rv_status_list);
        statusdatabase = FirebaseDatabase.getInstance();
        statusReference = statusdatabase.getReference("status").child(key);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstistial_ad_unit_id));


        FirebaseRecyclerOptions<Status> option =
                new FirebaseRecyclerOptions.Builder<Status>()
                        .setQuery(statusReference, Status.class)
                        .build();


        statusfirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Status, StatusListActivity.StatusViewHolder>(
                option
        ) {
            @Override
            protected void onBindViewHolder(@NonNull StatusListActivity.StatusViewHolder holder, int position, @NonNull Status model) {
                holder.textView.setText(model.getText());
                progressBar.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public StatusListActivity.StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.status_list_item, parent, false);

                final StatusListActivity.StatusViewHolder statusViewHolder = new StatusListActivity.StatusViewHolder(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String statusKey = getRef(statusViewHolder.getAdapterPosition()).getKey();
                        Intent statusintent = new Intent(StatusListActivity.this, StatusActivity.class);
                        statusintent.putExtra("category-key", key);
                        statusintent.putExtra("status-key", statusKey);
                        startActivity(statusintent);
                    }
                });


                return statusViewHolder;
            }
        };

        statusListRecyclerView.setHasFixedSize(true);
        statusListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statusListRecyclerView.setAdapter(statusfirebaseRecyclerAdapter);
        statusfirebaseRecyclerAdapter.startListening();


        AdRequest adRequest = new AdRequest.Builder().build();

        statusBannerAdView.loadAd(adRequest);



    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public StatusViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_status_item_name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusfirebaseRecyclerAdapter.stopListening();
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
