package net.kjulio.torch.timber;

import net.kjulio.torch.BuildConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class TimberInitializer {

    @Inject
    TimberInitializer() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
