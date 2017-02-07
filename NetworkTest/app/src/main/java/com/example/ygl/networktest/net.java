package com.example.ygl.networktest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.ygl.networktest.MainActivity.netisruning;

/**
 * Created by YGL on 2017/2/2.
 */

public class net extends Thread {
    public static final int TXT_MODE = 1;
    public static final int PICTRUE_MODE = 2;
    private int pictruenum=0;
    private String urlstr="";
    private int thedepth=0;
    public net(String urlstr,int thedepth){
        this.urlstr=urlstr;
        this.thedepth=thedepth;
    }
    private void iteration(List<String> urllist,int depth){
        if(depth>0){
            List<String> outurllist=new ArrayList<String>();
            for(int i=0;i<urllist.size();i++){
                String look=urllist.get(i).replaceAll("\\.|/|:", "_");
                if(look.matches("\\w+_jpg")||look.matches("\\w+_png")){
                    openUrl(urllist.get(i),PICTRUE_MODE);
                    pictruenum++;
                }else{
                    if((look.matches("\\w+_html")||look.matches("\\w+_com")||look.matches("\\w+_")||look.matches("\\w+_net"))&&depth>1) {
                        String con = openUrl(urllist.get(i), TXT_MODE);
                        Log.e("YGL", "迭代"+depth+",打开链接:" + urllist.get(i));
                        addUrl(urllist.get(i),con,outurllist);
                    }
                }
            }
            depth--;
            iteration(outurllist,depth);
        }
    }
    private List<String> oldurl=new ArrayList<String>();
    private Boolean compareUrl(String url){
        for(int i=0;i<oldurl.size();i++){
            if(url.equals(oldurl.get(i))){
                return false;
            }
        }
        oldurl.add(url);
        return true;
    }
    private String supplementUrl(String nowurl,String urlfragment){
        String look=nowurl.replaceAll("\\.|/|:", "_");
        if(look.matches("\\w+_")) {
            nowurl=nowurl.substring(0,(nowurl.length()-1));
        }

        if(urlfragment.indexOf("http://")!=-1){
            return urlfragment;
        }else {
            if (urlfragment.indexOf("//")==0){
                return "http:"+urlfragment;
            }else {
                if(urlfragment.indexOf("/")==0){
                    return nowurl+urlfragment;
                }else {
                    return "";
                }
            }
        }
    }
    private void addUrl(String nowurl,String con,List<String> allurl){
        int progress=0;
        int end=0;
        String urlfragment="";
        while ((progress=con.indexOf("href=\"",progress))!=-1){
            end=con.indexOf("\"",progress+6);
            urlfragment=con.substring(progress+6,end);
            String url=supplementUrl(nowurl,urlfragment);
            if((!url.equals(""))&&compareUrl(url)){
                allurl.add(url);
                Log.e("YGL","得到href链接:"+url);
            }
            progress=end;
        }
        /*
        while ((progress=con.indexOf("src=\"",progress))!=-1){
            end=con.indexOf("\"",progress+5);
            urlfragment=con.substring(progress+5,end);
            String url=supplementUrl(nowurl,urlfragment);
            if((url!="")&&compareUrl(url)){
                allurl.add(url);
                Log.e("YGL","得到src链接:"+url);
            }
            progress=end;
        }
        */
        while ((progress=con.indexOf("data-img=\"",progress))!=-1){
            end=con.indexOf("\"",progress+10);
            urlfragment=con.substring(progress+10,end);
            String url=supplementUrl(nowurl,urlfragment);
            if((url!="")&&compareUrl(url)){
                allurl.add(url);
                Log.e("YGL","得到data-img链接:"+url);
            }
            progress=end;
        }
    }

    private String openUrl(final String urlstr,final int MODE) {
                String con="";
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlstr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //connection.setRequestProperty("User-agent", userAgent);
                    //connection.setUseCaches(false);
                    //connection.setInstanceFollowRedirects(false);
                    //connection.connect();
                    InputStream in = connection.getInputStream();
                    if(MODE==PICTRUE_MODE){
                        Bitmap bitmap= BitmapFactory.decodeStream(in);
                        SavePicture.savePicture("/mnt/sdcard/DCIM/mypictrue/",bitmap);
                    }else{
                        // 下面对获取到的输入流进行读取
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        con=response.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
        return con;
    }
    @Override
    public void run(){
        List<String> mylist=new ArrayList<String>();
        String look=urlstr.replaceAll("\\.|/|:", "_");
        if(look.matches("\\w+_html")||look.matches("\\w+_com")||look.matches("\\w+_")||look.matches("\\w+_net")) {
            String con = openUrl(urlstr, TXT_MODE);
            Log.e("YGL","打开链接:" + urlstr);
            addUrl(urlstr,con,mylist);
        }else{
            if(look.matches("\\w+_jpg")||look.matches("\\w+_png")){
                openUrl(urlstr,PICTRUE_MODE);
                pictruenum++;
            }
        }
        iteration(mylist,thedepth);//迭代
        netisruning=false;
    }
}
