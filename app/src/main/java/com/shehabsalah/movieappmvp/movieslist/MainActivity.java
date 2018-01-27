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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.shehabsalah.movieappmvp.R;
import com.shehabsalah.movieappmvp.util.ActivityUtils;

public class MainActivity extends AppCompatActivity {
    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private MoviesListFragment moviesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create the movie Fragment
        moviesFragment =
                (MoviesListFragment) getSupportFragmentManager().findFragmentById(R.id.list_container);
        if (moviesFragment == null){
            moviesFragment = MoviesListFragment.newInstance();
            moviesFragment.setType(MoviesSortType.MOST_POPULAR, false);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), moviesFragment, R.id.list_container);
        }

        // Load previously saved state, if available.
        if (savedInstanceState != null) {

            MoviesSortType currentSortType =
                    (MoviesSortType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            moviesFragment.setType(currentSortType, false);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_popular){
            moviesFragment.setType(MoviesSortType.MOST_POPULAR, true);
        }else if (id == R.id.action_top_rated){
            moviesFragment.setType(MoviesSortType.TOP_RATED, true);
        }else if (id == R.id.action_favorite){
            moviesFragment.setType(MoviesSortType.FAVORITES, true);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, moviesFragment.getType());
        super.onSaveInstanceState(outState);
    }

}
