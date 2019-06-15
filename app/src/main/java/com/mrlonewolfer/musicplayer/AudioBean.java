package com.mrlonewolfer.musicplayer;

import androidx.annotation.NonNull;

public class AudioBean {
    String aPath;
    String aName;
    String aAlbum;
    String aArtist;


    public AudioBean() {

    }

    public String getaPath() {
        return aPath;
    }

    public void setaPath(String aPath) {
        this.aPath = aPath;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaAlbum() {
        return aAlbum;
    }

    public void setaAlbum(String aAlbum) {
        this.aAlbum = aAlbum;
    }

    public String getaArtist() {
        return aArtist;
    }

    public void setaArtist(String aArtist) {
        this.aArtist = aArtist;
    }

    @Override
    public String toString() {
        return "AudioBean{" +
                "aPath='" + aPath + '\'' +
                ", aName='" + aName + '\'' +
                ", aAlbum='" + aAlbum + '\'' +
                ", aArtist='" + aArtist + '\'' +
                '}';
    }
}
