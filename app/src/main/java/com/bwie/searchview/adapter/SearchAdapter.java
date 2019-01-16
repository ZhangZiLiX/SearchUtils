package com.bwie.searchview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bwie.searchview.R;
import com.bwie.searchview.bean.SearchContentBean;

import java.util.List;


/**
 * date:2019/1/12
 * author:张自力(DELL)
 * function:  搜索历史的适配器
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context mContext;
    private List<SearchContentBean> mSearchContentBeanList;

    public SearchAdapter(Context context, List<SearchContentBean> searchContentBeanList) {
        mContext = context;
        mSearchContentBeanList = searchContentBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.searchview_adapter,null);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         holder.mTxtContent.setText(mSearchContentBeanList.get(position).getSearchString());
    }

    @Override
    public int getItemCount() {
        return mSearchContentBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTxtContent;

        public ViewHolder(View itemView) {
           super(itemView);
            mTxtContent = itemView.findViewById(R.id.txt_content);
       }
   }

}
