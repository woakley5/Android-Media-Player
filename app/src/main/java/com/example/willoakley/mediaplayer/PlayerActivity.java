package com.example.willoakley.mediaplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class PlayerActivity extends AppCompatActivity{

    private ImageButton playButton;
    private ImageButton backButton;
    private ImageButton nextButton;
    private TextView timeElapsedLabel;
    private TextView timeTotalLabel;
    private TextView songTitleLabel;
    private TextView artistNameLabel;
    private SeekBar seekBar;
    private Timer updater;
    private ArrayList<Song> songs;
    private int currentlyPlaying;
    private ImageView artworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songs = ListActivity.getSongList();
        currentlyPlaying = ListActivity.getClickedRow();

        playButton = (ImageButton)findViewById(R.id.playButton);
        backButton = (ImageButton)findViewById(R.id.backButton);
        nextButton = (ImageButton)findViewById(R.id.nextButton);
        songTitleLabel = (TextView)findViewById(R.id.songTitleLabel);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        timeElapsedLabel = (TextView)findViewById(R.id.timeElapsedLabel);
        artworkView = (ImageView)findViewById(R.id.artworkView);
        artistNameLabel = (TextView)findViewById(R.id.artistLabel);
        timeTotalLabel = (TextView)findViewById(R.id.songLengthLabel);
        playButton.setImageResource(+R.raw.play);
        backButton.setImageResource(+R.raw.back);
        nextButton.setImageResource(+R.raw.forward);
        //soundPlayer = ListActivity.getMediaPlayer();
        songTitleLabel.setText(songs.get(currentlyPlaying).getName());
        setTitle(songs.get(currentlyPlaying).getName());
        artistNameLabel.setText(songs.get(currentlyPlaying).getArtist());
        artworkView.setImageResource((int)songs.get(currentlyPlaying).getAlbumArtwork());
        //seekBar.setMax(soundPlayer.getDuration());
        seekBar.setMax(ListActivity.getPlayerDuration());

        //soundPlayer.start();
        ListActivity.startPlayer();


        if(currentlyPlaying == songs.size() -1)
        {
            nextButton.setEnabled(false);
        }
        else{
            nextButton.setEnabled(true);
        }
        seekBar.setProgress(0);
        updater = new Timer();

        updater.schedule(new TimerTask() { //timer to update seekbar
            @Override
            public void run() {
                updateSeekBar();
            }

        }, 0, 50);

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!ListActivity.getIsPlaying()) {
                    //soundPlayer.start();
                    ListActivity.startPlayer();
                } else {
                    //soundPlayer.pause();
                    ListActivity.pausePlayer();
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(ListActivity.getPlayerCurrentPosition() < 5000) {
                    if(currentlyPlaying == 0)
                    {
                        //soundPlayer.seekTo(0);
                        ListActivity.playerSeekTo(0);
                    }
                    else {
                        System.out.println("moving back song");
                        backSong();
                    }

                }
                else
                {
                    System.out.println("Moving to beginning of song");
                    //soundPlayer.seekTo(0);
                    ListActivity.playerSeekTo(0);
                }
            }
        }); //back button resets song

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(currentlyPlaying < songs.size()) {
                    nextSong();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //seek bar interface methods

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // soundPlayer.seekTo(seekBar.getProgress());
                ListActivity.playerSeekTo(seekBar.getProgress());

            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            updater.cancel();
            //soundPlayer.reset();
            System.out.println("Ending Activity");
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateSeekBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //int currentPosition = soundPlayer.getCurrentPosition();
                int currentPosition = ListActivity.getPlayerCurrentPosition();
                if (ListActivity.getIsPlaying()) {
                    playButton.setImageResource(+R.raw.pause);
                } else {
                    playButton.setImageResource(+R.raw.play);
                }
                seekBar.setProgress(currentPosition);
                timeElapsedLabel.setText(String.format("%01d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(currentPosition) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentPosition)),
                        TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))));
                timeTotalLabel.setText(String.format("%01d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(ListActivity.getPlayerDuration()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ListActivity.getPlayerDuration())),
                        TimeUnit.MILLISECONDS.toSeconds(ListActivity.getPlayerDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ListActivity.getPlayerDuration()))));
            }
        });
    }

    public void nextSong()
    {
        currentlyPlaying++;
        if(currentlyPlaying == songs.size() -1)
        {
            nextButton.setEnabled(false);
        }
        //soundPlayer.reset();
        ListActivity.resetPlayer();
        //soundPlayer = MediaPlayer.create(this, songs.get(currentlyPlaying).getID());
        ListActivity.changeSong(currentlyPlaying, this);
        songTitleLabel.setText(songs.get(currentlyPlaying).getName());
        setTitle(songs.get(currentlyPlaying).getName());
        artistNameLabel.setText(songs.get(currentlyPlaying).getArtist());
        artworkView.setImageResource((int)songs.get(currentlyPlaying).getAlbumArtwork());
        //seekBar.setMax(soundPlayer.getDuration());
        seekBar.setMax(ListActivity.getPlayerDuration());
        //soundPlayer.start();
        ListActivity.startPlayer();
    }

    public void backSong()
    {
        currentlyPlaying--;
        if(currentlyPlaying != songs.size() -1)
        {
            nextButton.setEnabled(true);
        }
        //soundPlayer.reset();
        ListActivity.resetPlayer();
        //soundPlayer = MediaPlayer.create(this, songs.get(currentlyPlaying).getID());
        ListActivity.changeSong(currentlyPlaying, this);
        songTitleLabel.setText(songs.get(currentlyPlaying).getName());
        setTitle(songs.get(currentlyPlaying).getName());
        artistNameLabel.setText(songs.get(currentlyPlaying).getArtist());
        artworkView.setImageResource((int)songs.get(currentlyPlaying).getAlbumArtwork());
        //seekBar.setMax(soundPlayer.getDuration());
        seekBar.setMax(ListActivity.getPlayerDuration());
        //soundPlayer.start();
        ListActivity.startPlayer();
    }









}
