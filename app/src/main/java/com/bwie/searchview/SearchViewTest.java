package com.bwie.searchview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bwie.searchview.adapter.SearchAdapter;
import com.bwie.searchview.bean.SearchContentBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.bwie.searchview.R.id.txt_search_go;

/**
 * date:2019/1/16
 * author:张自力(DELL)
 * function:
 */

public class SearchViewTest extends LinearLayout implements View.OnClickListener {

    private String serachContent="";//搜索内容存储
    private boolean serachBtn=false;//搜索按钮改变事件标识
    private boolean serachWeb=false;//搜索webview网址改变事件标识
    private ImageView imgBack;
    private SearchView searchview;
    private TextView txtSearchGo;
    private TextView txtSearchBack;
    private RecyclerView rvSearchlog;
    private ScrollView scrollviewSearchlog;
    private Button btnClear;
    private SharedPreferences mSearchSp;
    private List<SearchContentBean> mList;
    private SearchAdapter mSearchAdapter;
    private LinearLayout mLlsearchAndLog;
    private WebView mWebviewSearch;
    private Pattern mHttpPattern;

    //重写三个方法
    public SearchViewTest(Context context) {
        this(context, null);
    }

    public SearchViewTest(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchViewTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化数据  加载布局
        initViews(context, attrs, defStyleAttr);
        //初始化SP
        initSharedProferences(context, attrs, defStyleAttr);
        //初始化搜索历史的list 和 Adapter对象
        initSearchListAndAdapter(context);
        //对搜索框SearchView进行监听
        setSearchViewOnClickListener(context, attrs, defStyleAttr);


    }

