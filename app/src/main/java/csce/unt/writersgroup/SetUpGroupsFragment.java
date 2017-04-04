package csce.unt.writersgroup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.BoardView;

import java.util.ArrayList;
import java.util.Random;

import csce.unt.writersgroup.adapters.WriterAdapter;
import csce.unt.writersgroup.model.Writer;

/**
 * Created by GW on 3/27/2017.
 */

public class SetUpGroupsFragment extends Fragment implements AdapterView.OnItemClickListener,
        BoardView.BoardListener
{
    private ListView userListView;
    private ArrayList<WriterAdapter> writerListAdapterList = new ArrayList<>();
    private ArrayList<Writer> writers;
    private BoardView writerBoardView;
    private int mColumns;
    private int sCreatedItems = 0;
    private Writer tmpWriterToChange;

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
        tmpWriterToChange = writerListAdapterList.get(column).getItemList().get(row).second;
    }

    @Override
    public void onItemChangedColumn(int oldColumn, int newColumn)
    {
        updateGroup(oldColumn, newColumn,tmpWriterToChange);
//        TextView numPages = (TextView) writerBoardView.getHeaderView(oldColumn).findViewById(R
//                .id.writer_num_pages);
//        numPages.setText("" + writerBoardView.getAdapter(oldColumn).getItemCount());
//        TextView writerName = (TextView) writerBoardView.getHeaderView(newColumn).findViewById(R
//                .id.writer_name);
//        writerName.setText("" + writerBoardView.getAdapter(newColumn).getItemCount());

    }

    private void updateGroup(int oldColumn, int newColumn, Writer tmpWriterToChange)
    {
        //TODO Update database values here
    }


    @Override
    public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow)
    {
        if (fromColumn != toColumn || fromRow != toRow)
        {
            Toast.makeText(getActivity(), "End - column: " + toColumn + " row: " + toRow, Toast
                    .LENGTH_SHORT).show();
            tmpWriterToChange=null;
        }
    }

    private ArrayList<Pair<Long, Writer>> getWriters()
    {

        ArrayList<Pair<Long, Writer>> writerArray = new ArrayList<>();
        int addItems = 15;
        for (int i = 0; i < addItems; i++)
        {
            Writer w = new Writer();
            w.setName("Writer" + i);
            w.setPages(new Random(System.currentTimeMillis() * i).nextInt(500));
            long id = w.hashCode();
            writerArray.add(new Pair<>(id, w));
        }
        return writerArray;
    }

    private void initFields(View currentView)
    {
        writerListAdapterList.add(new WriterAdapter(getWriters(), R.layout.writer_column, R.id
                .item_layout, true));
        writerListAdapterList.add(new WriterAdapter(getWriters(), R.layout.writer_column, R.id
                .item_layout, true));
        final View header = View.inflate(getActivity(), R.layout.writer_column_header, null);
        final View header2 = View.inflate(getActivity(), R.layout.writer_column_header, null);
        writerBoardView = (BoardView) currentView.findViewById(R.id.init_groups_writer_board_view);
        writerBoardView.setSnapToColumnsWhenScrolling(true);
        writerBoardView.setSnapToColumnWhenDragging(true);
        writerBoardView.setSnapDragItemToTouch(true);
        writerBoardView.setBoardListener(this);
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

        writerBoardView.addColumnList(writerListAdapterList.get(0), header, false);
        writerBoardView.addColumnList(writerListAdapterList.get(1), header2, false);
    }

    public interface Callbacks
    {
    }
}
