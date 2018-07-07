package com.calex.groov.util;

public interface Clock {
  long currentTimeMs();
  long todayStartTimestamp();
  long todayEndTimestamp();
}
