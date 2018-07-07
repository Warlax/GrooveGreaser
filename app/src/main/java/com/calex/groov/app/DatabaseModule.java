package com.calex.groov.app;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.calex.groov.data.GroovDatabase;
import com.google.common.base.Preconditions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
  private final Context context;

  public DatabaseModule(Context context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Provides
  @Singleton
  public GroovDatabase provideDatabase() {
    return Room.databaseBuilder(context, GroovDatabase.class, "groov-db")
        .fallbackToDestructiveMigration()
        .build();
  }
}
