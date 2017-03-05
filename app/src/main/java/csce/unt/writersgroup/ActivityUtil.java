package csce.unt.writersgroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by GW on 03/05/2017.
 */

public class ActivityUtil
{
    public static final int ACTIVITY_REQUEST_CODE = 1;
    public static final String ARG_LOGGED_IN_USER = "argLoggedInUser";

    public static Object getBundleArg(Bundle extras, String argumentKey)
    {
        if (extras == null)
        {
            return null;
        }
        if (!extras.containsKey(argumentKey))
            return null;
        return extras.get(argumentKey);


    }

    public static void showScreen(Activity activity, Class child, Bundle bundle)
    {
        Intent intent = new Intent(activity, child);
        intent.putExtras(bundle);

        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);

    }

    public static void showScreen(Activity activity, Class child)
    {
        showScreen(activity, child, new Bundle());
    }
}
