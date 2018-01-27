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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.data.MovieReviews;
import com.shehabsalah.movieappmvp.data.MovieTrailers;
import com.shehabsalah.movieappmvp.data.source.local.MovieAppDatabase;
import com.shehabsalah.movieappmvp.data.source.remote.apis.ReviewsApiConfig;
import com.shehabsalah.movieappmvp.data.source.remote.apis.TrailersApiConfig;
import com.shehabsalah.movieappmvp.data.source.remote.listeners.NoConnectionListener;
import com.shehabsalah.movieappmvp.data.source.remote.listeners.OnServiceListener;
import com.shehabsalah.movieappmvp.data.source.remote.request.RequestHandler;
import com.shehabsalah.movieappmvp.data.source.remote.response.GeneralResponse;
import com.shehabsalah.movieappmvp.data.source.remote.response.ReviewsResponse;
import com.shehabsalah.movieappmvp.data.source.remote.response.TrailersResponse;
import com.shehabsalah.movieappmvp.util.ApplicationClass;
import com.shehabsalah.movieappmvp.util.Constants;

import java.util.ArrayList;

/**
 * Created by ShehabSalah on 1/12/18.
 * Listens to user actions from the UI ({@link DetailsFragment}), retrieves the data and updates the
 * UI as required.
 */

public class DetailsPresenter implements DetailsContract.presenter, OnServiceListener, NoConnectionListener {

    private DetailsContract.view view;
    private MovieAppDatabase movieAppDatabase;
    private Activity activity;
    private int movieId;

    DetailsPresenter(DetailsContract.view view, Activity activity) {
        this.view = view;
        this.activity = activity;
        movieAppDatabase = MovieAppDatabase.getInstance(ApplicationClass.getAppContext());
    }

    @Override
    public void loadMovieInformation(int movieId) {
        this.movieId = movieId;
        ReviewsApiConfig reviewsApiConfig =
                RequestHandler.getClient(Constants.BASE_URL + String.valueOf(movieId) + Constants.FILE_SPERATOR, null)
                        .create(ReviewsApiConfig.class);

        TrailersApiConfig trailersApiConfig =
                RequestHandler.getClient(Constants.BASE_URL + String.valueOf(movieId) + Constants.FILE_SPERATOR, null)
                        .create(TrailersApiConfig.class);

        RequestHandler.execute(reviewsApiConfig.getMovieReviews(Constants.API_KEY), this, this, ApplicationClass.getAppContext());
        RequestHandler.execute(trailersApiConfig.getMovieTrailers(Constants.API_KEY), this, this, ApplicationClass.getAppContext());
    }

    @Override
    public void onFavoriteClick(Movie movie) {
        if (movie.getFavorite() == Constants.FAVORITE_ACTIVE)
            movie.setFavorite(Constants.FAVORITE_NOT_ACTIVE);
        else
            movie.setFavorite(Constants.FAVORITE_ACTIVE);

        movieAppDatabase.movieDAO().updateMovie(movie);

        view.favoriteResponse(movie);
    }

    @Override
    public void onTrailerClicked(String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            activity.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            activity.startActivity(webIntent);
        }
    }

    @Override
    public void noInternetConnection() {
        loadReviewsFromLocalDB();
        loadTrailersFromLocalDB();
    }

    @Override
    public void onResponse(String TAG, Object response) {
        if (response instanceof ReviewsResponse) {
            ReviewsResponse reviewsResponse = (ReviewsResponse) response;
            ArrayList<MovieReviews> movieReviews = reviewsResponse.getResults();
            if (movieReviews.size() > 0) {
                saveReviewsIntoDatabase(movieReviews);
                view.showReviews(movieReviews);
            } else {
                view.hideReviews();
            }
        } else if (response instanceof TrailersResponse) {
            TrailersResponse trailersResponse = (TrailersResponse) response;
            ArrayList<MovieTrailers> movieTrailers = trailersResponse.getResults();
            if (movieTrailers.size() > 0) {
                saveTrailersIntoDatabase(movieTrailers);
                view.showTrailers(movieTrailers);
            } else {
                view.hideTrailers();
            }
        }
    }

    @Override
    public void onErrorResponse(String TAG, GeneralResponse response) {
        loadReviewsFromLocalDB();
        loadTrailersFromLocalDB();
    }

    private void loadReviewsFromLocalDB() {
        ArrayList<MovieReviews> movieReviews = new ArrayList<>(movieAppDatabase.movieDAO().selectReviews(movieId));
        if (movieReviews.size() > 0)
            view.showReviews(movieReviews);
        else
            view.hideReviews();
    }

    private void loadTrailersFromLocalDB() {
        ArrayList<MovieTrailers> movieTrailers = new ArrayList<>(movieAppDatabase.movieDAO().selectTrailers(movieId));
        if (movieTrailers.size() > 0)
            view.showTrailers(movieTrailers);
        else
            view.hideTrailers();
    }

    private void saveReviewsIntoDatabase(ArrayList<MovieReviews> movieReviews) {
        movieAppDatabase.movieDAO().deleteAllReviews(movieId);

        for (MovieReviews mReviews : movieReviews) {
            mReviews.setMovieId(movieId);
            movieAppDatabase.movieDAO().insertMovieReview(mReviews);
        }

    }

    private void saveTrailersIntoDatabase(ArrayList<MovieTrailers> movieTrailers) {
        movieAppDatabase.movieDAO().deleteAllTrailers(movieId);

        for (MovieTrailers mTrailer : movieTrailers) {
            mTrailer.setMovieId(movieId);
            movieAppDatabase.movieDAO().insertMovieTrailer(mTrailer);
        }

    }

}
