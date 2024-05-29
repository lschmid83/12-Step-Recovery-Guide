package com.citex.twelve_step_recovery.ui.audio;

import java.io.Serializable;

/**
 * Original example taken from Valdio Veliu on 16-07-11.
 * https://github.com/sitepoint-editors/AudioPlayer
 */
public class Audio implements Serializable {

    private String artist;
    private String album;
    private String title;
    private String image;
    private String data;
    private long duration;


    public Audio() { }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}