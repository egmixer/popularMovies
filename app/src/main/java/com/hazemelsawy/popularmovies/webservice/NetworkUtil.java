package com.hazemelsawy.popularmovies.webservice;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    private static NetworkUtil mInstance;
    private Services mServices;
    private Retrofit mRetrofit;

    private NetworkUtil() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mServices = mRetrofit.create(Services.class);
    }

    public static NetworkUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkUtil();
        }
        return mInstance;
    }

    public Services getServices() {
        return mServices;
    }

    public interface Services {
        @GET("{category}")
        Call<MovieModel> getMovies(@Path("category") String category, @Query("api_key") String apiKey);

        @GET("{id}/reviews")
        Call<ReviewModel> getReviews(@Path("id") String id, @Query("api_key") String apiKey);

        @GET("{id}/trailers")
        Call<TrailerModel> getTrailers(@Path("id") String id, @Query("api_key") String apiKey);
    }

}