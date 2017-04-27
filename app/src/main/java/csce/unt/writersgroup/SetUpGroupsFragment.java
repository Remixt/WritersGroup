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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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
    private HashMap<Integer, String> columnToGroupMap = new HashMap<>();
    private Button createGroup;
    private Button createUser;
    private CheckBox isAnchorCheckbox;
    private EditText newGroupText;
    private EditText newUserText;
    private Button startSession;
    private User tmpUserToChange;
    private HashMap<String, UserAdapter> userAdapterGroupMap = new HashMap<>();
    private BoardView userBoardView;
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
                        User userObj = activity.getWriterMap().get
                                (user.trim());
                        if (userObj != null)
                        {
                            //make sure user is not set as anchor
                            userObj.setAnchor("false");
                            userPairList.add(new Pair<>((long) user.hashCode(), userObj));
                            int itemCount = userAdapterGroupMap.get(groupName) == null ? 1 :
                                    userAdapterGroupMap.get(groupName).getItemCount();
                            if (userAdapterGroupMap.containsKey(groupName) && userAdapterGroupMap
                                    .get(groupName).getPositionForItemId(userObj.getUid()
                                            .hashCode()) < 0) //if this group
                            // exists on our board view
                            {
                                //add the user to the user adapter if its not already there
                                userAdapterGroupMap.get(groupName).addItem(itemCount, new Pair<>(
                                        (long) userObj.getUid().hashCode(), userObj));
                                //Check if the user was in the logged in user list and remove it
                                // from the logged in list
                                int loggedInUserIndex = getIndexOfUser
                                        (LOGGED_IN_USERS_COLUMN_HEADER,
                                                userObj);
                                if (loggedInUserIndex > -1)
                                {
                                    userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER)
                                            .removeItem
                                                    (loggedInUserIndex);
                                }
                            }

                        }
                    }
                }
                if (anchors != null)
                {
                    String[] anchorAry = anchors.split(",");
                    for (String user : anchorAry)
                    {
                        User anchorObj = activity.getWriterMap().get
                                (user.trim());
                        if (anchorObj != null)
                        {
                            //make sure user is set as anchor
                            anchorObj.setAnchor("true");
                            userPairList.add(new Pair<>((long) user.hashCode(), anchorObj));
                            int itemCount = userAdapterGroupMap.get(groupName) == null ? 1 :
                                    userAdapterGroupMap.get(groupName).getItemCount();
                            if (userAdapterGroupMap.containsKey(groupName) && userAdapterGroupMap
                                    .get(groupName).getPositionForItemId(anchorObj.getUid()
                                            .hashCode()) < 0)
                            {
                                //add the user to the user adapter
                                userAdapterGroupMap.get(groupName).addItem(itemCount, new Pair<>(
                                        (long) anchorObj.getUid().hashCode(), anchorObj));
                                //Check if the user was in the logged in user list and remove it
                                // from the logged in list
                                int loggedInUserIndex = getIndexOfUser
                                        (LOGGED_IN_USERS_COLUMN_HEADER,
                                                anchorObj);
                                if (loggedInUserIndex > -1)
                                {
                                    userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER)
                                            .removeItem
                                                    (loggedInUserIndex);
                                }
                            }

                        }
                    }
                }
                columnToGroupMap.put(userAdapterGroupMap.size(), groupName);
