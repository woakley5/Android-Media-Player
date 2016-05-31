package com.example.willoakley.mediaplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WillOakley on 5/24/16.
 */
public class ListActivity extends AppCompatActivity {

    private ListView songListView;
    private static ArrayList<Song> songs;
    private static int selectedRow;
    private static MediaPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle("Your Songs");
        songListView = (ListView) findViewById(R.id.songListView);

        songs = new ArrayList<Song>();

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songs.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        /*songs.add(new Song(R.raw.song1, "Are You What You Want To Be?", R.raw.artwork1, "Foster the People"));
        songs.add(new Song(R.raw.song2, "Helena Beat", R.raw.artwork2, "Foster the People"));
        songs.add(new Song(R.raw.song3, "Dreams", R.raw.artwork3, "Beck"));*/


        ArrayList<String> songTitles = new ArrayList<String>();

        for (Song s : songs) {
            songTitles.add(s.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songTitles);
        songListView.setAdapter(arrayAdapter);

        final Intent newActivity = new Intent(this, PlayerActivity.class);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRow = position;
                if(soundPlayer != null)
                {
                    soundPlayer.reset();
                }
                soundPlayer = MediaPlayer.create();
                startActivity(newActivity);
            }
        });


    }

    public static ArrayList<Song> getSongList()
    {
        return songs;
    }

    public static int getClickedRow() {return selectedRow;}

    public static MediaPlayer getMediaPlayer()
    {
        return soundPlayer;
    }

    public static int getPlayerDuration()
    {
        return soundPlayer.getDuration();
    }

    public static void startPlayer()
    {
        soundPlayer.start();
    }

    public static void pausePlayer()
    {
        soundPlayer.pause();
    }

    public static void resetPlayer()
    {
        soundPlayer.reset();
    }

    public static boolean getIsPlaying()
    {
        return soundPlayer.isPlaying();
    }

    public static int getPlayerCurrentPosition()
    {
        return soundPlayer.getCurrentPosition();
    }

    public static void playerSeekTo(int i)
    {
        soundPlayer.seekTo(i);
    }

    public static void changeSong(int i, Context c) {
       // soundPlayer = MediaPlayer.create(c, (int)songs.get(i).getID());
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songs.get(i).getID());
        try {
            soundPlayer.setDataSource(c, trackUri);
        } catch (IOException e) {
            System.out.println("err");

        }
    }
}

