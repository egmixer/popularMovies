package com.hazemelsawy.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Hazem on 10/13/2017.
 */


public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MovieModel.Result> contentList = new ArrayList<>();
    private Context context;

    public GridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.poster_grid, parent, false);
        viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            Picasso.with(context)
                    .load("http://image.tmdb.org/t/p/w185//" + contentList.get(position).getPosterPath())
                    .into(((MyViewHolder) holder).moviePoster);
        }
    }

    public void update(ArrayList<MovieModel.Result> contentList) {
        this.contentList = contentList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView moviePoster;

        public MyViewHolder(final View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.iv_image_grid);
            moviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieModel.Result movie = contentList.get(getLayoutPosition());
                    Intent i = new Intent(context, DetailsActivity.class);
                    String movieTitle = movie.getOriginalTitle();
                    i.putExtra("title", movieTitle);
                    String poster = movie.getPosterPath();
                    i.putExtra("poster_url", poster);
                    String date = movie.getReleaseDate();
                    i.putExtra("date", date);
                    Float movieRate = movie.getVoteAverage();
                    i.putExtra("vote_average", movieRate);
                    String overView = movie.getOverview();
                    i.putExtra("overview", overView);
                    context.startActivity(i);
                }
            });
        }
    }

}
