package csce.unt.writersgroup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import csce.unt.writersgroup.model.Session;

import static csce.unt.writersgroup.model.Session.SESSION_STARTED;

public class WaitActivity extends AppCompatActivity
{


    /**
     * Firebase root URL
     */
    private static final String FIREBASE_URL = "https://writersgroup-69ec1.firebaseio.com/";
    private FirebaseAuth mAuth = null;
    /**
     * Firebase Databse reference,
     * Auth reference, and Firebase reference
     */
    private DatabaseReference mDatabase = null;
    private Firebase mFirebase = null;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = (Session) getIntent().getExtras().getSerializable("session");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(FIREBASE_URL);

        final DatabaseReference sessions = mDatabase.child("sessions");
        if (session != null)
        {
            sessions.child(session.getSessionId()).addChildEventListener(new ChildEventListener()
            {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
//                    for (DataSnapshot child : dataSnapshot.getChildren())
//                    {
//                        Session tmpSession = child.getValue(Session.class);
//                        if (tmpSession.getSessionId().equals(session.getSessionId()))
//                        {
//                            session = tmpSession;
//                            break;
//                        }
//                    }

                    if (dataSnapshot.getKey().equals("started") && dataSnapshot.getValue().equals
                            (SESSION_STARTED))
                    {
                        session.setSessionId(SESSION_STARTED);
                        Bundle bundle = new Bundle();
                        bundle.putLong(TimerActivity.ARG_TIMER_LENGTH, (1000) * 60 * 60);
                        bundle.putSerializable("session", session);
                        ActivityUtil.showScreen(WaitActivity.this, TimerActivity.class, bundle);
                        finish();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot)
                {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s)
                {

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

}
