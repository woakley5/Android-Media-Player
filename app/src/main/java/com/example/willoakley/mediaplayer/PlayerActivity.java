package com.example.willoakley.mediaplayer;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    VisualizerView mVisualizerView;
    private Visualizer mVisualizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songs = ListActivity.getSongList();
        currentlyPlaying = ListActivity.getClickedRow();
        mVisualizerView = (VisualizerView) findViewById(R.id.myVisualizerView);
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
        songTitleLabel.setText(songs.get(currentlyPlaying).getName());
        setTitle(songs.get(currentlyPlaying).getName());
        artistNameLabel.setText(songs.get(currentlyPlaying).getArtist());
        artworkView.setImageResource((int)songs.get(currentlyPlaying).getAlbumArtwork());
        seekBar.setMax(ListActivity.getPlayerDuration());
        ActionBar bar = getSupportActionBar();
        int color = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        int darkerColor = Color.HSVToColor(hsv);
        ColorDrawable colorDrawable = new  ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        getWindow().setStatusBarColor(darkerColor);

        setupVisualizerFxAndUI();
        mVisualizer.setEnabled(true);

        ListActivity.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });
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
                    ListActivity.startPlayer();
                } else {
                    ListActivity.pausePlayer();
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(ListActivity.getPlayerCurrentPosition() < 2000) {
                    if(currentlyPlaying == 0)
                    {
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
                    ListActivity.playerSeekTo(0);
                }
            }
        });

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
                ListActivity.playerSeekTo(seekBar.getProgress());

            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            updater.cancel();

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && ListActivity.getMediaPlayer() != null) {
            mVisualizer.release();
            ListActivity.getMediaPlayer().release();
            //ListActivity.getMediaPlayer() = null;
        }
    }

    public void updateSeekBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = ListActivity.getPlayerCurrentPosition();
                if (currentPosition >= ListActivity.getPlayerDuration() - 100) {
                    nextSong();
                    System.out.println("Next Song Running");
                }
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
            nextButton.setVisibility(View.INVISIBLE);
        }
        ListActivity.resetPlayer();
        ListActivity.changeSong(currentlyPlaying, this);
        songTitleLabel.setText(songs.get(currentlyPlaying).getName());
        setTitle(songs.get(currentlyPlaying).getName());
        artistNameLabel.setText(songs.get(currentlyPlaying).getArtist());
        artworkView.setImageResource((int) songs.get(currentlyPlaying).getAlbumArtwork());
        seekBar.setMax(ListActivity.getPlayerDuration());
        ActionBar bar = getSupportActionBar();
        int color = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        int darkerColor = Color.HSVToColor(hsv);
        ColorDrawable colorDrawable = new  ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        getWindow().setStatusBarColor(darkerColor);
        setupVisualizerFxAndUI();
        mVisualizer.setEnabled(true);

        ListActivity.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });
        ListActivity.startPlayer();
    }

    public void backSong()
    {
        currentlyPlaying--;
        if(currentlyPlaying != songs.size() -1)
        {
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        }
        ListActivity.resetPlayer();
        ListActivity.changeSong(currentlyPlaying, this);
        songTitleLabel.setText(songs.get(currentlyPlaying).getName());
        setTitle(songs.get(currentlyPlaying).getName());
        artistNameLabel.setText(songs.get(currentlyPlaying).getArtist());
        artworkView.setImageResource((int)songs.get(currentlyPlaying).getAlbumArtwork());
        seekBar.setMax(ListActivity.getPlayerDuration());
        ActionBar bar = getSupportActionBar();
        int color = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        int darkerColor = Color.HSVToColor(hsv);
        ColorDrawable colorDrawable = new  ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(darkerColor, PorterDuff.Mode.MULTIPLY));
        seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        getWindow().setStatusBarColor(darkerColor);
        setupVisualizerFxAndUI();
        mVisualizer.setEnabled(true);

        ListActivity.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });
        ListActivity.startPlayer();
    }

    private void setupVisualizerFxAndUI() {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(ListActivity.getMediaPlayer().getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }










}
