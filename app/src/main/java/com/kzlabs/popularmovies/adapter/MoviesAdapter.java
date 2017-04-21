package com.kzlabs.popularmovies.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kzlabs.popularmovies.fragment.MovieFragment;
import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.util.BindingUtils;

import java.util.List;

/**
 * Created by radsen on 11/28/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List movieList;
    private MovieFragment onItemClickListener;

    public MoviesAdapter(List<Movie> movieList){
        this.movieList = movieList;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, final int position) {
        final Movie movie = (Movie) movieList.get(position);
        if(movie.getBitmap() != null){
            holder.ivPoster.setImageBitmap(movie.getBitmap());
        } else {
            BindingUtils.loadImage(holder.ivPoster, movie.getPoster());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setOnItemClickListener(MovieFragment onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void swapData(List<Movie> movies) {
        movieList = movies;
        notifyDataSetChanged();
    }

    public Movie get(int position) {
        return (Movie) movieList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding mBinding;
        private final ImageView ivPoster;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);

            mBinding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return mBinding;
        }
    }
}
