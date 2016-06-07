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
import android.widget.SimpleAdapter;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        songs.add(0, new Song(R.raw.song3, "Dreams", R.raw.artwork3, "Beck"));
        songs.add(1, new Song(R.raw.song5, "Tearing Me Up", R.raw.artwork5, "Bob Moses"));
        songs.add(2, new Song(R.raw.song6, "Gold", R.raw.artwork6, "Chet Faker"));
        songs.add(3, new Song(R.raw.song14, "Starman", R.raw.artwork14, "David Bowie"));
        songs.add(4, new Song(R.raw.song7, "Latch", R.raw.artwork7, "Disclosure"));
        songs.add(5, new Song(R.raw.song13, "MoneyGrabber", R.raw.artwork13, "Fitz and the Tantrums"));
        songs.add(6, new Song(R.raw.song1, "Are You What You Want To Be?", R.raw.artwork1, "Foster the People"));
        songs.add(7, new Song(R.raw.song12, "All Along the Watch Tower", R.raw.artwork12, "Jimi Hendrix"));
        songs.add(8, new Song(R.raw.song11, "Hey Joe", R.raw.artwork11, "Jimi Hendrix"));
        songs.add(9, new Song(R.raw.song16, "When the Levee Breaks", R.raw.artwork16, "Led Zeppelin"));
        songs.add(10, new Song(R.raw.song4, "Road Blaster", R.raw.artwork4, "M83"));
        songs.add(11, new Song(R.raw.song15, "Another Brick in the Wall Pt. 2", R.raw.artwork15, "Pink Floyd"));
        songs.add(12, new Song(R.raw.song17, "Under Pressure", R.raw.artwork17, "Queen"));
        songs.add(13, new Song(R.raw.song10, "Sympathy for the Devil", R.raw.artwork10, "The Rolling Stones"));


        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (Song item : songs) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.getName());
            datum.put("artist", item.getArtist());
            data.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "artist"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        songListView.setAdapter(adapter);

        final Intent newActivity = new Intent(this, PlayerActivity.class);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRow = position;
                resetPlayer();
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

    public static int getPlayerDuration()
    {
        return soundPlayer.getDuration();
    }

    public static void startPlayer() {
        soundPlayer.start();
    }

    public static void pausePlayer()
    {
        soundPlayer.pause();
    }

    public static void resetPlayer()
    {
        if(soundPlayer != null)
        {
            soundPlayer.reset();
            System.out.println("Resetting Player");
        }

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

    public static MediaPlayer getMediaPlayer(){
        return soundPlayer;
    };
}

