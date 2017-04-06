package csce.unt.writersgroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import csce.unt.writersgroup.model.Session;

public class WaitActivity extends AppCompatActivity {


    /**
     * Firebase root URL
     */
    private static final String FIREBASE_URL = "https://writersgroup-69ec1.firebaseio.com/";
    /**
     * Firebase Databse reference,
     * Auth reference, and Firebase reference
     */
    private DatabaseReference mDatabase = null;
    private FirebaseAuth mAuth = null;
    private Firebase mFirebase = null;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = (Session)getIntent().getExtras().getSerializable("session");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(FIREBASE_URL);

        mFirebase.child("sessions").child(session.getSessionId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                session = dataSnapshot.getValue(Session.class);
                if(session.getStarted().equals("true")){
                    session = dataSnapshot.getValue(Session.class);
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(WaitActivity.this, MainActivity.class);
                    bundle.putSerializable("session", session);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
