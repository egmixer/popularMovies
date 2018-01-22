package com.hazemelsawy.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hazemelsawy.popularmovies.PrefUtil;
import com.hazemelsawy.popularmovies.R;
import com.hazemelsawy.popularmovies.fragments.DetailsFragment;
import com.hazemelsawy.popularmovies.fragments.ListFragment;

public class MainActivity extends AppCompatActivity implements ListFragment.MovieClickListener {
    public static final String DETAIL_URI = "URI";
    ListFragment listFragment;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listFragment = ListFragment.newInstance();
        PrefUtil.with(this);
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
        listFragment.setMovieCall(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String category = PrefUtil.getStringFromPref("category");
        // update sort by in our second pane using the fragment manager
        if (category != mCategory) {
            ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
            if (null != listFragment) {
                listFragment.fetchMoviesByCategory();
            }
            mCategory = category;
        }
    }

    @Override
    public void onMovieSelected(Uri uri) {
        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);
        if (detailsFragment != null) {
            listFragment.fetchMoviesByCategory();
            detailsFragment.getMovie(uri);
        } else {
            Bundle args = new Bundle();
            args.putParcelable(DETAIL_URI, uri);
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }
    }
}
