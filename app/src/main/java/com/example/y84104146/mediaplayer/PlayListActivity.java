package com.example.y84104146.mediaplayer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.y84104146.mediaplayer.wxapi.WXEntryActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.y84104146.mediaplayer.SongAdapter.keyVaule;

public class PlayListActivity extends AppCompatActivity{

    public static List<Song> songList = new ArrayList<Song>();

    public static List<String> info = new ArrayList<>();
//    public HashMap<String, String> info = new HashMap<>();
    public static List<Integer> value =  new ArrayList<>();
    public static List<Boolean> likeOrNot = new ArrayList<>();
    public static List<Integer> AlbumPic =  new ArrayList<>();
    public static HashMap<Integer,Song> list = new HashMap<>();
    public static Integer itemCount = 0;

//    public ImageView albumPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initSongs();
        SongAdapter adapter = new SongAdapter(PlayListActivity.this, R.layout.song_item, songList);
        ListView listView = (ListView) findViewById(R.id.playlist);
        listView.setAdapter(adapter);
        itemCount = listView.getAdapter().getCount();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song =  list.get(position);
                MainActivity.mediaPlayer.stop();
                keyVaule = position;
                Intent intent = new Intent(PlayListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.isOne:
                Toast.makeText(this,"You clicked one",Toast.LENGTH_SHORT).show();
                break;
            case R.id.isTwo:
                Intent intent = new Intent(PlayListActivity.this, WXEntryActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return true;
    }

//    获取当前进程名
    private String getProcessName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return null;
            }
            for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
                if (proInfo.pid == android.os.Process.myPid()) {
                    if (proInfo.processName != null) {
                        return proInfo.processName;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


//    传入歌曲id 获取其专辑图片id------int
    public static int returnAlbumPicId(Song s){
        return s.getAlbumPicture();
    }

    public void initSongs() {
        if(itemCount == 0){
            Song s0 = new Song("只要平凡（电影《我不是药神》主题曲） -","张杰/张碧晨",0,R.mipmap.zypf, false);
            songList.add(s0);
            AlbumPic.add(s0.getAlbumPicture());
            value.add(s0.getSongId());
            info.add(s0.getSongName()+s0.getSongSinger());
//        添加HashMap<songId,Song>
            list.put(s0.getSongId(),s0);
            likeOrNot.add(s0.getLiked());

            Song s1 = new Song("可能否（30秒铃声） -","木小雅",1,R.mipmap.knf,false);
            songList.add(s1);
            AlbumPic.add(s1.getAlbumPicture());
            value.add(s1.getSongId());
            info.add(s1.getSongName()+s1.getSongSinger());
            list.put(s1.getSongId(),s1);
            likeOrNot.add(s1.getLiked());

            Song s2 = new Song("哑巴  -","薛之谦",2,R.mipmap.yb,false);
            songList.add(s2);
            AlbumPic.add(s2.getAlbumPicture());
            value.add(s2.getSongId());
            info.add(s2.getSongName()+s2.getSongSinger());
            list.put(s2.getSongId(),s2);
            likeOrNot.add(s2.getLiked());

            Song s3 =  new Song("如歌-（电视剧《烈火如歌》主题曲） -","张杰",3,R.mipmap.rg,false);
            songList.add(s3);
            AlbumPic.add(s3.getAlbumPicture());
            value.add(s3.getSongId());
            info.add(s3.getSongName()+s3.getSongSinger());
            list.put(s3.getSongId(),s3);
            likeOrNot.add(s3.getLiked());

            Song s4 = new Song("齐天-（电影《悟空传》主题曲） -","华晨宇",4,R.mipmap.qt,false);
            songList.add(s4);
            AlbumPic.add(s4.getAlbumPicture());
            value.add(s4.getSongId());
            info.add(s4.getSongName()+s4.getSongSinger());
            list.put(s4.getSongId(),s4);
            likeOrNot.add(s4.getLiked());

            Song s5 = new Song("喜欢你 (原唱: Beyond)  -","邓紫棋",5,R.mipmap.xhn,false);
            songList.add(s5);
            AlbumPic.add(s5.getAlbumPicture());
            value.add(s5.getSongId());
            info.add(s5.getSongName()+s5.getSongSinger());
            list.put(s5.getSongId(),s5);
            likeOrNot.add(s5.getLiked());

            Song s6 = new Song("悟空  -","戴荃",6,R.mipmap.wk,false);
            songList.add(s6);
            AlbumPic.add(s6.getAlbumPicture());
            value.add(s6.getSongId());
            info.add(s6.getSongName()+s6.getSongSinger());
            list.put(s6.getSongId(),s6);
            likeOrNot.add(s6.getLiked());

            Song s7 = new Song("醉赤壁  -","林俊杰",7,R.mipmap.zcb,false);
            songList.add(s7);
            AlbumPic.add(s7.getAlbumPicture());
            value.add(s7.getSongId());
            info.add(s7.getSongName()+s7.getSongSinger());
            list.put(s7.getSongId(),s7);
            likeOrNot.add(s7.getLiked());

            Song s8 = new Song("离人愁  -","李袁杰",8,R.mipmap.lrc,false);
            songList.add(s8);
            AlbumPic.add(s8.getAlbumPicture());
            value.add(s8.getSongId());
            info.add(s8.getSongName()+s8.getSongSinger());
            list.put(s8.getSongId(),s8);
            likeOrNot.add(s8.getLiked());

            Song s9 = new Song("不爱我就拉倒  -","周杰伦",9,R.mipmap.bawjld,false);
            songList.add(s9);
            AlbumPic.add(s9.getAlbumPicture());
            value.add(s9.getSongId());
            info.add(s9.getSongName()+s9.getSongSinger());
            list.put(s9.getSongId(),s9);
            likeOrNot.add(s9.getLiked());

            Song s10 = new Song("我们-（电影《后来的我们》主题曲） -","陈奕迅",10,R.mipmap.wm,false);
            songList.add(s10);
            AlbumPic.add(s10.getAlbumPicture());
            value.add(s10.getSongId());
            info.add(s10.getSongName()+s10.getSongSinger());
            list.put(s10.getSongId(),s10);
            likeOrNot.add(s10.getLiked());
        }

    }

}
