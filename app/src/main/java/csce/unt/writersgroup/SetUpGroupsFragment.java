package csce.unt.writersgroup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.woxthebox.draglistview.BoardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import csce.unt.writersgroup.adapters.UserAdapter;
import csce.unt.writersgroup.model.User;

/**
 * Created by GW on 3/27/2017.
 */

public class SetUpGroupsFragment extends Fragment implements AdapterView.OnItemClickListener,
        BoardView.BoardListener
{
    SetGroupsActivity activity;
    private ListView userListView;
    private HashMap<String, User> writers;

    private HashMap<String, UserAdapter> userListAdapterList = new HashMap<>();
    private BoardView userBoardView;
    private int mColumns;
    private int sCreatedItems = 0;
    private HashMap<Integer, String> columnToGroupMap = new HashMap<>();
    private User tmpUserToChange;
    private ValueEventListener groupValueEventListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            Log.v("DataSnapShotKey", dataSnapshot.getKey() + ": " + dataSnapshot
                    .getValue().toString());
            for (DataSnapshot child : dataSnapshot.getChildren())
            {
                Log.v("ChildKey", child.getKey());
                String users = ((HashMap) child.getValue()).get("users").toString();
                String anchors = ((HashMap) child.getValue()).get("anchors").toString();
                ArrayList<Pair<Long, User>> userPairList = new ArrayList<>();

                if (users != null)
                {
                    String[] userAry = users.split(",");
                    for (String user : userAry)
                    {
                        User userObj = getWriterMap().get
                                (user.trim());
                        userObj.setAnchor("false");
                        userPairList.add(new Pair<>((long) user.hashCode(), userObj));
                    }
                }
                if (anchors != null)
                {
                    String[] anchorAry = anchors.split(",");
                    for (String user : anchorAry)
                    {
                        User anchorObj = getWriterMap().get
                                (user.trim());
                        anchorObj.setAnchor("true");
                        userPairList.add(new Pair<>((long) user.hashCode(), anchorObj));
                    }
                }
                columnToGroupMap.put(userListAdapterList.size(), child.getKey());
                userListAdapterList.put(userListAdapterList.size() + "", new UserAdapter
                        (userPairList, R.layout
                                .writer_column, R.id.item_layout, true));

            }
            updateBoardView();
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };
    private Button startSession;

    public static SetUpGroupsFragment newInstance()
    {
        return new SetUpGroupsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState)
    {
        View currentView = inflater.inflate(R.layout.fragment_init_groups, container, false);
        initFields(currentView);
        initListeners();
        return currentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public void onItemDragStarted(int column, int row)
    {
        Toast.makeText(getActivity(), "Start - column: " + column + " row: " + row, Toast
                .LENGTH_SHORT).show();
        tmpUserToChange = userListAdapterList.get("" + column).getItemList().get(row).second;
    }

    @Override
    public void onItemChangedColumn(int oldColumn, int newColumn)
    {
        updateGroup(oldColumn, newColumn, tmpUserToChange);
//        TextView numPages = (TextView) writerBoardView.getHeaderView(oldColumn).findViewById(R
//                .id.writer_num_pages);
//        numPages.setText("" + writerBoardView.getAdapter(oldColumn).getItemCount());
//        TextView writerName = (TextView) writerBoardView.getHeaderView(newColumn).findViewById(R
//                .id.writer_name);
//        writerName.setText("" + writerBoardView.getAdapter(newColumn).getItemCount());

    }

    @Override
    public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow)
    {
        if (fromColumn != toColumn || fromRow != toRow)
        {
            Toast.makeText(getActivity(), "End - column: " + toColumn + " row: " + toRow, Toast
                    .LENGTH_SHORT).show();
            tmpUserToChange = null;
        }
    }

    public HashMap<String, User> getWriterMap()
    {
        if (writers == null)
        {
            writers = new HashMap<>();
        }
        return writers;
    }

    private ArrayList<Pair<Long, User>> getWriters()
    {
        ArrayList<Pair<Long, User>> userArray = new ArrayList<>();
        int addItems = 15;
        for (int i = 0; i < addItems; i++)
        {
            User u = new User();
            u.setName("Writer" + i);
            u.setPages(new Random(System.currentTimeMillis() * i).nextInt(500));
            long id = u.hashCode();
            userArray.add(new Pair<>(id, u));
        }
        return userArray;
    }

    private void initFields(View currentView)
    {
//        userListAdapterList.add(new UserAdapter(getWriters(), R.layout.writer_column, R.id
//                .item_layout, true));
//        userListAdapterList.add(new UserAdapter(getWriters(), R.layout.writer_column, R.id
//                .item_layout, true));
        activity = (SetGroupsActivity) getActivity();
        final View header = View.inflate(getActivity(), R.layout.writer_column_header, null);
        final View header2 = View.inflate(getActivity(), R.layout.writer_column_header, null);
        userBoardView = (BoardView) currentView.findViewById(R.id.init_groups_writer_board_view);
        userBoardView.setSnapToColumnsWhenScrolling(true);
        userBoardView.setSnapToColumnWhenDragging(true);
        userBoardView.setSnapDragItemToTouch(true);
        userBoardView.setBoardListener(this);
        startSession = (Button) currentView.findViewById(R.id.button_start_session);
        ((TextView) header.findViewById(R.id.writer_name)).setText("Group #" + (mColumns + 1));
        ((TextView) header.findViewById(R.id.writer_num_pages)).setText("Number of Pages");
        ((TextView) header2.findViewById(R.id.writer_name)).setText("Group #" + (mColumns + 2));

        ((TextView) header2.findViewById(R.id.writer_num_pages)).setText("Number of Pages");

//        int addItems = 15;
//        header.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Writer w = new Writer();
//                w.setName("Writer" + sCreatedItems);
//                w.setPages(sCreatedItems * new Random(System.currentTimeMillis()).nextInt(500));
//                Pair item = new Pair<>(w.hashCode(), w);
//                writerBoardView.addItem(mColumns, 0, item, true);
//                //mBoardView.moveItem(4, 0, 0, true);
//                //mBoardView.removeItem(column, 0);
//                //mBoardView.moveItem(0, 0, 1, 3, false);
//                //mBoardView.replaceItem(0, 0, item1, true);
//                ((TextView) header.findViewById(R.id.writer_num_pages)).setText("" + getWriters
// ().size());
//            }
//        });

//        userBoardView.addColumnList(userListAdapterList.get(0), header, false);
//        userBoardView.addColumnList(userListAdapterList.get(1), header2, false);
    }

    private void initListeners()
    {

        activity.mDatabase.child("sessions").child(activity.session.getSessionId())
                .addListenerForSingleValueEvent(new ValueEventListener()

                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String groups = dataSnapshot.child("groups").getValue().toString();
                        for (String group : groups.split(","))
                        {
                            activity.mDatabase.child("groups").addListenerForSingleValueEvent
                                    (groupValueEventListener);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
        activity.mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User u = snapshot.getValue(User.class);
                    if (u != null)
                    {
                        getWriterMap().put(u.getUid(), u);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        startSession.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Set current session started=true
                //go to timer screen
            }
        });
    }

    private void updateBoardView()
    {
        for (String key : userListAdapterList.keySet())
        {
            final View header = View.inflate(getActivity(), R.layout.writer_column_header, null);
            ((TextView) header.findViewById(R.id.group_id)).setText(key);
            userBoardView.addColumnList(userListAdapterList.get(key), header, false);
        }
    }

    private void updateGroup(int oldColumn, int newColumn, User tmpWriterToChange)
    {
        final String oldGroup = columnToGroupMap.get(oldColumn);
        final String newGroup = columnToGroupMap.get(newColumn);

        Firebase groups = activity.mFirebase.child("groups");
        Firebase oldGroupReference = activity.mFirebase.child("groups").child(oldGroup);
        Firebase newGroupReference = activity.mFirebase.child("groups").child(newGroup);
        HashMap<String, Object> oldGroupMap = new HashMap<>();
        HashMap<String, Object> newGroupMap = new HashMap<>();
        for (Pair userPair : userListAdapterList.get(oldColumn + "").getItemList())
        {
            if (!((User) userPair.second).getUid().equals(tmpWriterToChange))
            {
                oldGroupMap.put(((User) userPair.second).getUid(), (User) userPair.second);
            }
        }
        for (Pair userPair : userListAdapterList.get(oldColumn + "").getItemList())
        {
            if (!((User) userPair.second).getUid().equals(tmpWriterToChange)) //this check cleans
            // up some of the bad data we have right now for testing, can be removed later as we
            // won't have users duplicated in a group
            {
                newGroupMap.put(((User) userPair.second).getUid(), (User) userPair.second);
            }
        }
        newGroupMap.put(tmpWriterToChange.getUid(), tmpWriterToChange);  //can be taken out along
        // with the above if statement later
        oldGroupReference.setValue(oldGroupMap);
        newGroupReference.setValue(newGroupMap);
        oldGroupReference.runTransaction(new Transaction.Handler()
        {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData)
            {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, com.firebase.client
                    .DataSnapshot dataSnapshot)
            {

            }
        });
        groups.runTransaction(new Transaction.Handler()
        {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData)
            {
                mutableData.child(oldGroup);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, com.firebase.client
                    .DataSnapshot dataSnapshot)
            {
                dataSnapshot.getValue();
            }
        });

    }

    public interface Callbacks
    {
    }
}
