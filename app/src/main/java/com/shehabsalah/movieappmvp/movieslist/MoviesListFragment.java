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

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shehabsalah.movieappmvp.R;
import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.util.ApplicationClass;
import com.shehabsalah.movieappmvp.util.MessageHandler;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * Created by ShehabSalah on 1/8/18.
 *
 */

public class MoviesListFragment extends Fragment implements MoviesContract.View {
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_to_refresh) SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.message_container) RelativeLayout messageContainer;
    @BindView(R.id.error_message) TextView errorMessage;
    @BindView(R.id.layout_container)
    BlurView blurView;

    private MoviesContract.Presenter mPresenter;
    private MoviesListAdapter adapter;


    public MoviesListFragment() {
        mPresenter = new MoviesPresenter(this);
    }

    public static MoviesListFragment newInstance() {
        return new MoviesListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.movies_list, container, false);
        ButterKnife.bind(this, mainView);
        mPresenter.setActivity(getActivity());
        adapter = new MoviesListAdapter(getActivity(), new ArrayList<Movie>(0), mPresenter);
        initViews();
        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            gridLayoutManager = new GridLayoutManager(ApplicationClass.getAppContext(), 3);
        else
            gridLayoutManager = new GridLayoutManager(ApplicationClass.getAppContext(), 2);

        recyclerView.setLayoutManager(gridLayoutManager);

        swipeToRefresh.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark),
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeToRefresh.setRefreshing(true);
                mPresenter.loadMovies();
            }
        });
        if (mainView!=null)
            mPresenter.loadMovies();
        return mainView;
    }

    private void initViews(){
        float radius = 10;

        View decorView = getActivity().getWindow().getDecorView();
        //Activity's root View. Can also be root View of your layout (preferably)
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //set background, if your root layout doesn't have one
        Drawable windowBackground = decorView.getBackground();
        blurView.setVisibility(View.GONE);
        blurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(getActivity()))
                .blurRadius(radius);
    }

    private void showMessageError() {
        messageContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

    }

    @Override
    public void showMovies(ArrayList<Movie> movies, boolean setAdapter) {
        if (swipeToRefresh!=null)
            swipeToRefresh.setRefreshing(false);

        recyclerView.setVisibility(View.VISIBLE);
        messageContainer.setVisibility(View.GONE);

        adapter.replaceData(movies);
        if (setAdapter)
            recyclerView.setAdapter(adapter);
    }

    @Override
    public void showNoMovies() {
        if (swipeToRefresh!=null)
            swipeToRefresh.setRefreshing(false);
        errorMessage.setText(R.string.no_movies);

        showMessageError();
    }

    @Override
    public void showNoFavorites() {
        if (swipeToRefresh!=null)
            swipeToRefresh.setRefreshing(false);
        errorMessage.setText(R.string.no_favorite);

        showMessageError();
    }

    @Override
    public void showServerError(String error) {
        if (swipeToRefresh!=null)
            swipeToRefresh.setRefreshing(false);
        MessageHandler.alertDialog(getActivity(), error, null);
    }

    @Override
    public void showNoInternetConnection() {
        if (swipeToRefresh!=null)
            swipeToRefresh.setRefreshing(false);
        Toast.makeText(getActivity(), R.string.no_internet_error_message, Toast.LENGTH_LONG).show();
    }

    public void setType(MoviesSortType moviesSortType, boolean reload){
        mPresenter.setMoviesType(moviesSortType, reload);
    }

    public MoviesSortType getType(){
        return mPresenter.getMoviesType();
    }

    @Override
    public void makeBackgroundBlur() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator!=null)
            vibrator.vibrate(50);
        blurView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        blurView.setVisibility(View.GONE);
        mPresenter.reload(false);
    }
}
