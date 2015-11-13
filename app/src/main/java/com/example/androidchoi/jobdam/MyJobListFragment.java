package com.example.androidchoi.jobdam;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidchoi.jobdam.Adpater.MyJobItemAdapter;
import com.example.androidchoi.jobdam.Manager.MyJobRequest;
import com.example.androidchoi.jobdam.Manager.NetworkManager;
import com.example.androidchoi.jobdam.Manager.NetworkRequest;
import com.example.androidchoi.jobdam.Model.Job;
import com.example.androidchoi.jobdam.Model.MyJobList;
import com.example.androidchoi.jobdam.Model.MyJobs;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyJobListFragment extends Fragment {

    ListView mListView;
    MyJobItemAdapter mAdapter;
    EditText mSearchEdit;
    ImageView mDeleteImage;
    private ArrayList<MyJobs> mJobList;
    TextView mCountTextView;

    public MyJobListFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MyJobRequest request = new MyJobRequest("kim");
        NetworkManager.getInstance().getNetworkData(getActivity(), request, new NetworkManager.OnResultListener<MyJobList>() {
            @Override
            public void onSuccess(NetworkRequest<MyJobList> request, MyJobList result) {
                mJobList = result.getJobList();
                mAdapter.setItems(mJobList);
                mCountTextView.setText("총 " + mAdapter.getCount() + "건");
            }
            @Override
            public void onFail(NetworkRequest<MyJobList> request, int code) {
            }
        });

        FrameLayout touchInterceptor = (FrameLayout)getActivity().findViewById(R.id.touchInterceptor);
        touchInterceptor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mSearchEdit.isFocused()) {
                        Rect outRect = new Rect();
                        mSearchEdit.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            mSearchEdit.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_job_list, container, false);
        View searchHeaderView = inflater.inflate(R.layout.view_item_search_header, null);
        View countHeaderView = inflater.inflate(R.layout.view_item_count_header, null);
        mListView = (ListView)view.findViewById(R.id.listview_my_job);
        mListView.addHeaderView(searchHeaderView);
        mListView.addHeaderView(countHeaderView, null, false);
        mDeleteImage = (ImageView)searchHeaderView.findViewById(R.id.image_search_delete);
        mSearchEdit = (EditText)searchHeaderView.findViewById(R.id.editText_search_bar);
        mSearchEdit.setHint("기업을 검색해주세요");
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ArrayList<JobData> jobList = new ArrayList<JobData>();
//                for (JobData c : mJobList) {
//                    if (c.toString().contains(s)) {
//                        jobList.add(c);
//                    }
//                }
//                mAdapter.setItems(jobList);
//                mCountTextView.setText("총 " + mAdapter.getCount() + "건");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (!string.equals("")) {
                    mDeleteImage.setVisibility(View.VISIBLE);
                } else {
                    mDeleteImage.setVisibility(View.GONE);
                }
            }
        });
        mAdapter = new MyJobItemAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Job data = (Job) mAdapter.getItem(position - mListView.getHeaderViewsCount());
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                intent.putExtra(Job.JOBITEM, data);
                startActivity(intent);
            }
        });
        mCountTextView = (TextView)view.findViewById(R.id.text_item_count);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
//
//    public static final String ShowMyScrapUrl = "http://52.69.235.46:3000/showmyscrap/%s";
//    class MyJobTask extends AsyncTask<String,Integer,String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String name = params[0];
//            String urlText = String.format(ShowMyScrapUrl, name);
//            try {
//                URL url = new URL(urlText);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestProperty("Accept", "application/json");
//                int code = conn.getResponseCode();
//                if (code == HttpURLConnection.HTTP_OK) {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line).append("\n\r");
//                    }
//                    return sb.toString();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if(s != null){
//                Gson gson = new Gson();
//                MyJobList.get(getContext(), gson.fromJson(s, MyJobList.class));
//            }else {
//                MyJobList.get(getContext());
//                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
//            }
//            mJobList = MyJobList.get(getContext()).getJobList();
//            mAdapter.setItems(mJobList);
//        }
//    }
}
