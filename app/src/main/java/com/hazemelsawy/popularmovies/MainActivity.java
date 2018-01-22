package com.hazemelsawy.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView moviesGrid;
    private GridAdapter gridAdapter;
    private NetworkUtil networkUtil;
    private Call<MovieModel> moviesCall = null;
    private ArrayList<MovieModel.Result> moviesResponse = new ArrayList<>();
    private String category = "popular";
    private static final String API_KEY = BuildConfig.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviesGrid = (RecyclerView) findViewById(R.id.rv_posters);
        networkUtil = NetworkUtil.getInstance();
        moviesGrid.setLayoutManager(new GridLayoutManager(this, 2));
        moviesGrid.setHasFixedSize(true);
        gridAdapter = new GridAdapter(this);
        moviesGrid.setAdapter(gridAdapter);
        getMovies(category);
    }

    private void getMovies(String category) {
        final Context context = getApplicationContext();
        moviesCall = networkUtil.getServices().getMovies(category, API_KEY);
        moviesCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                moviesResponse = response.body().getResults();
                gridAdapter.update(moviesResponse);
                moviesGrid.scrollToPosition(0);
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Toast.makeText(context, "No internet connection, please connect and try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.popular) {
            getMovies("popular");
            Toast.makeText(this, "popular", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.rated) {
            getMovies("top_rated");
            Toast.makeText(this, "top_rated", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
