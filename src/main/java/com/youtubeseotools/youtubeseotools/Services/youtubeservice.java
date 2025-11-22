package com.youtubeseotools.youtubeseotools.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.youtubeseotools.youtubeseotools.Model.searchVideo;
import com.youtubeseotools.youtubeseotools.Model.video;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class youtubeservice {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${youtube.api.key}")
    private String apiKey;

    // sirf host name (www.googleapis.com)
    @Value("${youtube.api.base.url}")
    private String youtubeBaseUrl;

    // kitne related videos chahiye
    @Value("${youtube.api.max}")
    private int maxRelatedValue;

    // ------------------------------------------------
    // 1) SEO Tag Generator
    // ------------------------------------------------
    public searchVideo searchVideo(String videoTitle) {

        List<String> videoIds = searchForVideoIds(videoTitle);

        if (videoIds.isEmpty()) {
            return new searchVideo(null, Collections.emptyList());
        }

        String primaryVideoId = videoIds.get(0);

        List<String> relatedVideoIds = videoIds.subList(
                1,
                Math.min(videoIds.size(), maxRelatedValue + 1)
        );

        video primaryVideo = getVideoById(primaryVideoId);

        List<video> relatedVideos = new ArrayList<>();
        for (String relatedVideoId : relatedVideoIds) {
            video relatedVideo = getVideoById(relatedVideoId);
            if (relatedVideo != null) {
                relatedVideos.add(relatedVideo);
            }
        }

        return new searchVideo(primaryVideo, relatedVideos);
    }

    // ------------------------------------------------
    // 2) Video Details Page
    // ------------------------------------------------
    public video getVideoDetailsById(String videoId) {
        return getVideoById(videoId);
    }

    // ------------------------------------------------
    // PRIVATE: Search API → get video IDs
    // ------------------------------------------------
    private List<String> searchForVideoIds(String videoTitle) {

        SearchResponse response = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(youtubeBaseUrl)
                        .path("/youtube/v3/search")
                        .queryParam("part", "snippet")
                        .queryParam("q", videoTitle)
                        .queryParam("type", "video")
                        .queryParam("maxResults", maxRelatedValue)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .block();

        if (response == null || response.items == null) {
            return Collections.emptyList();
        }

        List<String> videoIds = new ArrayList<>();

        for (SearchResultItem item : response.items) {
            if (item != null && item.id != null && item.id.videoId != null) {
                videoIds.add(item.id.videoId);
            }
        }

        return videoIds;
    }

    // ------------------------------------------------
    // PRIVATE: Videos API → full video details
    // ------------------------------------------------
    private video getVideoById(String videoId) {

        VideoDetailsResponse response = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(youtubeBaseUrl)
                        .path("/youtube/v3/videos")
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(VideoDetailsResponse.class)
                .block();

        if (response == null || response.items == null || response.items.isEmpty()) {
            return null;
        }

        VideoDetailItem item = response.items.get(0);
        Snippet snippet = item.snippet;

        video v = new video();
        v.setId(videoId);
        v.setTitle(snippet.title);
        v.setChannelTitle(snippet.channelTitle);

        if (snippet.tags != null) {
            v.setTags(Arrays.asList(snippet.tags));
        } else {
            v.setTags(Collections.emptyList());
        }

        String thumb = null;
        if (snippet.thumbnails != null && snippet.thumbnails.high != null) {
            thumb = snippet.thumbnails.high.url;
        }
        v.setThumbnailUrl(thumb);

        v.setDescription(snippet.description);
        v.setPublishedAt(snippet.publishedAt);

        return v;
    }

    // ------------------------------------------------
    // DTO CLASSES (JSON mapping)
    // ------------------------------------------------
    @Data
    static class SearchResponse {
        public List<SearchResultItem> items;
    }

    @Data
    static class SearchResultItem {
        public Id id;
    }

    @Data
    static class Id {
        public String videoId;
    }

    @Data
    static class VideoDetailsResponse {
        public List<VideoDetailItem> items;
    }

    @Data
    static class VideoDetailItem {
        public Snippet snippet;
    }

    @Data
    static class Snippet {
        public String title;
        public String channelTitle;
        public String description;
        public String publishedAt;
        public String[] tags;
        public Thumbnails thumbnails;
    }

    @Data
    static class Thumbnails {
        public Thumbnail high;
    }

    @Data
    static class Thumbnail {
        public String url;
    }
}
