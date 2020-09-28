package com.example.y84104146.mediaplayer;

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

public class WishListActivity extends AppCompatActivity {

    private List<Song> wishLists = new ArrayList<>();
    public HashMap<Integer,String> wishSong = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWishList();
        SongAdapter adapter = new SongAdapter(WishListActivity.this, R.layout.song_item, wishLists);
        ListView listView = findViewById(R.id.wishlist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = wishLists.get(position);
                MainActivity.mediaPlayer.stop();
                keyVaule = wishLists.get(position).getSongId();
                Intent intent = new Intent(WishListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initWishList(){

        for(int i = 0; i< PlayListActivity.itemCount; i++){
            if(PlayListActivity.likeOrNot.get(i)){
                Song song = new Song(PlayListActivity.info.get(i),"",i, PlayListActivity.AlbumPic.get(i),true);
                wishLists.add(song);
            }
        }

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
                Intent intent = new Intent(WishListActivity.this, WXEntryActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return true;
    }


}
