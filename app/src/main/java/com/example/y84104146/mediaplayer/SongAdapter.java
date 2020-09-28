package com.example.y84104146.mediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends ArrayAdapter <Song> {
    private int resourceId;
    public static Integer keyVaule;
    public static Integer maxKeyVaule = 0;


    public SongAdapter(Context context, int textViewResourceId, List<Song> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Song song = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        TextView songName = view.findViewById(R.id.song_name);
        songName.setText(song.getSongName());
        TextView singerName = view.findViewById(R.id.singer_name);
        singerName.setText(song.getSongSinger());
//        MainActivity.adapter_keyVaule =;
        keyVaule = song.getSongId();
        return view;
    }

}
