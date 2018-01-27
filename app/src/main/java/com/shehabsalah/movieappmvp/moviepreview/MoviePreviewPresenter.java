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
package com.shehabsalah.movieappmvp.moviepreview;

import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.data.source.local.MovieAppDatabase;
import com.shehabsalah.movieappmvp.util.ApplicationClass;
import com.shehabsalah.movieappmvp.util.Constants;

/**
 * Created by ShehabSalah on 1/10/18.
 * Listens to user actions from the UI ({@link MoviePreviewActivity}), retrieves the data and updates the
 * UI as required.
 */

public class MoviePreviewPresenter implements MoviePreviewContract.presenter {

    private MoviePreviewContract.view view;
    private MovieAppDatabase movieAppDatabase;

    MoviePreviewPresenter(MoviePreviewContract.view view) {
        this.view = view;
        movieAppDatabase = MovieAppDatabase.getInstance(ApplicationClass.getAppContext());
    }

    @Override
    public void onFavoritePressed(Movie movie) {
        if (movie.getFavorite() == Constants.FAVORITE_ACTIVE)
            movie.setFavorite(Constants.FAVORITE_NOT_ACTIVE);
        else
            movie.setFavorite(Constants.FAVORITE_ACTIVE);

        movieAppDatabase.movieDAO().updateMovie(movie);

        view.onFavoriteResponse(movie);

    }
}
