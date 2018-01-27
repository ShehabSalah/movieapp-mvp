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

package com.shehabsalah.movieappmvp.movieslist;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.data.source.local.MovieAppDatabase;
import com.shehabsalah.movieappmvp.data.source.remote.apis.MovieApiConfig;
import com.shehabsalah.movieappmvp.data.source.remote.response.MoviesResponse;
import com.shehabsalah.movieappmvp.data.source.remote.listeners.NoConnectionListener;
import com.shehabsalah.movieappmvp.data.source.remote.listeners.OnServiceListener;
import com.shehabsalah.movieappmvp.data.source.remote.request.RequestHandler;
import com.shehabsalah.movieappmvp.data.source.remote.response.GeneralResponse;
import com.shehabsalah.movieappmvp.moviedetails.DetailsActivity;
import com.shehabsalah.movieappmvp.moviepreview.MoviePreviewActivity;
import com.shehabsalah.movieappmvp.util.ApplicationClass;
import com.shehabsalah.movieappmvp.util.Constants;
import java.util.ArrayList;

import static android.support.v4.app.ActivityOptionsCompat.*;

/**
 * Created by ShehabSalah on 1/8/18.
 * Listens to user actions from the UI ({@link MoviesListFragment}), retrieves the data and updates the
 * UI as required.
 */

public class MoviesPresenter implements MoviesContract.Presenter, OnServiceListener, NoConnectionListener {
    private MoviesContract.View views;
    private MoviesSortType moviesSortType;
    private MovieAppDatabase movieAppDatabase;
    private Activity activity;

    MoviesPresenter(MoviesContract.View views) {
        this.views = views;
        movieAppDatabase = MovieAppDatabase.getInstance(ApplicationClass.getAppContext());
    }

    @Override
    public void reload(boolean setAdapter) {
        ArrayList<Movie> movies;
        switch (moviesSortType) {
            case FAVORITES:
                movies = new ArrayList<>(movieAppDatabase.movieDAO().selectFavorites());
                if (movies.size() > 0)
                    views.showMovies(movies, setAdapter);
                else
                    views.showNoFavorites();
                break;
            case TOP_RATED:
                movies = new ArrayList<>(movieAppDatabase.movieDAO().selectTopRatedMovies());
                if (movies.size() > 0)
                    views.showMovies(movies, setAdapter);
                else
                    loadMovies();
                break;
            case MOST_POPULAR:
                movies = new ArrayList<>(movieAppDatabase.movieDAO().selectPopularMovies());
                if (movies.size() > 0)
                    views.showMovies(movies, setAdapter);
                else
                    views.showNoMovies();
                break;
        }
    }

    @Override
    public void goToDetailsActivity(Movie movie, View imageView, View textView) {
        if (movie != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair<View, String> imagePair = Pair.create(imageView, Constants.KEY_CONNECTION_IMAGE);
                ActivityOptionsCompat options = makeSceneTransitionAnimation(activity,
                         imagePair);
                activity.startActivity(DetailsActivity.getDetailsIntent(activity, movie), options.toBundle());
            } else {
                activity.startActivity(DetailsActivity.getDetailsIntent(activity, movie));
            }

        }
    }

    @Override
    public void loadMovies() {
        MovieApiConfig movieApiConfig = RequestHandler.getClient(Constants.BASE_URL, null).create(MovieApiConfig.class);
        switch (moviesSortType) {
            case MOST_POPULAR:
                RequestHandler.execute(movieApiConfig.executePopular(Constants.API_KEY), this, this, ApplicationClass.getAppContext());
                break;
            case TOP_RATED:
                RequestHandler.execute(movieApiConfig.executeTopRated(Constants.API_KEY), this, this, ApplicationClass.getAppContext());
                break;
            case FAVORITES:
                reload(true);
                break;
        }
    }

    @Override
    public void setMoviesType(MoviesSortType moviesType, boolean reload) {
        this.moviesSortType = moviesType;
        if (reload) reload(true);
    }

    @Override
    public MoviesSortType getMoviesType() {
        return moviesSortType;
    }

    @Override
    public void noInternetConnection() {
        views.showNoInternetConnection();
        reload(true);
    }

    @Override
    public void onResponse(String TAG, Object response) {
        if (response instanceof MoviesResponse) {
            MoviesResponse moviesResponse = (MoviesResponse) response;
            ArrayList<Movie> movies = moviesResponse.getResults();
            if (movies.size() > 0) {
                saveListIntoDatabase(movies);
                reload(true);
            } else {
                if (moviesSortType == MoviesSortType.TOP_RATED)
                    views.showNoMovies();
                else
                    reload(true);
            }
        } else {
            onErrorResponse("", new GeneralResponse("-1", "Error Response!"));
        }
    }

    @Override
    public void onErrorResponse(String TAG, GeneralResponse response) {
        views.showServerError(response.getMessage());
    }

    @Override
    public void openMoviePreview(Movie movie, View imageView, View textView, View cardView) {
        if (movie != null) {
            views.makeBackgroundBlur();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Pair<View, String> titlePair = Pair.create(textView, Constants.KEY_CONNECTION_TITLE);
                Pair<View, String> imagePair = Pair.create(imageView, Constants.KEY_CONNECTION_IMAGE);
                Pair<View, String> containerPair = Pair.create(cardView, Constants.KEY_CONNECTION_CONTAINER);
                ActivityOptionsCompat options = makeSceneTransitionAnimation(activity,
                                titlePair, imagePair, containerPair);
                activity.startActivity(MoviePreviewActivity.getDetailsIntent(activity, movie), options.toBundle());
            } else {
                activity.startActivity(MoviePreviewActivity.getDetailsIntent(activity, movie));
            }
        }
    }

    private void saveListIntoDatabase(ArrayList<Movie> movies) {
        deleteAll();
        for (Movie movie : movies) {
            switch (moviesSortType) {
                case MOST_POPULAR:
                    movie.setType(Constants.PAGE_POPULAR);
                    break;
                case TOP_RATED:
                    movie.setType(Constants.PAGE_TOP_RATED);
                    break;
            }
            movieAppDatabase.movieDAO().insertMovie(movie);
        }
    }

    @Override
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void deleteAll() {
        if (moviesSortType == MoviesSortType.MOST_POPULAR)
            movieAppDatabase.movieDAO().deleteAll();
    }
}