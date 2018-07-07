package com.calex.groov.app;

import android.os.Handler;
import android.os.Looper;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class HandlerModule {
  @Provides
  @Reusable
  public Handler provideHandler() {
    return new Handler(Looper.getMainLooper());
  }
}
