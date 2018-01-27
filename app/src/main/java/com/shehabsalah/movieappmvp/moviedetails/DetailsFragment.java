/*
 * Copyright (C) 2018 Shehab Salah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shehabsalah.movieappmvp.moviedetails;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.shehabsalah.movieappmvp.R;
import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.data.MovieReviews;
import com.shehabsalah.movieappmvp.data.MovieTrailers;
import com.shehabsalah.movieappmvp.moviedetails.adapters.ReviewsAdapter;
import com.shehabsalah.movieappmvp.moviedetails.adapters.TrailersAdapter;
import com.shehabsalah.movieappmvp.util.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ShehabSalah on 1/12/18.
 *
 */

public class DetailsFragment extends Fragment implements DetailsContract.view {
    @BindView(R.id.back_drop_poster)
    ImageView backDropPoster;
    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.movie_title)
    TextView movieTitle;
    @BindView(R.id.movie_date)
    TextView movieDate;
    @BindView(R.id.movie_rate)
    TextView movieRate;
    @BindView(R.id.favorite)
    ImageView favorite;
    @BindView(R.id.movie_overview)
    TextView movieOverview;
    @BindView(R.id.trailer_name)
    TextView trailerName;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView trailerRecyclerView;
    @BindView(R.id.reviews_name)
    TextView reviewName;
    @BindView(R.id.review_recycler_view)
    RecyclerView reviewRecyclerView;
    @BindView(R.id.action_divider_review)
    View dividerView;

    private DetailsContract.presenter mPresenter;
    private Movie movie;


    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.details_layout, container, false);
        ButterKnife.bind(this, mainView);
        mPresenter = new DetailsPresenter(this, getActivity());
        Bundle extras = getArguments();
        if (extras != null && extras.containsKey(Constants.MOVIE_EXTRA)) {
            movie = extras.getParcelable(Constants.MOVIE_EXTRA);
            String image = null;
            if (extras.containsKey(Constants.KEY_CONNECTION_IMAGE))
                image = extras.getString(Constants.KEY_CONNECTION_IMAGE);

            initViews(image);
        }

        return mainView;
    }

    private void initViews(String image) {
        if (image != null) {
            Picasso.with(getActivity())
                    .load(image)
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
                    .into(moviePoster, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (getActivity() != null)
                                getActivity().supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            if (getActivity() != null)
                                getActivity().supportStartPostponedEnterTransition();
                        }
                    });
        } else {
            Picasso.with(getActivity())
                    .load(Constants.IMAGE_URL + movie.getPosterPath())
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
                    .into(moviePoster);
        }

        movieTitle.setText(movie.getTitle());
        Picasso.with(getActivity())
                .load(Constants.BACKDROP_URL + movie.getBackdropPath())
                .placeholder(R.drawable.placeholder_background)
                .error(R.drawable.placeholder_background)
                .into(backDropPoster);

        movieDate.setText(movie.getReleaseDate());
        movieRate.setText(getString(R.string.vote, String.valueOf(movie.getVoteAverage())));
        movieOverview.setText(movie.getOverview());

        favoriteResponse(movie);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                reviewRecyclerView.getContext(),
                reviewLayoutManager.getOrientation()
        );
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewRecyclerView.addItemDecoration(mDividerItemDecoration);
        reviewRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(trailerLayoutManager);


        mPresenter.loadMovieInformation(movie.getMovieId());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            backDropPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);

    }

    @OnClick(R.id.favorite)
    public void onFavoriteClicked() {
        mPresenter.onFavoriteClick(movie);
    }

    @Override
    public void hideTrailers() {
        trailerName.setVisibility(View.GONE);
        trailerRecyclerView.setVisibility(View.GONE);
        dividerView.setVisibility(View.GONE);
    }

    @Override
    public void hideReviews() {
        reviewName.setVisibility(View.GONE);
        reviewRecyclerView.setVisibility(View.GONE);
        dividerView.setVisibility(View.GONE);
    }

    @Override
    public void showTrailers(ArrayList<MovieTrailers> movieTrailers) {
        TrailersAdapter trailersAdapter = new TrailersAdapter(movieTrailers, getActivity(), mPresenter);
        trailerRecyclerView.setAdapter(trailersAdapter);
    }

    @Override
    public void showReviews(ArrayList<MovieReviews> reviews) {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
        reviewRecyclerView.setAdapter(reviewsAdapter);
    }

    @Override
    public void favoriteResponse(Movie movie) {
        this.movie = movie;
        if (movie.getFavorite() == Constants.FAVORITE_ACTIVE)
            favorite.setImageResource(R.drawable.ic_like);
        else
            favorite.setImageResource(R.drawable.ic_unlike);
    }
}
