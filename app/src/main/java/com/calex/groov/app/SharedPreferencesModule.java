package com.calex.groov.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Preconditions;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

  private static final String NAME = "prefs";

  private final Context context;

  public SharedPreferencesModule(Context context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Provides
  public SharedPreferences provideSharedPreferences() {
    return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
  }
}
