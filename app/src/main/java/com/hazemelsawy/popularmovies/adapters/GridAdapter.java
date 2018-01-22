package com.hazemelsawy.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hazemelsawy.popularmovies.R;
import com.squareup.picasso.Picasso;



public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private static OnItemClickListener listener;
    private Cursor mCursor;
    private static final int POSTER_KEY = 3;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            if (mCursor != null && mCursor.moveToFirst()) {
                mCursor.moveToPosition(position);
                Picasso.with(context).load("http://image.tmdb.org/t/p/w185//" +
                        mCursor.getString(POSTER_KEY)).into(myViewHolder.moviePoster);
                myViewHolder.moviePoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(view, position);
                    }
                });
            }

        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView moviePoster;

        public MyViewHolder(final View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.iv_image_grid);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

}
