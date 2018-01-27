package com.hazemelsawy.popularmovies.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hazemelsawy.popularmovies.R;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Cursor mCursor;

    public ReviewsAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_reviews, parent, false);
        viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            if (mCursor != null && mCursor.moveToFirst()) {
                mCursor.moveToPosition(position);
                myViewHolder.author.setText(mCursor.getString(2));
                myViewHolder.review.setText(mCursor.getString(3));
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView review;

        public MyViewHolder(final View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_author);
            review = (TextView) itemView.findViewById(R.id.tv_review);
        }
    }

}
