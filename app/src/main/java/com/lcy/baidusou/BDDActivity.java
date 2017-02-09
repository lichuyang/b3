package com.lcy.baidusou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by lcy on 2017/2/6.
 */
public class BDDActivity extends Activity{
    private WebView bddView;
    private Intent intent;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdd);
        intent = getIntent();
        url = intent.getStringExtra("url");
        bddView = (WebView) findViewById(R.id.bdd);
        WebSettings settings = bddView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        bddView.loadUrl(url);
        bddView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("url", url);
                if(url.startsWith("baiduyun:")){
                    if(isAppInstalled(BDDActivity.this, "com.baidu.netdisk")){
                        //url = "baiduyun://127.0.0.1/action.SAVE?shareid=1348796053&uk=1228255694&username=全力搜";
                        url = "baiduyun://127.0.0.1/action.DOWNLOAD?shareid=1348796053&uk=1228255694&username=全力搜";
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    }else{
                        Toast.makeText(BDDActivity.this, "请下载百度网盘客户端！",Toast.LENGTH_SHORT ).show();
                    }
                }else{
                }
                return true;
            }
        });

    }

    private boolean isAppInstalled(Context context, String packagename)
    {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if(packageInfo ==null){
            //System.out.println("没有安装");
            return false;
        }else{
            //System.out.println("已经安装");
            return true;
        }
    }

}