    /**
     * 对搜索框SearchView进行监听
     *
     * */
    private void setSearchViewOnClickListener(Context context, AttributeSet attrs, int defStyleAttr) {
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击右侧搜索按钮时  触发的方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //输入框内容发生变化时 触发的方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //得到搜索的内容
                serachContent = newText;
                return true;
            }
        });


    }

    /**
     * 初始化搜索历史的list 和 Adapter对象
     *
     * */
    private void initSearchListAndAdapter(Context context) {
        //搜索
        mList = new ArrayList<>();
        mSearchAdapter = new SearchAdapter(context, mList);
        rvSearchlog.setAdapter(mSearchAdapter);
        //刷新事件
        getRefreshSearchData();
    }

    /**
     * //刷新事件
     * */
    private void getRefreshSearchData() {
        //从sp中得到数据
        String requestSearchContent = mSearchSp.getString("searchContent", "");
        //将数据加入集合中
        if(!requestSearchContent.equals("")){
            //首先展示历史区域
            scrollviewSearchlog.setVisibility(VISIBLE);
            //加入数据
            mList.add(new SearchContentBean(requestSearchContent));
            mSearchAdapter.notifyDataSetChanged();
        }else{
            //为空就隐藏
            scrollviewSearchlog.setVisibility(GONE);
        }
    }

    /**
     * 初始化SP
     *
     * */
    private void initSharedProferences(Context context, AttributeSet attrs, int defStyleAttr) {
        //创建一个sp存储
        mSearchSp =getContext().getSharedPreferences("search", Context.MODE_PRIVATE);
    }

    /**
     * 初始化数据  加载布局
     *
     */
    private void initViews(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        //View view = inflate(context, R.layout.searchview_utils, this);
        View view = LayoutInflater.from(context).inflate(R.layout.searchview_utils, this);

        //初始化正则
        mHttpPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");

        //找控件
        searchview = (SearchView) view.findViewById(R.id.searchview);//搜索框
        txtSearchGo = (TextView) view.findViewById(txt_search_go);//搜索按钮
        txtSearchBack = (TextView) view.findViewById(R.id.txt_search_back);//返回按钮  默认隐藏
        rvSearchlog = (RecyclerView) view.findViewById(R.id.rv_searchlog);//历史记录 rv
        scrollviewSearchlog = (ScrollView) view.findViewById(R.id.scrollview_searchlog);//包裹rv历史记录 默认隐藏
        btnClear = (Button) view.findViewById(R.id.btn_clear);//清空历史记录
        mLlsearchAndLog = view.findViewById(R.id.ll_searchandlog);//包含搜索框和历史的容器
        mWebviewSearch = view.findViewById(R.id.webview_search);//网址搜索框

        //布局管理器
        LinearLayoutManager layoutManagerSearch = new GridLayoutManager(context,3);
        rvSearchlog.setLayoutManager(layoutManagerSearch);

        //webview设置
        setWebView();

        //事件监听
        txtSearchGo.setOnClickListener(this);
        txtSearchBack.setOnClickListener(this);
        btnClear.setOnClickListener(this);

    }

    /**
     * webview设置
     *
     * */
    private void setWebView() {

        WebSettings webSettings = mWebviewSearch.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 设置缓存
        webSettings.setJavaScriptEnabled(true);//设置能够解析Javascript
        webSettings.setDomStorageEnabled(true);//设置适应Html5 //重点是这个设置

        mWebviewSearch.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                //接受证书  忽略ssl证书错误
                handler.proceed();
            }
        });
    }

    /**
     * 点击事件
     * */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == txt_search_go) {//作用  点击之后
            //1 切换按钮状态(搜索按钮隐藏  返回按钮展示)
            if (!serachBtn) {
                //按钮切换
                txtSearchGo.setVisibility(View.GONE);
                txtSearchBack.setVisibility(View.VISIBLE);
                //将搜索内容存储
                //mSearchSp.getString("searchContent", "");
                boolean issearchContent = mSearchSp.edit().putString("searchContent", serachContent).commit();
                if (issearchContent) {
                    //刷新数据
                    if (serachContent.equals("")) {
                        Toast.makeText(getContext(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    } else {
                        //添加数据
                        //Toast.makeText(getContext(), "存储数据成功!", Toast.LENGTH_SHORT).show();
                        //加完之后刷新
                        getRefreshSearchData();
                        //判断输入的是否是一个网址
                        //开始判断了
                        if (mHttpPattern.matcher(serachContent).matches()) {
                            //这是一个网址链接
                            Toast.makeText(getContext(), "加载中……", Toast.LENGTH_SHORT).show();
                            //将隐藏的webview展示  搜索容器隐藏
                            mWebviewSearch.setVisibility(View.VISIBLE);
                            mLlsearchAndLog.setVisibility(View.GONE);
                            mWebviewSearch.loadUrl(serachContent);//加载url
                        } else {
                            //这不是一个网址链接
                            Toast.makeText(getContext(), "这不是一个网址", Toast.LENGTH_SHORT).show();
                            mWebviewSearch.setVisibility(View.GONE);
                            mLlsearchAndLog.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "存储数据失败！", Toast.LENGTH_SHORT).show();
                    ;
                }
            } else {
                txtSearchGo.setVisibility(View.VISIBLE);
                txtSearchBack.setVisibility(View.GONE);
            }

            //2 改变按钮标识
            serachBtn = true;

            // 进行请求接口  进行网络数据请求

        } else if (i == R.id.txt_search_back) {//作用  点击之后
            //1 切换按钮状态(搜索按钮展示  返回按钮隐藏)
            if (!serachBtn) {
                txtSearchGo.setVisibility(View.GONE);
                txtSearchBack.setVisibility(View.VISIBLE);
            } else {
                txtSearchGo.setVisibility(View.VISIBLE);
                txtSearchBack.setVisibility(View.GONE);
            }
            //2 改变按钮标识
            serachBtn = false;


        } else if (i == R.id.btn_clear) {
            boolean spclearistrue = mSearchSp.edit().clear().commit();
            if (spclearistrue) {
                Toast.makeText(getContext(), "清空数据成功！", Toast.LENGTH_SHORT).show();
                ;
                //刷新数据
                initSearchListAndAdapter(getContext());
            } else {
                Toast.makeText(getContext(), "清空数据失败！", Toast.LENGTH_SHORT).show();
                ;
            }


        }
    }


}