//                userAdapterGroupMap.put(userAdapterGroupMap.size() + "", new UserAdapter
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
    private ValueEventListener currentSessionValueEventListener = new ValueEventListener()
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.getValue() != null)
            {
                String groups = dataSnapshot.child("groups").getValue().toString(); //get groups
                // in session
                for (String group : groups.split(","))
                {
                    //empty group name, skip this
                    if (group.trim().length() == 0) continue;
                    //add a listener to the group for changes to the group
                    activity.mDatabase.child("groups").addListenerForSingleValueEvent
                            (groupValueEventListener);
                    columnToGroupMap.put(userAdapterGroupMap.size(), group);
                    //store group name in map with numeric column number as the key
                    UserAdapter value = new UserAdapter
                            (new ArrayList<Pair<Long, User>>(),
                                    R.layout
                                            .writer_column, R.id.item_layout, true);
                    //put this into the UserAdapter Map
                    userAdapterGroupMap.put(group, value);
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
                    activity.getWriterMap().put(u.getUid(), u);
                }
            }
            if (userAdapterGroupMap.size() == 0)
            {
                //create default Logged In User group column
                UserAdapter loggedInUserAdapter = new UserAdapter
                        (activity.getLoggedInWriters(),
                                R.layout
                                        .writer_column, R.id.item_layout, true);
                userAdapterGroupMap.put(LOGGED_IN_USERS_COLUMN_HEADER, loggedInUserAdapter);
                updateBoardView();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };

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
            tmpUserToChange = userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER).getItemList
                    ().get(row).second;
        }
        else if (userAdapterGroupMap.get(columnToGroupMap.get(column)) != null)
        {
            Toast.makeText(getActivity(), "Start - column: " + column + " row: " + row, Toast
                    .LENGTH_SHORT).show();
            String groupKey = columnToGroupMap.get(column);
            List<Pair<Long, User>> groupUserList = userAdapterGroupMap.get(groupKey).getItemList
                    ();
            tmpUserToChange = groupUserList.get(row).second;
        }
    }

    @Override
    public void onItemChangedColumn(int oldColumn, int newColumn)
    {
        updateGroup(oldColumn, newColumn, tmpUserToChange);
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
        if (user == null)
        {
            Log.e(getClass().getSimpleName(), "Could not retrieve user to add to group");
        }
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
        Set<String> strings = userAdapterGroupMap.keySet();
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
        UserAdapter userAdapter = userAdapterGroupMap.get
                (groupKey);
        if (userAdapter == null)
        {
            return -1;
        }
        List<Pair<Long, User>> itemList = userAdapter.getItemList();
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


    private Pair<String, String> getUserAnchorStringsFromGroup(String groupKey)
    {
        String userString = "", anchorString = "";
        if (userAdapterGroupMap.get(groupKey) == null)
        {
            return new Pair<>("", "");
        }
        for (Pair<Long, User> userPair : userAdapterGroupMap.get(groupKey).getItemList())
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

    private void initFields(View currentView)
    {
//        userAdapterGroupMap.add(new UserAdapter(getWriters(), R.layout.writer_column, R.id
//                .item_layout, true));
//        userAdapterGroupMap.add(*(getWriters(), R.layout.writer_column, R.id
//                .item_layout, true));
        activity = (SetGroupsActivity) getActivity();
//        final View header = View.inflate(getActivity(), R.layout.writer_column_header, null);
//        final View header2 = View.inflate(getActivity(), R.layout.writer_column_header, null);
        userBoardView = (BoardView) currentView.findViewById(R.id.init_groups_writer_board_view);
        userBoardView.setSnapToColumnsWhenScrolling(true);
        userBoardView.setSnapToColumnWhenDragging(true);
        userBoardView.setSnapDragItemToTouch(true);
        userBoardView.setBoardListener(this);
        startSession = (Button) currentView.findViewById(R.id.button_start_session);
        createGroup = (Button) currentView.findViewById(R.id.button_create_new_group);
        newGroupText = (EditText) currentView.findViewById(R.id.edit_text_new_group);
        createUser = (Button) currentView.findViewById(R.id.button_create_new_user);
        newUserText = (EditText) currentView.findViewById(R.id.edit_text_new_user);
        isAnchorCheckbox = (CheckBox) currentView.findViewById(R.id.checkbox_is_anchor);
//        ((TextView) header.findViewById(R.id.writer_name)).setText("Group " + (mColumns + 1));
//        ((TextView) header.findViewById(R.id.writer_num_pages)).setText("Number of Pages");
//        ((TextView) header2.findViewById(R.id.writer_name)).setText("Group " + (mColumns + 2));
//
//        ((TextView) header2.findViewById(R.id.writer_num_pages)).setText("Number of Pages");
    }

    private void initListeners()
    {
        final DatabaseReference sessionReference = activity.mDatabase.child("sessions").child
                (activity
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
                activity.startSession();
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
                    groupName = "Group " + (userAdapterGroupMap.size());
                }
                columnToGroupMap.put(userAdapterGroupMap.size(), groupName);
                UserAdapter newUserAdapter = new UserAdapter(new ArrayList<Pair<Long,
                        User>>(), R.layout
                        .writer_column, R.id.item_layout, true);
                userAdapterGroupMap.put(groupName, newUserAdapter);
                createGroupInFirebase(groupName);

                updateBoardView();
            }
        });
        createUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userName = newUserText.getText().toString();
                if (userName.trim().length() == 0)
                {
                    Toast.makeText(activity, "Enter a username", Toast.LENGTH_LONG).show();
                    return;
                }

                User user = new User();
                user.setName(userName);
                user.setEmail(userName);
                user.setUid(UUID.randomUUID().toString());
                user.setAnchor(isAnchorCheckbox.isChecked() ? "true" : "false");
                userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER).addItem
                        (userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER).getItemCount(),
                                new Pair<>((long) user.hashCode(), user));
                Map<String, Object> userObj = new HashMap<>();
                userObj.put(user.getUid(), user);
                sessionReference.child("users").updateChildren(userObj);
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
        if (userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER) instanceof DragItemAdapter)
        {
            userBoardView.addColumnList(userAdapterGroupMap.get(LOGGED_IN_USERS_COLUMN_HEADER),
                    defaultHeader, false);
        }
        for (int column : columnToGroupMap.keySet())
        {
            String key = columnToGroupMap.get(column);
//        }
//        for (String key : userAdapterGroupMap.keySet())
//        {
            if (key.equals(LOGGED_IN_USERS_COLUMN_HEADER)) continue;
            final View header = View.inflate(getActivity(), R.layout.writer_column_header, null);
            ((TextView) header.findViewById(R.id.group_id)).setText(key);
            if (userAdapterGroupMap.get(key) instanceof DragItemAdapter)
            {
                userBoardView.addColumnList(userAdapterGroupMap.get(key), header, false);
            }
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
    }

}
