package com.lcy.baidusou.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcy.baidusou.BDDActivity;
import com.lcy.baidusou.R;
import com.lcy.baidusou.bean.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcy on 2017/2/4.
 */
public class ResultAdapter extends BaseAdapter{
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private List<SearchResult> searchResults = new ArrayList<SearchResult>();
    private Context context;
        /**构造函数*/
        public ResultAdapter(Context context, List<SearchResult> searchResults) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.searchResults = searchResults;
        }

        @Override
        public int getCount() {
            Log.d("searchResults.size()", searchResults.size()+"");
            return searchResults.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**书中详细解释该方法*/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.search_listitem,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.item);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.title.setText(searchResults.get(position).getTitle());

            /**为Button添加点击事件*/
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] items = new String[] { "保存到百度网盘", "百度网盘下载" ,"分享资源"};
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == 0){
                                        String url = "baiduyun://127.0.0.1/action.SAVE?shareid="+searchResults.get(position).getId()+"&uk="+searchResults.get(position).getUserId()+"&username=全力搜";
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(url);
                                        intent.setData(content_url);
                                        context.startActivity(intent);
                                    }else if(which == 1) {
                                        String url = "baiduyun://127.0.0.1/action.DOWNLOAD?shareid="+searchResults.get(position).getId()+"&uk="+searchResults.get(position).getUserId()+"&username=全力搜";
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(url);
                                        intent.setData(content_url);
                                        context.startActivity(intent);
                                    }else if(which == 2) {
                                        Intent share_intent = new Intent();
                                        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                                        share_intent.setType("text/plain");//设置分享内容的类型
                                        share_intent.putExtra(Intent.EXTRA_SUBJECT, "全力搜分享资源下载");//添加分享内容标题
                                        share_intent.putExtra(Intent.EXTRA_TEXT, searchResults.get(position).getTitle() +"下载地址:"+"http://www.vdashuju.com/file-"+searchResults.get(position).getUserId()+"-"+searchResults.get(position).getId()+".html  百度网盘下载-全力搜");//添加分享内容
                                        //创建分享的Dialog
                                        share_intent = Intent.createChooser(share_intent, "全力搜分享资源下载");
                                        context.startActivity(share_intent);
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
                    if(isAppInstalled(context, "com.baidu.netdisk")){
                        dialog.show();
                    }else{
                        Toast.makeText(context, "请下载百度网盘客户端！",Toast.LENGTH_SHORT ).show();
                    }
                   /* Intent bdIntent = new Intent();
                    bdIntent.setClass(context, BDDActivity.class);
                    bdIntent.putExtra("url", "https://pan.baidu.com/wap/link?uk="+searchResults.get(position).getUserId()+"&shareid="+searchResults.get(position).getId());
                    context.startActivity(bdIntent);*/
                }
            });

            return convertView;
        }
    /**存放控件*/
    public final class ViewHolder{
        public TextView title;
        public LinearLayout itemLayout ;
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
