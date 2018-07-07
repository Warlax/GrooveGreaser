package com.calex.groov.app;

import com.calex.groov.util.Clock;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class ClockModule {
  @Provides
  @Reusable
  public Clock provideClock() {
    return new Clock() {
      @Override
      public long currentTimeMs() {
        return System.currentTimeMillis();
      }

      @Override
      public long todayStartTimestamp() {
        return Date.from(
            LocalDateTime
                .ofInstant(
                    new Date().toInstant(), ZoneId.systemDefault())
                .with(LocalTime.MIN)
                .atZone(ZoneId
                    .systemDefault())
                    .toInstant())
            .getTime();
      }

      @Override
      public long todayEndTimestamp() {
        return Date.from(
            LocalDateTime
                .ofInstant(
                    new Date().toInstant(), ZoneId.systemDefault())
                .with(LocalTime.MAX)
                .atZone(ZoneId
                    .systemDefault())
                .toInstant())
            .getTime();
      }
    };
  }
}
