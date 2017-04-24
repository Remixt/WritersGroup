package csce.unt.writersgroup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import csce.unt.writersgroup.model.Session;

/**
 * Created by GW on 3/27/2017.
 */

public class SetGroupsActivity extends AppCompatActivity implements SetUpGroupsFragment.Callbacks
{
    private static final String INIT_FRAGMENT = "fragment_init_groups";
    private static final String FIREBASE_URL = "https://writersgroup-69ec1.firebaseio.com/";
    public Session session;
    public DatabaseReference mDatabase = null;
    public Firebase mFirebase = null;
    private FirebaseAuth mAuth = null;
    private Toolbar toolbar;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs)
    {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_groups);
        initFields();

        switchFragment(INIT_FRAGMENT);
        Bundle b = getIntent().getExtras();
        session = (Session) b.get("session");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(FIREBASE_URL);

    }

    private void initFields()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new WritersGroupNavigationItemListener
                (findViewById(android.R.id.content)));
    }

    public void startSession()
    {
        session.setStarted(Session.SESSION_STARTED);
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("started", session.getStarted());
        mDatabase.child("sessions").child(session.getSessionId()).child("started").setValue
                (session.getStarted());
    }

    private void switchFragment(String initFragment)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        switch (initFragment)
        {
            case INIT_FRAGMENT:
                fragment = SetUpGroupsFragment.newInstance();
                break;
            default:
                fragment = SetUpGroupsFragment.newInstance();
        }
        ft.replace(R.id.set_groups_drawer_layout, fragment);
        ft.commit();
    }


}
