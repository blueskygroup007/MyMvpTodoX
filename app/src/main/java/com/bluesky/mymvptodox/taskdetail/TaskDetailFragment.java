package com.bluesky.mymvptodox.taskdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluesky.mymvptodox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author BlueSky
 * @date 2019/4/6
 * Description:
 */
public class TaskDetailFragment extends Fragment {
    private static final String ARG_PARAMS = "PARAMS";
    private static TaskDetailFragment mFragment = null;
    private String mParams;

    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance(String params) {
        if (mFragment == null) {
            mFragment = new TaskDetailFragment();
        }
        Bundle args = new Bundle();
        args.putString(ARG_PARAMS, params);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle args = getArguments();
            mParams = args.getString(ARG_PARAMS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_task_detail, container, false);
        TextView tv_params = root.findViewById(R.id.tv_params);
        if (mParams != null) {
            tv_params.setText(mParams);
        }

        FloatingActionButton fab=getActivity().findViewById(R.id.fab);

        return root;
    }
}
