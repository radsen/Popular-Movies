package com.kzlabs.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kzlabs.popularmovies.model.Comment;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.util.BindingUtils;

/**
 * Created by radsen on 4/7/17.
 */

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.Section> {

    private static final int SECTION_TRAILER_INDEX = 0;
    private static final int SECTION_MOVIE_INDEX = 1;
    private static final int SECTION_REVIEW_INDEX = 3;
    private static final int SECTION_TITLE_INDEX = 2;

    private static final int HEADER = 1;
    private static final int SECTIONS_BEFORE_REVIEWS = SECTION_REVIEW_INDEX;

    private final FragmentManager mChildManager;
    private final FavListener mListener;

    private Movie mMovie;
    private final Context mContext;
    private ViewPager.OnPageChangeListener mPageListener;

    interface FavListener {
        void onClick(View view, long id);
    }

    public MovieDetailAdapter(Context context, FragmentManager childFragmentManager,
                              Movie movie, FavListener listener,
                              ViewPager.OnPageChangeListener pageChangeListener){
        mChildManager = childFragmentManager;
        mContext = context;
        mMovie = movie;
        mListener = listener;
        mPageListener = pageChangeListener;
    }

    @Override
    public MovieDetailAdapter.Section onCreateViewHolder(ViewGroup parent, int viewType) {
        Section sectionViewHolder = null;

        View view = null;
        switch (viewType){
            case SECTION_TRAILER_INDEX:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detail_section_trailer, parent, false);
                sectionViewHolder = new STrailer(view);
                break;
            case SECTION_MOVIE_INDEX:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detail_section_movie, parent, false);
                sectionViewHolder = new SMovie(view);
                break;
            case SECTION_TITLE_INDEX:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_title, parent, false);
                sectionViewHolder = new STitle(view);
                break;
            case SECTION_REVIEW_INDEX:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detail_section_reviews, parent, false);
                sectionViewHolder = new Review(view);
                break;
        }

        return sectionViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieDetailAdapter.Section holder, int position) {
        if(holder instanceof STrailer) {
            STrailer trailerHolder = (STrailer) holder;
            trailerHolder.vpTrailers.setAdapter(new TrailerAdapter(mChildManager,
                    mMovie.getTrailers()));
            if(mPageListener != null){
                trailerHolder.vpTrailers.addOnPageChangeListener(mPageListener);
            }
        } else if (holder instanceof SMovie) {
            final SMovie movieHolder = (SMovie) holder;
            movieHolder.tvTitle.setText(mMovie.getTitle());
            movieHolder.tvRelease.setText(String.valueOf(mMovie.getYear()));
            String duration = "";
            if(mMovie.getRuntime() != 0){
                duration = String.format(mContext.getString(R.string.format_dur),
                        mMovie.getRuntime());
            } else {
                duration = mContext.getString(R.string.unknown_duration);
            }
            movieHolder.tvRuntime.setText(duration);

            movieHolder.ivPoster.setImageBitmap(mMovie.getBitmap());

            movieHolder.tvOverview.setText(mMovie.getOverview());
            movieHolder.tvRating.setText(String.format(mContext.getString(R.string.format_rat),
                    mMovie.getAverage()));

            if(mMovie.isFavorite()){
                movieHolder.ivFav.setImageResource(R.drawable.ic_favorite_red);
            }

            movieHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(view, mMovie.getId());
                }
            });

        } else if (holder instanceof STitle) {
            ((STitle)holder).tvTitle.setText(mContext.getString(R.string.section_title_reviews));
        } else if (holder instanceof Review) {
            Review reviewHolder = (Review) holder;
            Comment comment = mMovie.getComments().get(position - SECTIONS_BEFORE_REVIEWS);
            reviewHolder.tvAuthor.setText(comment.getAuthor());
            reviewHolder.tvContent.setText(comment.getContent());
        }
    }

    @Override
    public int getItemCount() {
        int sections = 0;
        if(mMovie != null){
            sections++;

            if(mMovie.hasTrailers()){
                sections++;
            }

            if(mMovie.hasComments()){
                sections = sections + HEADER + mMovie.getComments().size();
            }
        }
        return sections;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case SECTION_TRAILER_INDEX:
                return (mMovie.hasTrailers())?SECTION_TRAILER_INDEX:SECTION_MOVIE_INDEX;
            case SECTION_MOVIE_INDEX:
                return SECTION_MOVIE_INDEX;
            case SECTION_TITLE_INDEX:
                return SECTION_TITLE_INDEX;
            default:
                return SECTION_REVIEW_INDEX;
        }
    }

    public void swap(Movie movie) {
        if(movie != null){
            mMovie = movie;
        }
        notifyDataSetChanged();
    }

    public class Section extends RecyclerView.ViewHolder {

        public Section(View itemView) {
            super(itemView);
        }

    }

    public class STrailer extends Section {
        ViewPager vpTrailers;

        public STrailer(View itemView) {
            super(itemView);
            vpTrailers = (ViewPager) itemView.findViewById(R.id.vp_trailers);
            TabLayout tabLayout = (TabLayout) itemView.findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(vpTrailers, true);
        }
    }

    public class SMovie extends Section {
        TextView tvTitle;
        TextView tvRelease;
        TextView tvRuntime;
        ImageView ivPoster;
        TextView tvOverview;
        TextView tvRating;
        ImageView ivFav;

        public SMovie(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvRelease = (TextView) itemView.findViewById(R.id.tv_release_date);
            tvRuntime = (TextView) itemView.findViewById(R.id.tv_runtime);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
            tvOverview = (TextView) itemView.findViewById(R.id.tv_overview);
            tvRating = (TextView) itemView.findViewById(R.id.tv_rating);
            ivFav = (ImageView) itemView.findViewById(R.id.iv_fav);
        }
    }

    public class Review extends Section {
        TextView tvContent;
        TextView tvAuthor;

        public Review(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_author);
        }
    }

    private class STitle extends Section {
        TextView tvTitle;

        public STitle(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
