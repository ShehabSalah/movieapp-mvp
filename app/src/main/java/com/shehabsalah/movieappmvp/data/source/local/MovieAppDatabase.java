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
package com.shehabsalah.movieappmvp.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.shehabsalah.movieappmvp.data.Movie;
import com.shehabsalah.movieappmvp.data.MovieReviews;
import com.shehabsalah.movieappmvp.data.MovieTrailers;

/**
 * Created by ShehabSalah on 1/9/18.
 */
@Database(entities = {Movie.class, MovieReviews.class, MovieTrailers.class}, version = 2)
public abstract class MovieAppDatabase extends RoomDatabase {

    private static MovieAppDatabase INSTANCE;

    public abstract MovieDAO movieDAO();

    private static final Object sLock = new Object();

    public static MovieAppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MovieAppDatabase.class, "MoviesAppMvp.db")
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }


}
