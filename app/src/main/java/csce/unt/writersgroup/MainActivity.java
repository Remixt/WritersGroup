package csce.unt.writersgroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import csce.unt.writersgroup.model.Session;

public class MainActivity extends AppCompatActivity implements NavActivity
{
    Button anchor_no;
    Toolbar toolbar;
    Button anchor_yes;
    Button navigateToTimer;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getIntent().getExtras().containsKey("session"))
        {
            session = (Session) getIntent().getExtras().get("session");
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        navigateToTimer = (Button) findViewById(R.id.button_go_to_timer);

        anchor_no = (Button) findViewById(R.id.dont_become_anchor_button);
        anchor_yes = (Button) findViewById(R.id.become_anchor_button);
        initListeners();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new WritersGroupNavigationItemListener
                (findViewById(android.R.id.content), MainActivity.this));
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == R.id.action_logout)
        {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void logout()
    {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void initListeners()
    {
        navigateToTimer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle b = new Bundle();
                b.putSerializable("session", session);
                ActivityUtil.showScreen(MainActivity.this, WaitActivity.class, b);
//                Bundle bundle = new Bundle();
//                bundle.putLong(TimerActivity.ARG_TIMER_LENGTH, (1000) * 60 * 60);
//                ActivityUtil.showScreen(MainActivity.this, TimerActivity.class, bundle);
            }
        });

        anchor_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LinearLayout anchor_buttons = (LinearLayout) findViewById(R.id.anchor_buttons);
                anchor_buttons.setVisibility(View.GONE);
                TextView anchor_reminder = (TextView) findViewById(R.id.anchor_reminder);
                anchor_reminder.setVisibility(View.GONE);
            }
        });
        anchor_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LinearLayout anchor_buttons = (LinearLayout) findViewById(R.id.anchor_buttons);
                anchor_buttons.setVisibility(View.GONE);
                TextView anchor_reminder = (TextView) findViewById(R.id.anchor_reminder);
                anchor_reminder.setVisibility(View.GONE);
            }
        });
    }
}
