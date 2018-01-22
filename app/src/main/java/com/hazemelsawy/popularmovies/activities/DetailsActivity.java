package com.hazemelsawy.popularmovies.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.hazemelsawy.popularmovies.R;
import com.hazemelsawy.popularmovies.fragments.DetailsFragment;



public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);
        if (detailsFragment != null) {
            detailsFragment.getMovie((Uri) getIntent().getExtras().getParcelable(MainActivity.DETAIL_URI));
        }
    }
}
