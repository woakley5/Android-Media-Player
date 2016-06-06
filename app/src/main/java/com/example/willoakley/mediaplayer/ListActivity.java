package com.example.willoakley.mediaplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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

        songs.add(new Song(R.raw.song1, "Are You What You Want To Be?", R.raw.artwork1, "Foster the People"));
        songs.add(new Song(R.raw.song3, "Dreams", R.raw.artwork3, "Beck"));
        songs.add(new Song(R.raw.song4, "Road Blaster", R.raw.artwork4, "M83"));
        songs.add(new Song(R.raw.song5, "Tearing Me Up", R.raw.artwork5, "Bob Moses"));
        songs.add(new Song(R.raw.song6, "Gold", R.raw.artwork6, "Chet Faker"));



        ArrayList<String> songTitles = new ArrayList<String>();

        for (Song s : songs) {
            songTitles.add(s.getName() + "   -   " + s.getArtist());
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
                soundPlayer = MediaPlayer.create(getApplicationContext(), (int)songs.get(selectedRow).getID());
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
        soundPlayer = MediaPlayer.create(c, (int)songs.get(i).getID());

    }
}

