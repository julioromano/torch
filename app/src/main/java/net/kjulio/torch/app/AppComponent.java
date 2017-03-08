package net.kjulio.torch.app;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class})
public interface AppComponent {

    void inject(App app);

}
