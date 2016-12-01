package com.kzlabs.popularmovies;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzlabs.popularmovies.BR;
import com.kzlabs.popularmovies.model.Movie;

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
        holder.getBinding().setVariable(BR.movie, movie);
        holder.getBinding().executePendingBindings();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding mBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return mBinding;
        }
    }
}
