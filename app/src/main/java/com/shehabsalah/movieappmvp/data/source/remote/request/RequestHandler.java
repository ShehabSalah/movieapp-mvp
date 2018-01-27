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
package com.shehabsalah.movieappmvp.data.source.remote.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.shehabsalah.movieappmvp.data.source.remote.listeners.NoConnectionListener;
import com.shehabsalah.movieappmvp.data.source.remote.listeners.OnServiceListener;
import com.shehabsalah.movieappmvp.data.source.remote.response.GeneralResponse;
import java.util.HashMap;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ShehabSalah on 1/8/18.
 *
 */

public class RequestHandler {

    private static final String TAG = "RequestHandler";

    public static <T> void execute(Call<T> call, final OnServiceListener onServiceListener, NoConnectionListener noConnectionListener, Context context) {
        execute(TAG, call, onServiceListener, noConnectionListener, context);
    }

    public static <T> void execute(final String TAG, Call<T> call, final OnServiceListener onServiceListener, NoConnectionListener noConnectionListener, Context context) {
        //check if there is internet connection or not.
        if (checkInternetConnection(context)) {

            // if there is internet connection
            Log.d(TAG, call.request().toString());
            // Fire the interface implementation and get the server response
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                    //If the request success! return the response body to the Activity.
                    onServiceListener.onResponse(TAG, response.body());
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    //If the request Failed! return the error response to the Activity.
                    onServiceListener.onErrorResponse(TAG, new GeneralResponse("404", "Error"));
                }
            });

        } else {
            // if there is no internet connection call noInternetConnection in the Activity to notify the user.
            noConnectionListener.noInternetConnection();
        }
    }

    /**
     * This method take the application context and check on the internet connectivity.
     * if the device connected with the internet! the method will return true, else it will return false.
     * @param context  Activity context.
     * @return boolean true if internet connected, false if it not connected.
     * */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager =

                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * To work with Retrofit you need basically three classes:
     * 1- Model class which is used to map the JSON data to
     * 2- Interfaces which defines the possible HTTP operations
     * 3- Retrofit.Builder class - Instance which uses the interface and the Builder API which allows
     * defining the URL end point for the HTTP operation.
     * */
    public static Retrofit getClient(String baseUrl, HashMap<String, String> headers) {
        /**
         * An interceptor is used to modify each request before it is performed and alters the request header.
         * It can be used to adds the credentials, if you request your user details from the server
         * and if the request need more calls that require you to authenticate, you can use an interceptor for this.
         * In our case we use the loggingInterceptor which allow us to track the user input, url call
         * and the server response.
         *
         * To add an interceptor, you have to use the:
         * okhttp3.OkHttpClient.Builder.addInterceptor(Interceptor) method on the OkHttp Builder.
         * */

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(loggingInterceptor);
        /**
         * (HttpLoggingInterceptor) An OkHttp interceptor which logs HTTP request and response data.
         * The logs generated by this interceptor when using the HEADERS or BODY levels has the potential
         * to leak sensitive information such as "Authorization" or "Cookie" headers and the contents of
         * request and response bodies.
         * */
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                /**
                 * The created OkHttp client has to be added to your Retrofit client with the
                 * retrofit2.Retrofit.Builder.client(OkHttpClient) method.
                 * */
                .client(client.build())
                /**
                 * Interface which uses Gson for its deserialization.
                 * With this adapter being applied the Retrofit interfaces are able to return JSON files as
                 * class <LoginResponse>
                 * */
                .addConverterFactory(GsonConverterFactory.create()).build();

                // NOTE:: this retrofit missing the create() method!
    }
}
