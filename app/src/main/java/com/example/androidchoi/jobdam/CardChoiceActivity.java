package com.example.androidchoi.jobdam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidchoi.jobdam.Adpater.CardItemAdapter;
import com.example.androidchoi.jobdam.ItemView.ExpandableChildQuestionItemView;
import com.example.androidchoi.jobdam.Manager.MyApplication;
import com.example.androidchoi.jobdam.Manager.NetworkManager;
import com.example.androidchoi.jobdam.Model.MyCardLab;
import com.example.androidchoi.jobdam.Model.MyCards;

import java.util.ArrayList;

public class CardChoiceActivity extends AppCompatActivity {

    ListView mListView;
    CardItemAdapter mAdapter;
    int mQuestionNum;
    int mJobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_choice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_back);

        Intent intent = getIntent();
        mQuestionNum = intent.getIntExtra(ExpandableChildQuestionItemView.QUESTION_NUM, 0);
        mJobId = intent.getIntExtra(ExpandableChildQuestionItemView.JOB_ID, -1);

        mListView = (ListView)findViewById(R.id.listView_attach_card);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mAdapter = new CardItemAdapter();
        showMyMemo();
        mListView.setAdapter(mAdapter);
    }

    public void showMyMemo() {
        NetworkManager.getInstance().showMyMemo(CardChoiceActivity.this,
                new NetworkManager.OnResultListener<MyCardLab>() {
                    @Override
                    public void onSuccess(MyCardLab result) {
                        mAdapter.setItems(result.getCardList());
                    }
                    @Override
                    public void onFail(int code) {
                        Log.i("error : ", code+"");
                        Toast.makeText(MyApplication.getContext(), "데이터를 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attach, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_attach) {
            SparseBooleanArray isCheckedArray = mListView.getCheckedItemPositions();
            ArrayList<String> CardIdList = new ArrayList<String>();
            for(int i = 0; i < isCheckedArray.size(); i++){
                if(isCheckedArray.valueAt(i) == true)
                    CardIdList.add(((MyCards) mAdapter.getItem(isCheckedArray.keyAt(i))).getId());
            }
            for(int i =0 ; i<CardIdList.size(); i++){
                Log.i("memoId", CardIdList.get(i));
            }
            NetworkManager.getInstance().addQuestionTag(getApplicationContext(), mJobId, CardIdList, mQuestionNum, new NetworkManager.OnResultListener(){
                @Override
                public void onSuccess(Object result) {
                    Log.i("result", result.toString());
                    setResult(RESULT_OK);
                    finish();
                }
                @Override
                public void onFail(int code) {
                    Log.i("error : ", code+"");
                    Toast.makeText(MyApplication.getContext(), "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
