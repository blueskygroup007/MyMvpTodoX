package com.bluesky.rabbit_habit.habit_list;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluesky.rabbit_habit.R;
import com.bluesky.rabbit_habit.data.Habit;
import com.bluesky.rabbit_habit.data.source.HabitsDataSource;
import com.bluesky.rabbit_habit.data.source.HabitsRepository;
import com.bluesky.rabbit_habit.habit_list.dummy.DummyContent.DummyItem;
import com.bluesky.rabbit_habit.util.Injection;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HabitFragment extends Fragment implements HabitListContract.View{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = HabitFragment.class.getSimpleName().toString();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private HabitRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HabitFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HabitFragment newInstance(int columnCount) {
        HabitFragment fragment = new HabitFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;

            //todo 当行数大于1时,设置排列方式为网格
/*            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }*/

//            recyclerView.setAdapter(new MyHabitRecyclerViewAdapter(DummyContent.ITEMS, mListener));
            mAdapter = new HabitRecyclerViewAdapter(new ArrayList<>(0));
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    /**
     * 测试代码:当fragment的视图都创建完毕,传递真正的列表数据进去
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*        HabitDao dao = RHDatabase.getInstance(getContext()).habitDao();
        List<Habit> habits = dao.getHabits();*/
        HabitsRepository repository = Injection.provideTasksRepository(getContext());
        repository.getHabits(new HabitsDataSource.LoadHabitsCallback() {
            @Override
            public void onHabitsLoaded(List<Habit> habits) {
                mAdapter.replaceData(habits);
                Log.e(TAG, habits.toString());
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "取数据失败................");
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        //getView获取的是fragment的onCreateView返回的rootview
        if (getView() == null) {
            return;
        }
        if (mRecyclerView!=null){
         //todo -----------------------当前进度
            //todo 主线---生成V和P的实现类
            //todo 支线---替换RecyclerView为SwipeRefreshLayout以支持下拉刷新
        }

    }

    @Override
    public void showHabits(List<Habit> habits) {

    }

    @Override
    public void showAddHabit() {

    }

    @Override
    public void showHabitDetailsUi(String habitId) {

    }

    @Override
    public void showLoadingHabitsError() {

    }

    @Override
    public void showNoHabits() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setPresenter(HabitListContract.Presenter presenter) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
