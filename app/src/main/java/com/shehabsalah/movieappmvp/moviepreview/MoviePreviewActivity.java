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

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.shehabsalah.movieappmvp.R;
import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.util.Constants;
import com.shehabsalah.movieappmvp.util.PicassoHandler;
import com.squareup.picasso.Callback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoviePreviewActivity extends AppCompatActivity implements MoviePreviewContract.view {
    @BindView(R.id.movie_poster)
    ImageView poster;
    @BindView(R.id.movie_title)
    TextView title;
    @BindView(R.id.movie_description)
    TextView description;
    @BindView(R.id.favorite_text)
    TextView favoriteText;
    MoviePreviewContract.presenter mPresenter;
    Movie movie;

    public static Intent getDetailsIntent(Context context, Movie movie) {
        return new Intent(context, MoviePreviewActivity.class).putExtra(Constants.MOVIE_EXTRA, movie);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_movie_preview);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.MOVIE_EXTRA)) {
            mPresenter = new MoviePreviewPresenter(this);
            initViews(intent);
        } else {
            supportFinishAfterTransition();
            onBackPressed();
        }
    }

    private void initViews(Intent intent) {
        Bundle extras = getIntent().getExtras();
        movie = intent.getParcelableExtra(Constants.MOVIE_EXTRA);
        description.setText(movie.getOverview());
        if (movie.getFavorite() == Constants.FAVORITE_ACTIVE)
            favoriteText.setText(getString(R.string.remove_favorite));
        else
            favoriteText.setText(getString(R.string.add_favorite));

        if (intent.hasExtra(Constants.KEY_CONNECTION_IMAGE))
            PicassoHandler.getInstance(this).getPicasso()
                    .load(extras.getString(Constants.KEY_CONNECTION_IMAGE))
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportStartPostponedEnterTransition();
                        }
                    });
        else
            PicassoHandler.getInstance(this).getPicasso()
                    .load(Constants.IMAGE_URL + movie.getPosterPath())
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
                    .into(poster);

        if (intent.hasExtra(Constants.KEY_CONNECTION_TITLE))
            title.setText(extras.getString(Constants.KEY_CONNECTION_TITLE));
        else
            title.setText(movie.getTitle());

    }

    @OnClick(R.id.preview_container)
    public void close() {
        supportFinishAfterTransition();
        onBackPressed();
    }

    @OnClick(R.id.dialog_container)
    public void dialogClicked() {
        //Do nothing...
    }

    @OnClick(R.id.add_to_favorite)
    public void onFavoriteClicked() {
        mPresenter.onFavoritePressed(movie);
    }

    @Override
    public void onFavoriteResponse(Movie movie) {
        if (movie.getFavorite() == Constants.FAVORITE_ACTIVE)
            favoriteText.setText(getString(R.string.remove_favorite));
        else
            favoriteText.setText(getString(R.string.add_favorite));

        this.movie = movie;
    }
}
