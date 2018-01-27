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
import android.view.View;

import com.shehabsalah.movieappmvp.data.Movie;
import java.util.ArrayList;

/**
 * Created by ShehabSalah on 1/8/18.
 * This specifies the contract between the view and the presenter.
 */

public interface MoviesContract {
    interface View{
        void showMovies(ArrayList<Movie> movies, boolean setAdapter);
        void showNoMovies();
        void showNoFavorites();
        void showServerError(String error);
        void showNoInternetConnection();
        void makeBackgroundBlur();
    }

    interface Presenter{
        void goToDetailsActivity(Movie movie, android.view.View imageView, android.view.View textView);
        void loadMovies();
        void setMoviesType(MoviesSortType moviesType, boolean reload);
        void setActivity(Activity activity);
        void openMoviePreview(Movie movie, android.view.View imageView, android.view.View textView, android.view.View cardView);
        void reload(boolean setAdapter);
        MoviesSortType getMoviesType();

    }
}
