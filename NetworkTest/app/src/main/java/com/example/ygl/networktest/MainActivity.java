package com.example.ygl.networktest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity{
    private Button sendRequest;
    private EditText in;
    private WebView webview;
    private FloatingActionButton fab;
    private Button opentesturl;
    private int thedepth=1;
    public static Boolean netisruning=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview=(WebView)findViewById(R.id.webview);
        webview.loadUrl("http://cn.bing.com");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 根据传入的参数再去加载新的网页
                return true; // 表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
            }
        });

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(netisruning){
                    showRuningString();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("开始爬取");
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.prepare, null);
                    builder.setView(view);
                    final EditText url = (EditText)view.findViewById(R.id.url);
                    url.setText(webview.getUrl());
                    final TextView textview =(TextView)view.findViewById(R.id.depth);
                    textview.setText("深度:1");
                    final SeekBar seekbar=(SeekBar)view.findViewById(R.id.seekbar);
                    seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            textview.setText("深度:"+(progress+1));
                            thedepth=progress+1;
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String urlstr=url.getText().toString();
                            net n=new net(urlstr,thedepth);
                            n.start();
                            netisruning=true;
                            showRuningString();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.show();
                }
            }
        });

        opentesturl=(Button)findViewById(R.id.opentesturl);
        opentesturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl("http://m.mm131.com/xinggan/");
            }
        });
    }
    private void showRuningString(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("爬取中");
        builder.setMessage("正在爬取中\n目前没有暂停功能\n通过强制结束程序以停止爬取");
        builder.setPositiveButton("隐藏",null);
        builder.show();
    }
}
