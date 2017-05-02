package csce.unt.writersgroup.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import csce.unt.writersgroup.R;
import csce.unt.writersgroup.model.User;

/**
 * Created by GW on 3/27/2017.
 */

public class UserAdapter extends DragItemAdapter<Pair<Long, User>, UserAdapter.ViewHolder>
{
    private final int mLayoutId;
    private final int mGrabHandleId;
    private final boolean mDragOnLongPress;
    public UserAdapter(ArrayList<Pair<Long, User>> list, int layoutId, int grabHandleId,
                       boolean dragOnLongPress)
    {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setHasStableIds(true);
        setItemList(list);
        Log.d(getClass().getSimpleName(), "Number of users in list: " + getItemList().size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        Log.d(getClass().getSimpleName(), "Number of users in list: " + getItemList().size());
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position)
    {
//        Log.d("ItemID", position + ": " + mItemList.get(position).first);
        return mItemList.get(position).first;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        super.onBindViewHolder(holder, position);
        User user = mItemList.get(position).second;
        if (user != null)
        {
            holder.writerName.setText(user.getName());
            holder.writerPages.setText(String.valueOf(user.getPages()));
            if (user.isAnAnchor())
            {
                holder.cardView.setCardBackgroundColor(Color.CYAN);
                holder.writerName.setText(user.getName() + "(Anchor)");
            }
        }
        else
        {
            holder.writerName.setText("Guest");
            holder.writerPages.setText("0");
        }
        holder.itemView.setTag(mItemList.get(position));

    }

    class ViewHolder extends DragItemAdapter.ViewHolder
    {
        TextView writerName;
        TextView writerPages;
        CardView cardView;

        ViewHolder(final View itemView)
        {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            writerName = (TextView) itemView.findViewById(R.id.writer_name);
            writerPages = (TextView) itemView.findViewById(R.id.writer_num_pages);
            cardView = (CardView) itemView.findViewById(R.id.card);
        }

        @Override
        public void onItemClicked(View view)
        {
//            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view)
        {
//            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
