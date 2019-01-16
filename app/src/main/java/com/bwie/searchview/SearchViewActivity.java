package com.bwie.searchview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SearchViewActivity extends AppCompatActivity {

    private SearchViewTest searchviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        //初始化控件
        //initView();


    }

    /*private void initView() {
        searchviewText = (SearchViewTest) findViewById(R.id.searchview_text);
    }*/
}
