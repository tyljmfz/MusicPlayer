package com.example.y84104146.mediaplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.y84104146.mediaplayer.wxapi.WXEntryActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView playerView;
    private SeekBar playerBar;
    private ImageView playerList;
    private TextView curretTime;
    private TextView totalTime;
    private TextView songInfo;
    private ImageView nextSong;
    private ImageView lastSong;
    private ImageView likeSong;
    private DrawerLayout mDrawerLayout;
    private NavSelectActivity navSelectActivity;
    private NavigationView navigationView;

    public static boolean checkPlayingState = false;

    //<Url,Position>
    public HashMap<String,Integer> AudioPositionMap=new HashMap<>();

    public Phonograph phonograph;
    public static boolean isLikeOrNot = false;
    public static WishList likeList = new WishList(isLikeOrNot);

    public static MediaPlayer mediaPlayer=new MediaPlayer();
    private Timer timer = new Timer();

    public NetworkStatesReceiver networkStatesReceiver;

    public static AlertDialog.Builder builder;

    public HashMap<Integer,String> AudioUrlList = new HashMap<>();

    public App myApplication;
    public HttpProxyCacheServer proxy;
    public String proxyUrl;

    public void buildAudioList(){

        AudioUrlList.put(0,"http://nf01.sycdn.kuwo.cn/resource/n1/25/35/401360027.mp3");
        AudioUrlList.put(1,"http://nf01.sycdn.kuwo.cn/resource/n2/59/7/2076410040.mp3");
        AudioUrlList.put(2,"http://nf01.sycdn.kuwo.cn/resource/n3/93/94/2827541245.mp3");
        AudioUrlList.put(3,"http://nf01.sycdn.kuwo.cn/resource/n1/75/44/1637607611.mp3");
        AudioUrlList.put(4,"http://ri01.sycdn.kuwo.cn/resource/n2/27/27/2536336032.mp3");
        AudioUrlList.put(5,"http://re01.sycdn.kuwo.cn/resource/n2/69/33/1625071345.mp3");
        AudioUrlList.put(6,"http://ri01.sycdn.kuwo.cn/resource/n3/25/98/2109636378.mp3");
        AudioUrlList.put(7,"http://ra01.sycdn.kuwo.cn/resource/n3/128/3/11/3233852694.mp3");
        AudioUrlList.put(8,"http://nf01.sycdn.kuwo.cn/resource/n2/84/37/3429731527.mp3");
        AudioUrlList.put(9,"http://nf01.sycdn.kuwo.cn/resource/n1/78/99/2555924131.mp3");
        AudioUrlList.put(10,"http://nf01.sycdn.kuwo.cn/resource/n1/90/93/2398464589.mp3");

    }

    public String getValue(HashMap<Integer,String> map, Integer value){
        buildAudioList();
        String keyUrl = null;

        keyUrl = map.get(value);
        return keyUrl;
    }

    //传入的是字符串 0 1 2
    public String synKey="";
    private void initMediaPlayer(){
        try{
            if(mediaPlayer==null){
                mediaPlayer=new MediaPlayer();
            }
            songInfo.setText(PlayListActivity.info.get(SongAdapter.keyVaule));
            if(SongAdapter.keyVaule != 0){
                if( PlayListActivity.likeOrNot.get(SongAdapter.keyVaule)){
                    likeSong.setImageResource(R.mipmap.yeslike);
                }else {
                    likeSong.setImageResource(R.mipmap.nolike);
                }
                mediaPlayer.reset();
                proxyUrl = proxy.getProxyUrl(getValue(AudioUrlList,SongAdapter.keyVaule));
                mediaPlayer.setDataSource(proxyUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.start();
            }else if(SongAdapter.keyVaule == 0){
                if( PlayListActivity.likeOrNot.get(SongAdapter.keyVaule)){
                    likeSong.setImageResource(R.mipmap.yeslike);
                }
                mediaPlayer.reset();
                proxyUrl = proxy.getProxyUrl(getValue(AudioUrlList,SongAdapter.keyVaule));
                mediaPlayer.setDataSource(proxyUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getCurrentProgress(){
        int currentProgress;
        if(playerBar==null){
            currentProgress=-1;
        }else{
            currentProgress=playerBar.getProgress();
        }
        return currentProgress;
    }

    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        Log.e("MainActivity", "onConfigurationChanged");
        pause();
        phonograph.setPlaying(true);
    }

    public void pause(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
            playerView.setImageResource(R.mipmap.pause);
        }
        savePlayPosition(getValue(AudioUrlList,SongAdapter.keyVaule),getCurrentProgress());
    }

    public void seekTo(int position){
        if(mediaPlayer!=null){
            mediaPlayer.seekTo(position);
        }
    }

    public boolean isPlaying(){
        if(mediaPlayer == null){
            checkPlayingState = false;
        }else{
            checkPlayingState = mediaPlayer.isPlaying();
        }
        return checkPlayingState;
    }

    public void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            playerView.setImageResource(R.mipmap.pause);
            mediaPlayer=null;

        }
    }
    // MediaPlayer 现有函数的简单重写--------------end

    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public int getSavedPlayPosition(String url){
        if(AudioPositionMap.containsKey(url)){
            return AudioPositionMap.get(url);
        }
        return 0;
    }

    public void savePlayPosition(String audioUrl, int currentProgress) {

        //用hashMap来存储一对URL和其对应的播放进度,形成对应关系

        if(currentProgress<0){
            return;
        }
        AudioPositionMap.put(audioUrl,currentProgress);
    }

    public String timeFormat(int milli){


        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return (mm+":"+ss) ;
    }


//  播放/暂停点击事件的切换
    public void onClick(View v){

        int progress=getCurrentProgress();

        if(progress < 100){

            if(SongAdapter.keyVaule != null){
                if(isPlaying()){
                    pause();
                    phonograph.setPlaying(checkPlayingState);
                    playerView.setImageResource(R.mipmap.pause);
                }else{
                    if(NetworkStatesReceiver.ifSelectedCancel){
                        NetworkStatesReceiver.setValueOfDialog("网络提示","当前无wifi连接，是否使用移动数据继续播放？",NetworkStatesReceiver.ifSelectedCancel);
                    }else{
                        phonograph.setPlaying(checkPlayingState);
                        playerView.setImageResource(R.mipmap.playing);
                        mediaPlayer.start();
                    }

                }
            }else{
                Toast.makeText(getApplicationContext(),"您当前没有选中歌曲",Toast.LENGTH_SHORT).show();
            }
        }else{
            stop();
            Log.d("TestPause","Pause");
            playerView.setImageResource(R.mipmap.pause);
        }
    }


    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            try{
                if(mediaPlayer == null){
                    return;
                }else if(mediaPlayer.isPlaying() && playerBar.isPressed() == false){
                    handleProgress.sendEmptyMessage(0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };



    Handler handleProgress = new Handler(){
        public void handleMessage(Message msg){
            if(mediaPlayer != null){
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                if(duration>0){
                    long pos = (playerBar.getMax() * (long) position) / duration;
                    playerBar.setProgress((int) pos);

                    String cTime = timeFormat(position);

                    String tTime = timeFormat(duration);
                    if(position == duration-1000){
                        Log.d("TestTime","get");
                        playerView.setImageResource(R.mipmap.pause);
                    }
                    totalTime.setText(tTime);
                    curretTime.setText(cTime);
                }
            }
        }
    };


    class seekBarChanged implements SeekBar.OnSeekBarChangeListener{

        public int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int fprogress, boolean b) {
            if(mediaPlayer == null){
                return;
            }
            this.progress= fprogress * mediaPlayer.getDuration() / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(progress);

        }
    }

    class toPlayList implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            startActivity(intent);
        }
    };

    class toLastSong implements View.OnClickListener{
        @Override
        public void onClick(View v){
            if(SongAdapter.keyVaule != null && SongAdapter.keyVaule != 0){
                SongAdapter.keyVaule--;
                playerView.setImageResource(R.mipmap.pause);
                playerBar.setProgress(0);
                String cTime = timeFormat(0);
                curretTime.setText(cTime);
                phonograph.setPlaying(true);
                phonograph.setPicture(SongAdapter.keyVaule);
                initMediaPlayer();
            }else if(SongAdapter.keyVaule != null && SongAdapter.keyVaule == 0){
                Toast.makeText(getApplicationContext(),"当前歌曲已为第一首",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"您还没有选中歌曲",Toast.LENGTH_SHORT).show();
            }
        }
    };

    class toNextSong implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(SongAdapter.keyVaule != null && (SongAdapter.keyVaule + 1) < PlayListActivity.itemCount){
                Log.d("testNumber", PlayListActivity.itemCount+"");
                SongAdapter.keyVaule++;
                playerView.setImageResource(R.mipmap.pause);
                playerBar.setProgress(0);
                String cTime = timeFormat(0);
                curretTime.setText(cTime);
                phonograph.setPlaying(true);
                phonograph.setPicture(SongAdapter.keyVaule);
                initMediaPlayer();
            }else if(SongAdapter.keyVaule != null && (SongAdapter.keyVaule + 1) == PlayListActivity.itemCount){
                Toast.makeText(getApplicationContext(),"当前歌曲已为最后一首",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"您还没有选中歌曲",Toast.LENGTH_SHORT).show();
            }
        }
    };



    class toLikeSong implements View.OnClickListener{
        @Override
        public void onClick(View view){
            if(SongAdapter.keyVaule != null){
                likeList.setLikeing(!likeList.getLikeing());
                if(likeList.getLikeing()){
                    likeSong.setImageResource(R.mipmap.yeslike);
                    PlayListActivity.likeOrNot.set(SongAdapter.keyVaule,true);
                }else {
                    likeSong.setImageResource(R.mipmap.nolike);
                }
            }else {
                Toast.makeText(getApplicationContext(),"您当前没有选中歌曲",Toast.LENGTH_SHORT).show();
            }

        }
    }


