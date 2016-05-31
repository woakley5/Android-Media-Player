package com.example.willoakley.mediaplayer;

import android.media.Image;

/**
 * Created by WillOakley on 5/16/16.
 */
public class Song {
    private long id;
    private String title;
    private long albumArtworkId;
    private String artist;

    public Song(long i, String name,  String band)
    {
        id = i;
        title = name;
        //albumArtworkId = artwork;
        artist = band;
    }

    public long getID()
    {
        return id;
    }

    public String getName()
    {
        return title;
    }

    public long getAlbumArtwork() { return albumArtworkId; }

    public String getArtist() { return artist; }


}
