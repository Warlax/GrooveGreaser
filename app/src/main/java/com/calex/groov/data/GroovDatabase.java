package com.calex.groov.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.calex.groov.data.entities.Challenge;
import com.calex.groov.data.entities.RepSet;
import com.calex.groov.data.dao.ChallengeDao;
import com.calex.groov.data.dao.RepSetDao;

@Database(entities = {Challenge.class, RepSet.class,}, version = 1)
public abstract class GroovDatabase extends RoomDatabase {
  public abstract ChallengeDao challengeDao();
  public abstract RepSetDao repSetDao();
}
