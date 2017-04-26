package csce.unt.writersgroup;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by GW on 4/22/2017.
 */

public class WritersGroupNavigationItemListener implements NavigationView
        .OnNavigationItemSelectedListener
{
    public Activity activity;

    private View parentView;

    public WritersGroupNavigationItemListener(View parentView, Activity activity)
    {
        this.parentView = parentView;
        this.activity = activity;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout)
        {
            ((NavActivity) activity).logout();
        }
//        else if (id == R.id.nav_camera)
//        {
//             Handle the camera action
//        }
//        else if (id == R.id.nav_gallery)
//        {
//
//        }
//        else if (id == R.id.nav_slideshow)
//        {
//
//        }
//        else if (id == R.id.nav_manage)
//        {
//
//        }
//        else if (id == R.id.nav_share)
//        {
//
//        }
//        else if (id == R.id.nav_send)
//        {
//
//        }

        DrawerLayout drawer = (DrawerLayout) parentView.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public View getParentView()
    {
        return parentView;
    }

    public void setParentView(View parentView)
    {
        this.parentView = parentView;
    }
}
