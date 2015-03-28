package thoughts.oved.gilad.thoughts;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by gilad on 3/28/15.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "W7kohGyu0ocgrwfMwW7IZpEy04yP5gkAEZDbOCKW", "4PNljdv6eDXN6D60iEgshacW37v4emRAxhB7c4Mt");
    }

}
