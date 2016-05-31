package com.example.willoakley.mediaplayer;

import java.io.File;
import java.util.ArrayList;
import com.example.willoakley.mediaplayer.Song;

/**
 * Created by APCS1 on 5/17/2016.
 */
public class Library
{
    private ArrayList<Song> songs;

    public Library()
    {
        File file = new File("/storage/sdcard0/Music");
        for(int i = 0; i < file.list().length; i++)
        {
            File tempFile = new File("/storage/sdcard0/Music");
            tempFile.equals(file.listFiles()[i]);
            System.out.println(tempFile.canRead() + tempFile.getName().substring(0,tempFile.getName().indexOf(".")-1) + tempFile.toURI().toString());
            //songs.add(new Song(tempFile.canRead(), tempFile.getName().substring(0,tempFile.getName().indexOf(".")-1), tempFile.toURI()));
        }

    }
}
