package csce.unt.writersgroup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csce.unt.writersgroup.model.Session;
import csce.unt.writersgroup.model.User;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>
{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Firebase root URL
     */
    private static final String FIREBASE_URL = "https://writersgroup-69ec1.firebaseio.com/";
    private Session session;
    /**
     * Firebase Databse reference,
     * Auth reference, and Firebase reference
     */
    private DatabaseReference mDatabase = null;
    private FirebaseAuth mAuth = null;
    private Firebase mFirebase = null;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle bundle = getIntent().getExtras();

//        FirebaseApp.initializeApp(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setText("writersgroupunt@gmail.com");
        populateAutoComplete();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(FIREBASE_URL);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("writersgroupunt2017");
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    attemptLogin(false);
                    return true;
                }
                return false;
            }
        });
        if (bundle != null && bundle.containsKey("argUser"))
        {
            mEmailView.setText(((User) bundle.get("argUser")).getEmail());
            mPasswordView.setText("");
            mPasswordView.requestFocus();
        }
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(false);
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        Button officerSignIn = (Button) findViewById(R.id.sign_in_as_officer);
        officerSignIn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                attemptLogin(true);
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_READ_CONTACTS)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection)
    {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void addUserToSession(DatabaseReference sessionReference, Session session) throws
            Exception
    {
        if (currentUser == null)
        {
            throw new Exception("Error adding user to session.  currentUser is null");
        }
        if (session.getUsers() == null)
        {
            session.setUsers(new HashMap<String, Object>());
        }
        if (!session.getUsers().containsKey(currentUser.getUid()))
        {
            session.getUsers().put(currentUser.getUid(), currentUser);
        }
        sessionReference.child(session.getSessionId()).child("users").updateChildren(session.getUsers());

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     *
     * @param isOfficer
     */
    private void attemptLogin(final boolean isOfficer)
    {
        if (mAuthTask != null)
        {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password))
        {
            // Check for a valid password, if the user entered one.
            if (!isPasswordValid(password))
            {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            showProgress(false);
                            if (task.isSuccessful())
                            {
                                session = new Session();
                                session.setSessionId(Session.generateNewSessionID());
                                final String email = task.getResult().getUser().getEmail();
                                final String username;
                                if (email.contains("@"))
                                {
                                    username = email.split("@")[0];
                                }
                                else
                                {
                                    username = email;
                                }
                                mDatabase.child("users").orderByChild("email").equalTo(email)
                                        .addChildEventListener(new ChildEventListener()
                                {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                                    {
                                        currentUser = dataSnapshot.getValue(User.class);
                                        finishLogIn(isOfficer);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                                    {
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
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Sign In Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void finishLogIn(final boolean isOfficer)
    {
        final DatabaseReference sessionsReference = mDatabase.child
                ("sessions");
        sessionsReference.addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot
                                                     dataSnapshot)
                    {
                        Session dbSession = null;
                        for (DataSnapshot child : dataSnapshot
                                .getChildren())
                        {
                            Session tmpSession = child.getValue(Session.class);
                            if (tmpSession == null || tmpSession.getSessionId() == null) continue;
                            if (tmpSession.getSessionId().equals
                                    (session.getSessionId()))
                            {
                                dbSession = tmpSession;
                                break;
                            }
                        }
                        if (dbSession != null)
                        {
                            session = dbSession;
                        }

                        try
                        {
                            addUserToSession(sessionsReference, session);
                        } catch (Exception e)
                        {
                            Log.e(getClass().getSimpleName(), "Error adding user to session", e);
                            Toast.makeText(LoginActivity.this, "Could not log you into today's " +
                                    "session.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        HashMap<String, Object> map = new
                                HashMap<>();
                        map.put(session.getSessionId(),
                                session);
                        sessionsReference.updateChildren(map);
                        Bundle bundle = new Bundle();
                        Intent intent = null;
                        if (session != null && session.getStarted()
                                .equals(Session.SESSION_STARTED))
                        {
                            intent = new Intent(LoginActivity.this,
                                    (isOfficer ? SetGroupsActivity.class
                                            : MainActivity.class));
                        }
                        else
                        {
                            intent = new Intent(LoginActivity.this,
                                    (isOfficer ? SetGroupsActivity.class
                                            : WaitActivity.class));
                        }
                        bundle.putSerializable("session", session);
                        bundle.putSerializable("currentUser", currentUser);

                        intent.putExtras(bundle);

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError
                                                    databaseError)
                    {

                    }
                }
        );
    }

    private boolean isEmailValid(String email)
    {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password)
    {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean mayRequestContacts()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS))
        {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener()
                    {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v)
                        {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        }
        else
        {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private void populateAutoComplete()
    {
        if (!mayRequestContacts())
        {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private interface ProfileQuery
    {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password)
        {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            // TODO: attempt authentication against a network service.

            try
            {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS)
            {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail))
                {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            showProgress(false);

            if (success)
            {
                Bundle bundle = new Bundle();
                bundle.putString(ActivityUtil.ARG_LOGGED_IN_USER, mEmail);
                ActivityUtil.showScreen(LoginActivity.this, MainActivity.class, bundle);
            }
            else
            {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            showProgress(false);
        }

        private String usernameFromEmail(String email)
        {
            if (email.contains("@"))
            {
                return email.split("@")[0];
            }
            else
            {
                return email;
            }
        }
    }
}

