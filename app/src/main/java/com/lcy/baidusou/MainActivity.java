package com.lcy.baidusou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcy.baidusou.adapter.ResultAdapter;
import com.lcy.baidusou.bean.SearchResult;
import com.lcy.baidusou.ui.PullUpLoadMoreListView;
import com.shelwee.update.UpdateHelper;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private EditText inputEdit;
    private Button searchButton;
    private String SEARCH_URL = "http://www.vdashuju.com/v1/search/";
    private int pageNum = 1;
    private List<SearchResult> searchResults = new ArrayList<SearchResult>();
    private ResultAdapter resultAdapter;
    private PullUpLoadMoreListView listView;
    private RelativeLayout textLayout;
    private TextView numberView;
    private TextView tip2View;
    private boolean isLoading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateHelper updateHelper = new UpdateHelper.Builder(this)
                .checkUrl("http://www.xxx.com/static/update.json")
                .isHintNewVersion(false)
                .isAutoInstall(true) //设置为false需在下载完手动点击安装;默认值为true，下载后自动安装。
                .build();
        updateHelper.check();
        inputEdit = (EditText) findViewById(R.id.inputText);
        searchButton = (Button) findViewById(R.id.search);
        listView = (PullUpLoadMoreListView)findViewById(R.id.listview);
        textLayout = (RelativeLayout)findViewById(R.id.text_layout);
        numberView = (TextView)findViewById(R.id.total_number);
        tip2View = (TextView)findViewById(R.id.tip2);
        String tip2Text = "本APP可以直接打开百度网盘客户端保存和下载文件，如未安装百度网盘官方客户端，请安装后使用。浏览器可访问 <a href='http://www.vdashuju.com/?android'>http://www.vdashuju.com</a> 。";
        tip2View.setText(Html.fromHtml(tip2Text));
        tip2View.setMovementMethod(LinkMovementMethod.getInstance());
        listView.setAdapter(null);
        listView.setOnLoadMoreListener(new PullUpLoadMoreListView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                if(isLoading)
                    return;
                isLoading = true;
                OkHttpClient mOkHttpClient = new OkHttpClient();
                //创建一个Request
                String url = SEARCH_URL + pageNum + "/?wd=" + inputEdit.getText().toString();
                Log.d("url", url);
                final Request request = new Request.Builder()
                        .url(url)
                        .build();
                //new call
                Call call = mOkHttpClient.newCall(request);
                //请求加入调度
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        //Log.d("errpor", "error");
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Toast.makeText(MainActivity.this, "资源搜索失败！!", Toast.LENGTH_SHORT).show();
                                          }
                                      });
                        isLoading = false;
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        String jsonResult =  response.body().string();
                        try {
                            JSONObject jo = new JSONObject(jsonResult);
                            int count = jo.getInt("count");
                            int index = jo.getInt("index");
                            final JSONArray ja = jo.getJSONArray("data");
                            for(int i=0;i<ja.length();i++){
                                SearchResult sr = new SearchResult();
                                sr.setId(ja.getJSONObject(i).getString("id"));
                                sr.setUserId(ja.getJSONObject(i).getString("userId"));
                                sr.setTitle(ja.getJSONObject(i).getString("title"));
                                searchResults.add(sr);
                            }
                            resultAdapter = new ResultAdapter(MainActivity.this, searchResults);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textLayout.setVisibility(View.GONE);
                                    //listView.setAdapter(resultAdapter);
                                    resultAdapter.notifyDataSetChanged();
                                    pageNum += 1;
                                    isLoading = false;
                                    listView.setLoadState(false);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            isLoading = false;
                        }
                    }
                });
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0) ;

                if(isLoading) {
                    Toast.makeText(MainActivity.this, "正在加载中，请稍后!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //创建okHttpClient对象
                if(inputEdit.getText() == null || inputEdit.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, "请输入搜索内容!", Toast.LENGTH_SHORT).show();
                }else {
                    isLoading = true;
                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    //创建一个Request
                    String url = SEARCH_URL + pageNum + "/?wd=" + inputEdit.getText().toString();
                    Log.d("url", url);
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    //new call
                    Call call = mOkHttpClient.newCall(request);
                    //请求加入调度
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "资源搜索失败！!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            isLoading = false;
                        }

                        @Override
                        public void onResponse(final Response response) throws IOException {
                            searchResults.clear();
                            String jsonResult =  response.body().string();
                            if (jsonResult.equals("{}")) {
                                isLoading = false;
                                return;
                            }
                            try {
                                JSONObject jo = new JSONObject(jsonResult);
                                final int count = jo.getInt("count");
                                int index = jo.getInt("index");
                                JSONArray ja = jo.getJSONArray("data");
                                for(int i=0;i<ja.length();i++){
                                    SearchResult sr = new SearchResult();
                                    sr.setId(ja.getJSONObject(i).getString("id"));
                                    sr.setUserId(ja.getJSONObject(i).getString("userId"));
                                    sr.setTitle(ja.getJSONObject(i).getString("title"));
                                    searchResults.add(sr);
                                }
                                resultAdapter = new ResultAdapter(MainActivity.this, searchResults);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        numberView.setText("共有"+count+"条搜索结果");
                                        textLayout.setVisibility(View.GONE);
                                        listView.setAdapter(resultAdapter);
                                        pageNum += 1;
                                        isLoading = false;
                                    }
                                });

                            } catch (JSONException e) {
                                isLoading = false;
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        /*
         * 将actionBar的HomeButtonEnabled设为ture，
         *
         * 将会执行此case
         */
            case R.id.action_openBD:
                startAPP("com.baidu.netdisk");
                break;
            case R.id.action_share:
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("text/plain");//设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_SUBJECT, "全力搜app下载");//添加分享内容标题
                share_intent.putExtra(Intent.EXTRA_TEXT, "全力搜,百度网盘搜索-www.vdashuju.com-app下载地址http://www.vdashuju.com/download/quanlisou.apk");//添加分享内容
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "全力搜app下载");
                startActivity(share_intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startAPP(String appPackageName){
        try{
            Intent intent = this.getPackageManager().getLaunchIntentForPackage(appPackageName);
            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this, "没有安装", Toast.LENGTH_LONG).show();
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
