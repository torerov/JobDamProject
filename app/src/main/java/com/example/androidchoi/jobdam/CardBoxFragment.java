package com.example.androidchoi.jobdam;


import android.app.Activity;
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
import android.widget.Toast;

import com.example.androidchoi.jobdam.Adpater.CardItemAdapter;
import com.example.androidchoi.jobdam.Manager.MyApplication;
import com.example.androidchoi.jobdam.Manager.NetworkManager;
import com.example.androidchoi.jobdam.Model.MyCardLab;
import com.example.androidchoi.jobdam.Model.MyCard;
import com.example.androidchoi.jobdam.Model.MyCards;
import com.example.androidchoi.jobdam.Model.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBoxFragment extends Fragment {

    private static final int REQUEST_MODIFY = 1;
    private static final int REQUEST_NEW = 2;

    ListView mListView;
    CardItemAdapter mAdapter;
    FloatingActionMenu fam;
    EditText mSearchEdit;
    ImageView mDeleteImage;
    private ArrayList<MyCards> mCardList;
    TextView mCountTextView;

    public CardBoxFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == REQUEST_MODIFY){
//            Toast.makeText(getActivity(), CardLab.get(getActivity()).getCardList().get(0).getTitle(), Toast.LENGTH_SHORT).show();
        }else if(requestCode == REQUEST_NEW){
            mListView.smoothScrollToPositionFromTop(0, 0, 500);
        }
        mCountTextView.setText("총 " + mAdapter.getCount() + "건");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView subTitle = (TextView) getActivity().findViewById(R.id.text_subtitle);
        subTitle.setText(R.string.card_box);

        Toast.makeText(getActivity(),"카드를 불러옵니다!", Toast.LENGTH_SHORT).show();
        NetworkManager.getInstance().showMyMemo(getActivity(),
                User.USER_NAME, new NetworkManager.OnResultListener<MyCardLab>() {
            @Override
            public void onSuccess(MyCardLab result) {
                mCardList = result.getCardList();
                mAdapter.setItems(mCardList);
                mCountTextView.setText("총 " + mAdapter.getCount() + "건");
            }
            @Override
            public void onFail(int code) {
                Toast.makeText(MyApplication.getContext(), code + "", Toast.LENGTH_SHORT).show();
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
        View view = inflater.inflate(R.layout.fragment_card_box, container, false);
        View searchHeaderView = inflater.inflate(R.layout.view_item_search_header, null);
        View countHeaderView = inflater.inflate(R.layout.view_item_count_header, null);
        mListView = (ListView) view.findViewById(R.id.listview_card);
        mListView.addHeaderView(searchHeaderView);
        mListView.addHeaderView(countHeaderView, null, false);
        mDeleteImage = (ImageView)searchHeaderView.findViewById(R.id.image_search_delete);
        mSearchEdit = (EditText)searchHeaderView.findViewById(R.id.editText_search_bar);
        mSearchEdit.setHint("태그를 검색해주세요");
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ArrayList<CardData> cardList = new ArrayList<CardData>();
//                for (CardData c : mCardList) {
//                    if (c.getTitle().contains(s)) {
//                        cardList.add(c);
//                    }
//                }
//                mAdapter.setItems(cardList);
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
        mAdapter = new CardItemAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCard data = (MyCard) mAdapter.getItem(position - mListView.getHeaderViewsCount());
                Intent intent = new Intent(getActivity(), CardWriteActivity.class);
                intent.putExtra(MyCard.CARD_ITEM, data);
                intent.putExtra(MyCard.CARD_NEW, false);
//                intent.putExtra(CardData.CARDPOSITION, position - mListView.getHeaderViewsCount());
                startActivityForResult(intent, REQUEST_MODIFY);
            }
        });
        fam = (FloatingActionMenu) view.findViewById(R.id.menu);
        FloatingActionButton addCardButton = (FloatingActionButton) view.findViewById(R.id.fab_write_card);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardWriteActivity.class);
                intent.putExtra(MyCard.CARD_NEW, true);
                startActivityForResult(intent, REQUEST_NEW);
                fam.close(true);
            }
        });
        FloatingActionButton addCategoryButton = (FloatingActionButton) view.findViewById(R.id.fab_add_category);

        mCountTextView = (TextView) view.findViewById(R.id.text_item_count);
        return view;
    }
}
