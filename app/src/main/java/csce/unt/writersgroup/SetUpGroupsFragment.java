package csce.unt.writersgroup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.woxthebox.draglistview.BoardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import csce.unt.writersgroup.adapters.UserAdapter;
import csce.unt.writersgroup.model.Group;
import csce.unt.writersgroup.model.User;

/**
 * Created by GW on 3/27/2017.
 */

public class SetUpGroupsFragment extends Fragment implements AdapterView.OnItemClickListener,
        BoardView.BoardListener
{
    public static final String LOGGED_IN_USERS_COLUMN_HEADER = "Logged In Users";
    SetGroupsActivity activity;
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
            if (activity.session.getGroups() == null)
            {
                return;
            }

            for (DataSnapshot child : dataSnapshot.getChildren())
            {
                String groupName = child.getKey();
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
                        if (userObj != null)
                        {
                            userObj.setAnchor("false");
                            userPairList.add(new Pair<>((long) user.hashCode(), userObj));
                            int itemCount = userListAdapterList.get(groupName) == null ? 1 :
                                    userListAdapterList.get(groupName).getItemCount();
                            if (userListAdapterList.containsKey(groupName))
                            {
                                userListAdapterList.get(groupName).addItem(itemCount, new Pair<>(
                                        (long) userObj.getUid().hashCode(), userObj));
                            }
                            int loggedInUserIndex = getIndexOfUser(LOGGED_IN_USERS_COLUMN_HEADER,
                                    userObj);
                            if (loggedInUserIndex > -1)
                            {
                                userListAdapterList.get(LOGGED_IN_USERS_COLUMN_HEADER).removeItem
                                        (loggedInUserIndex);
                            }
                        }
                    }
                }
                if (anchors != null)
                {
                    String[] anchorAry = anchors.split(",");
                    for (String user : anchorAry)
                    {
                        User anchorObj = getWriterMap().get
                                (user.trim());
                        if (anchorObj != null)
                        {
                            anchorObj.setAnchor("true");
                            userPairList.add(new Pair<>((long) user.hashCode(), anchorObj));
                            int itemCount = userListAdapterList.get(groupName) == null ? 1 :
                                    userListAdapterList.get(groupName).getItemCount();
                            if (userListAdapterList.containsKey(groupName))
                            {
                                userListAdapterList.get(groupName).addItem(itemCount, new Pair<>(
                                        (long) anchorObj.getUid().hashCode(), anchorObj));
                            }
                            int loggedInUserIndex = getIndexOfUser(LOGGED_IN_USERS_COLUMN_HEADER,
                                    anchorObj);
                            if (loggedInUserIndex > -1)
                            {
                                userListAdapterList.get(LOGGED_IN_USERS_COLUMN_HEADER).removeItem
                                        (loggedInUserIndex);
                            }
                        }
                    }
                }
                columnToGroupMap.put(userListAdapterList.size(), groupName);
