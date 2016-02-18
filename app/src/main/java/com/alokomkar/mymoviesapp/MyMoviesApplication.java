package com.alokomkar.mymoviesapp;

import android.app.Application;

import com.alokomkar.mymoviesapp.models.MovieModel;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCore;

/**
 * Created by cognitive on 2/16/16.
 */
public class MyMoviesApplication extends Application {
    public static MyMoviesApplication application;


    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        // http://www.rushorm.com/
        // Rush is initialized asynchronously to recieve a callback after it initialized
        // set an InitializeListener on the config object

        AndroidInitializeConfig config = new AndroidInitializeConfig(getApplicationContext());
        List<Class<? extends Rush>> classList = new ArrayList<>();
        classList.add(MovieModel.MovieResult.class);
        config.setClasses( classList );
        RushCore.initialize(config);

    }

    public static MyMoviesApplication getApplication() {
        return application;
    }

}
