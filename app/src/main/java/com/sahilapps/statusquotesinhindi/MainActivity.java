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

public class MainActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private RecyclerView categoriesRecyclerView;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private InterstitialAd interstitialAd;
    private FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> firebaseRecyclerAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        categoriesRecyclerView = findViewById(R.id.rv_categories);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("categories");
        progressBar = findViewById(R.id.main_progressbar);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstistial_ad_unit_id));



        FirebaseRecyclerOptions<CategoryItem> options =
                new FirebaseRecyclerOptions.Builder<CategoryItem>()
                        .setQuery(reference, CategoryItem.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull CategoryItem model) {
                holder.textView.setText(model.getName());
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("name", model.getName());
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_list_item, parent, false);

                final CategoryViewHolder viewHolder = new CategoryViewHolder(view);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = getRef(viewHolder.getAdapterPosition()).getKey();
                        Intent intent = new Intent(MainActivity.this, StatusListActivity.class);
                        intent.putExtra("key", key);
                        startActivity(intent);
                    }
                });

                return viewHolder;
            }
        };

        categoriesRecyclerView.setHasFixedSize(true);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        bannerAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        bannerAdView.loadAd(adRequest);



    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_category_item_name);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();

    }
}
