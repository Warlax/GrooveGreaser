package com.calex.groov.app;

import com.calex.groov.activity.ChallengeActivity;
import com.calex.groov.activity.ChallengesActivity;
import com.calex.groov.activity.CreateChallengeActivity;
import com.calex.groov.activity.HomeActivity;
import com.calex.groov.workers.CreateChallengeWorker;
import com.calex.groov.workers.RecordSetWorker;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    ClockModule.class,
    ContextModule.class,
    DatabaseModule.class,
    HandlerModule.class,
    SharedPreferencesModule.class,
})
@GroovApplicationScope
public interface GroovApplicationComponent {
  void inject(HomeActivity activity);
  void inject(ChallengesActivity activity);
  void inject(CreateChallengeActivity activity);
  void inject(ChallengeActivity activity);

  void inject(CreateChallengeWorker worker);
  void inject(RecordSetWorker worker);
}
