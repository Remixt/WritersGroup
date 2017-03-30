package csce.unt.writersgroup;

/**
 * Created by Satyanarayana on 3/30/2017.
 */
import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;



public class WritersGroup extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        // Make the data persist for offline usage
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
