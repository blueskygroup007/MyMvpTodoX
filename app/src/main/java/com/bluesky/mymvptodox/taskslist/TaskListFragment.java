package com.bluesky.mymvptodox.taskslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description:
 */
public class TaskListFragment extends Fragment {

    /* TODO: 2019/3/7 */
    private static final String ARG_PARM1 = "param1";
    private static final String ARG_PARM2 = "param2";
    private int mParam1;
    private String mParam2;

    /**
     * 无参构造函数,必须有
     */
    public TaskListFragment() {
    }

    /**
     * fragment的创建方法,加了两个无用参数来说明用法
     *
     * @param param1
     * @param param2
     * @return
     */
    public static TaskListFragment newInstance(int param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARM1, param1);
        bundle.putString(ARG_PARM2, param2);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //当fragment重新创建时,从bundle中取出参数
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARM1);
            mParam2 = getArguments().getString(ARG_PARM2);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
