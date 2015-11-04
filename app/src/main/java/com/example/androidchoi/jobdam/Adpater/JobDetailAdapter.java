package com.example.androidchoi.jobdam.Adpater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.example.androidchoi.jobdam.ExpandableChildAddressItemView;
import com.example.androidchoi.jobdam.ExpandableChildContentItemView;
import com.example.androidchoi.jobdam.ExpandableGroupItemView;
import com.example.androidchoi.jobdam.Model.AddressData;
import com.example.androidchoi.jobdam.Model.ContentData;
import com.example.androidchoi.jobdam.Model.GroupData;
import com.example.androidchoi.jobdam.Model.ChildData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Choi on 2015-11-04.
 */
public class JobDetailAdapter extends BaseExpandableListAdapter {

    List<GroupData> mItems = new ArrayList<GroupData>();
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int TYPE_INDEX_CONTENT = 0;
    private static final int TYPE_INDEX_ADDRESS = 1;
    private static final int TYPE_INDEX_QUESTION = 2;

    public void add(String title, ChildData content) {
        GroupData data = new GroupData(title, content);
        mItems.add(data);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItems.get(groupPosition).getChildData();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return (long)groupPosition<<32 | 0xffffffff;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (long)groupPosition << 32 | childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandableGroupItemView view;
        if(convertView != null){
            view = (ExpandableGroupItemView)convertView;
        }else {
            view = new ExpandableGroupItemView(parent.getContext());
        }
        view.setExpandableTitle(mItems.get(groupPosition));
        return view;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        switch(getChildType(groupPosition,childPosition)){
            case TYPE_INDEX_CONTENT : {
                ExpandableChildContentItemView view;
                if(convertView != null){
                    view = (ExpandableChildContentItemView)convertView;
                } else {
                    view = new ExpandableChildContentItemView(parent.getContext());
                }
                view.setExpandableContent((ContentData)mItems.get(groupPosition).getChildData());
                return view;
            }
            case TYPE_INDEX_ADDRESS : {
                ExpandableChildAddressItemView view;
                if(convertView != null){
                    view = (ExpandableChildAddressItemView)convertView;
                } else {
                    view = new ExpandableChildAddressItemView(parent.getContext());
                }
                view.setExpandableAddress((AddressData)mItems.get(groupPosition).getChildData());
                return view;
            }
            case TYPE_INDEX_QUESTION :
            default : {
                return convertView;
            }
        }
    }
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        ChildData data = mItems.get(groupPosition).getChildData();
        if(data instanceof ContentData){
            return TYPE_INDEX_CONTENT;
        } else if(data instanceof AddressData){
            return TYPE_INDEX_ADDRESS;
        } else{ // data instanceof QuestionData
            return TYPE_INDEX_QUESTION;
        }
    }
    @Override
    public int getChildTypeCount() {
        return 3;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}