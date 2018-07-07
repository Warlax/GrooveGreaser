package com.calex.groov.app;

import android.app.Application;

public class GroovApplication extends Application {

  private GroovApplicationComponent applicationComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    applicationComponent =
        DaggerGroovApplicationComponent.builder()
            .databaseModule(new DatabaseModule(this))
            .sharedPreferencesModule(new SharedPreferencesModule(this))
            .build();
  }

  public GroovApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }
}