//                userListAdapterList.put(userListAdapterList.size() + "", new UserAdapter
//                        (userPairList, R.layout
//                                .writer_column, R.id.item_layout, true));

            }
            updateBoardView();
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };
    private Button startSession;
    private ValueEventListener currentSessionValueEventListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.getValue() != null)
            {
                String groups = dataSnapshot.child("groups").getValue().toString();
                for (String group : groups.split(","))
                {
                    activity.mDatabase.child("groups").addListenerForSingleValueEvent
                            (groupValueEventListener);
                    userListAdapterList.put(group, new UserAdapter
                            (new ArrayList<Pair<Long, User>>(),
                                    R.layout
                                            .writer_column, R.id.item_layout, true));
                }

            }
            else
            {
                Log.e(getClass().getSimpleName(), "Session does not exist for " +
                        activity.session.getSessionId());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };
    private ArrayList<Pair<Long, User>> loggedInWriters;
    private ValueEventListener userValueEventListener = new ValueEventListener()
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
            if (userListAdapterList.size() == 0)
            {
                userListAdapterList.put(LOGGED_IN_USERS_COLUMN_HEADER, new UserAdapter
                        (getLoggedInWriters(),
                                R.layout
                                        .writer_column, R.id.item_layout, true));
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };
    private EditText newGroupText;
    private Button createGroup;

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
        if (column == 0)
        {
            tmpUserToChange = userListAdapterList.get(LOGGED_IN_USERS_COLUMN_HEADER).getItemList
                    ().get(row).second;

        }
        else if (userListAdapterList.get(columnToGroupMap.get(column)) != null)
        {
            Toast.makeText(getActivity(), "Start - column: " + column + " row: " + row, Toast
                    .LENGTH_SHORT).show();
            tmpUserToChange = userListAdapterList.get(columnToGroupMap.get(column)).getItemList()
                    .get(row).second;
        }
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

    private String addUserFromString(String userString, User user)
    {
        if (userString == null) return user.getUid();
        String[] ary = userString.split(",");
        if (ary.length == 0 || (ary.length == 1 && ary[0].length() == 0))
        {
            return user.getUid();
        }
        String newUserString = "";
        for (String userStr : ary)
        {
            if (userStr.equals(user.getUid())) return userString;
            newUserString += userStr;
            newUserString += ",";
        }
        newUserString += user.getUid();
        return trimTrailingComma(newUserString);
    }

    private void createGroupInFirebase(String groupName)
    {
        Map<String, Object> sessionGroupMap = new HashMap<>();
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put(groupName, new Group(groupName));
        activity.mDatabase.child("groups").updateChildren(groupMap);

        String groupString = "";
        Set<String> strings = userListAdapterList.keySet();
        //Converting to TreeSet for simple alphabetical sort
        for (String groupID : new TreeSet<>(strings))
        {
            if (groupID.equals(LOGGED_IN_USERS_COLUMN_HEADER)) continue;
            groupString += groupID;
            groupString += ",";
        }

        groupString = trimTrailingComma(groupString);
        sessionGroupMap.put("groups", groupString);
        activity.mDatabase.child("sessions").child(activity.session.getSessionId())
                .updateChildren(sessionGroupMap);

    }

    private int getIndexOfUser(String groupKey, User userObj)
    {
        List<Pair<Long, User>> itemList = userListAdapterList.get
                (groupKey).getItemList();
        int index = -1;
        for (int i = 0; i < itemList.size(); i++)
        {
            Pair<Long, User> row = itemList.get(i);
            if (row.second.getUid().equals(userObj.getUid()))
            {
                index = i;
                break;
            }
        }
        return index;
    }

    private ArrayList<Pair<Long, User>> getLoggedInWriters()
    {
        if (loggedInWriters == null)
        {
            loggedInWriters = new ArrayList<>();
            for (String key : getWriterMap().keySet())
            {
                User user = getWriterMap().get(key);
                loggedInWriters.add(new Pair<>((long) user.hashCode(), user));
            }
        }

        return loggedInWriters;
    }

    private Pair<String, String> getUserAnchorStringsFromGroup(String groupKey)
    {
        String userString = "", anchorString = "";
        if (userListAdapterList.get(groupKey) == null)
        {
            return new Pair<>("", "");
        }
        for (Pair<Long, User> userPair : userListAdapterList.get(groupKey).getItemList())
        {
            if (userPair.second.isAnAnchor())
            {
                anchorString += userPair.second.getUid();
                anchorString += ",";
            }
            else
            {
                userString += userPair.second.getUid();
                userString += ",";
            }
        }
        userString = trimTrailingComma(userString);
        anchorString = trimTrailingComma(anchorString);
        return new Pair<>(userString, anchorString);
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

    private ArrayList<Pair<Long, User>> getWritersInGroup(String group)
    {
        return null;
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
        createGroup = (Button) currentView.findViewById(R.id.button_create_new_group);
        newGroupText = (EditText) currentView.findViewById(R.id.edit_text_new_group);
        ((TextView) header.findViewById(R.id.writer_name)).setText("Group " + (mColumns + 1));
        ((TextView) header.findViewById(R.id.writer_num_pages)).setText("Number of Pages");
        ((TextView) header2.findViewById(R.id.writer_name)).setText("Group " + (mColumns + 2));

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
        DatabaseReference sessionReference = activity.mDatabase.child("sessions").child(activity
                .session
                .getSessionId());
        sessionReference
                .addValueEventListener(currentSessionValueEventListener);
        sessionReference.child("users").addListenerForSingleValueEvent(userValueEventListener);

        startSession.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Set current session started=true
                //go to timer screen
            }
        });
        createGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String groupName = newGroupText.getText().toString();
                if (groupName.trim().length() <= 0)
                {
                    groupName = "Group " + (userListAdapterList.size());
                }
                columnToGroupMap.put(userListAdapterList.size(), groupName);
                userListAdapterList.put(groupName, new UserAdapter(new ArrayList<Pair<Long,
                        User>>(), R.layout
                        .writer_column, R.id.item_layout, true));
                createGroupInFirebase(groupName);

                updateBoardView();
            }
        });
    }

    private String removeUserFromString(String userString, User user)
    {
        if (userString == null) return "";
        String[] ary = userString.split(",");
        if (ary.length == 0 || (ary.length == 1 && ary[0].length() == 0))
        {
            return "";
        }
        String newUserString = "";
        for (String userStr : ary)
        {
            if (user != null && userStr.equals(user.getUid())) continue;
            newUserString += userStr;
            newUserString += ",";
        }
        newUserString = trimTrailingComma(newUserString);
        return newUserString;
    }

    @NonNull
    private String trimTrailingComma(String str)
    {
        if (str == null)
        {
            return "";
        }
        if (str.startsWith(","))
        {
            str = str.substring(1, str.length());
        }
        if (str.endsWith(","))
        {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void updateBoardView()
    {
        userBoardView.clearBoard();
        View defaultHeader = View.inflate(getActivity(), R.layout.writer_column_header, null);
        ((TextView) defaultHeader.findViewById(R.id.group_id)).setText
                (LOGGED_IN_USERS_COLUMN_HEADER);
        userBoardView.addColumnList(userListAdapterList.get(LOGGED_IN_USERS_COLUMN_HEADER),
                defaultHeader, false);
        for (String key : userListAdapterList.keySet())
        {
            if (key.equals(LOGGED_IN_USERS_COLUMN_HEADER)) continue;
            final View header = View.inflate(getActivity(), R.layout.writer_column_header, null);
            ((TextView) header.findViewById(R.id.group_id)).setText(key);
            userBoardView.addColumnList(userListAdapterList.get(key), header, false);
        }
    }

    private void updateGroup(int oldColumn, int newColumn, User tmpWriterToChange)
    {
        final String oldGroup = columnToGroupMap.get(oldColumn) == null ?
                LOGGED_IN_USERS_COLUMN_HEADER : columnToGroupMap.get(oldColumn);
        final String newGroup = columnToGroupMap.get(newColumn) == null ?
                LOGGED_IN_USERS_COLUMN_HEADER : columnToGroupMap.get(newColumn);

        Pair<String, String> oldGroupValues = getUserAnchorStringsFromGroup(oldGroup);
        Pair<String, String> newGroupValues = getUserAnchorStringsFromGroup(newGroup);
        if (tmpWriterToChange != null && tmpWriterToChange.isAnAnchor())
        {
            oldGroupValues = new Pair<>(oldGroupValues.first, removeUserFromString(oldGroupValues
                    .second, tmpWriterToChange));
            newGroupValues = new Pair<>(newGroupValues.first, addUserFromString(newGroupValues
                    .second, tmpWriterToChange));

        }
        else
        {
            oldGroupValues = new Pair<>(removeUserFromString(oldGroupValues.first,
                    tmpWriterToChange), oldGroupValues.second);
            newGroupValues = new Pair<>(addUserFromString(newGroupValues.first,
                    tmpWriterToChange), newGroupValues.second);
        }
        if (!oldGroup.equals(LOGGED_IN_USERS_COLUMN_HEADER))
        {
            Map<String, Object> groupValues = new HashMap<>();
            groupValues.put("users", oldGroupValues.first);
            groupValues.put("anchors", oldGroupValues.second);
            activity.mDatabase.child("groups").child(oldGroup).updateChildren(groupValues);
        }

        if (!newGroup.equals(LOGGED_IN_USERS_COLUMN_HEADER))
        {
            Map<String, Object> groupValues = new HashMap<>();
            groupValues.put("users", newGroupValues.first);
            groupValues.put("anchors", newGroupValues.second);
            activity.mDatabase.child("groups").child(newGroup).updateChildren(groupValues);
        }

/*
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
*/
    }

    public interface Callbacks
    {
    }
}
