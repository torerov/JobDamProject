package com.example.androidchoi.jobdam;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidchoi.jobdam.Adpater.JobItemAdapter;
import com.example.androidchoi.jobdam.Model.JobItemData;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyJobListFragment extends Fragment {

    ListView mListView;
    JobItemAdapter mAdapter;

    public MyJobListFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_job_list, container, false);
        View headerView = inflater.inflate(R.layout.view_job_count_header, null);

        mListView = (ListView)view.findViewById(R.id.listview_my_job);
        mListView.addHeaderView(headerView);
        mAdapter = new JobItemAdapter();
        mListView.setAdapter(mAdapter);

        initData();
        TextView textView = (TextView)view.findViewById(R.id.text_job_item_count);
        textView.setText("총 "+ mAdapter.getCount() + "건");
        return view;
    }
    private void initData() {
        for(int i = 0; i<5; i++) {
            JobItemData data = new JobItemData();
            mAdapter.add(data);
        }
    }
}