//  以下两个函数为网络连接判断

//    注册
    @Override
    protected  void onResume(){
        if(networkStatesReceiver == null){
            networkStatesReceiver = new NetworkStatesReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStatesReceiver,filter);
        Log.d(TAG,"注册");
//        重写方法需要这样调用
        super.onResume();
    }

//    注销
    @Override
    protected void onPause(){
        unregisterReceiver(networkStatesReceiver);
        Log.d(TAG,"注销");
        super.onPause();
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
                Intent intent = new Intent(MainActivity.this, WXEntryActivity.class);
                startActivity(intent);
                Log.d("testOpen","WX");
                break;

            default:
        }
        return true;
    }

    class toSelectedNav implements NavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.nav_one:
//                        openActivity(MainActivity.class);
                    Log.d("testOpen","main");
                    break;
                case R.id.nav_two:
                    Intent intent = new Intent(MainActivity.this, WishListActivity.class);
                    startActivity(intent);
                    Log.d("testOpen","Wish");
                    break;
                default:
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    private HttpProxyCacheServer getProxy() {
        return App.getProxy(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_signin);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        playerView=findViewById(R.id.player_play_image);
        playerBar= findViewById(R.id.player_seekbar);
        playerList =  findViewById(R.id.player_list);
        curretTime = findViewById(R.id.curret_time);
        totalTime = findViewById(R.id.total_time);
        songInfo = findViewById(R.id.song_info);
        lastSong = findViewById(R.id.last_song);
        nextSong = findViewById(R.id.next_song);
        likeSong = findViewById(R.id.like_dec);
        navigationView = findViewById(R.id.nav_view);
        phonograph = findViewById(R.id.phonograph_view);

        playerView.setOnClickListener(MainActivity.this);
        playerList.setOnClickListener(new toPlayList());
        lastSong.setOnClickListener(new toLastSong());
        nextSong.setOnClickListener(new toNextSong());
        likeSong.setOnClickListener(new toLikeSong());
        playerBar.setOnSeekBarChangeListener(new seekBarChanged());
        navigationView.setCheckedItem(R.id.nav_one);
        navigationView.setNavigationItemSelectedListener(new toSelectedNav());

        if(proxy == null){
            proxy = new HttpProxyCacheServer(getApplicationContext());
        }

        if(SongAdapter.keyVaule != null){
            proxyUrl = proxy.getProxyUrl(getValue(AudioUrlList,SongAdapter.keyVaule));

        }else{
            proxyUrl = null;
        }

        initMediaPlayer();

        builder=new AlertDialog.Builder(this);

        //timer.schedule(task, firstTime, period);
        // firstTime为Date类型,period为long
        // 从firstTime时刻开始，每隔period毫秒执行一次。
        timer.schedule(timerTask,0,1000);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("tag", "播放完毕");
                playerView.setImageResource(R.mipmap.pause);
                phonograph.setPlaying(true);
            }
        });

    }
    private <T extends FragmentActivity> void openActivity(Class<T> activity){
        startActivity(new Intent(this, activity));
    }
}
