package com.hazemelsawy.popularmovies.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hazemelsawy.popularmovies.R;
import com.hazemelsawy.popularmovies.fragments.DetailsFragment;


public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    DetailsFragment detailsFragment;
    private Uri movieUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);
            movieUri = getIntent().getExtras().getParcelable(MainActivity.DETAIL_URI);
        } else {
            detailsFragment = (DetailsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "details_fragment");
            movieUri = savedInstanceState.getParcelable("detail_uri");
        }
        detailsFragment.getMovie(movieUri);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("detail_uri", movieUri);
        getSupportFragmentManager().putFragment(outState, "details_fragment", detailsFragment);
    }
}
