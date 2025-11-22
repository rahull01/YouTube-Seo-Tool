package com.youtubeseotools.youtubeseotools.Model;

import java.util.List;

public class searchVideo {

    private video primaryVideos;
    private List<video> relatedVideos;

    public searchVideo() {
    }

    public searchVideo(video primaryVideos, List<video> relatedVideos) {
        this.primaryVideos = primaryVideos;
        this.relatedVideos = relatedVideos;
    }

    public video getPrimaryVideos() {
        return primaryVideos;
    }

    public void setPrimaryVideos(video primaryVideos) {
        this.primaryVideos = primaryVideos;
    }

    public List<video> getRelatedVideos() {
        return relatedVideos;
    }

    public void setRelatedVideos(List<video> relatedVideos) {
        this.relatedVideos = relatedVideos;
    }
}
