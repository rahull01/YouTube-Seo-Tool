package com.youtubeseotools.youtubeseotools.Controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.youtubeseotools.youtubeseotools.Model.searchVideo;
import com.youtubeseotools.youtubeseotools.Model.video;
import com.youtubeseotools.youtubeseotools.Services.thumbnailService;
import com.youtubeseotools.youtubeseotools.Services.youtubeservice;

@Controller
@RequestMapping("/youtube")
public class YoutubeTagsController {

    @Autowired
    private youtubeservice youTubeService;

    @Autowired
    private thumbnailService thumbnailService;

    @Value("${youtube.api.key}")
    private String apiKey;

    private boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    // -------------------------------------
    // 1️⃣ SEO Tag Generator (home.html)
    // -------------------------------------
   @PostMapping("/search")
public String videoTags(@RequestParam("videoTitle") String videoTitle, Model model) {

    if (!isApiKeyConfigured()) {
        model.addAttribute("error", "API key is not configured");
        return "home";
    }

    if (videoTitle == null || videoTitle.trim().isEmpty()) {
        model.addAttribute("error", "Video Title is required");
        return "home";
    }

    try {
        searchVideo result = youTubeService.searchVideo(videoTitle);

        video primary = result.getPrimaryVideos();

        // ✅ Yaha check karo: agar primary hi null hai to clear message
        if (primary == null) {
            model.addAttribute("error", "No videos found for this title. Try a different or more specific title.");
            return "home";
        }

        model.addAttribute("primaryVideo", primary);
        model.addAttribute("relatedVideos", result.getRelatedVideos());

        if (primary.getTags() != null) {
            String primaryTagsStr = String.join(", ", primary.getTags());
            primary.setTagsAsString(primaryTagsStr);
           
        }

        if (result.getRelatedVideos() != null) {
            String allTags = result.getRelatedVideos().stream()
                    .filter(v -> v.getTags() != null)
                    .flatMap(v -> v.getTags().stream())
                    .collect(Collectors.joining(", "));

            model.addAttribute("allTagsAsString", allTags);
        }

        return "home";

    } catch (Exception e) {
        model.addAttribute("error", "Error: " + e.getMessage());
        return "home";
    }
}


 
    @PostMapping("/video-details")
    public String videoDetails(@RequestParam("videoUrlOrId") String videoUrlOrId, Model model) {

        if (!isApiKeyConfigured()) {
            model.addAttribute("error", "API key is not configured");
            return "video-details";
        }

        if (videoUrlOrId == null || videoUrlOrId.trim().isEmpty()) {
            model.addAttribute("error", "Video URL or ID is required");
            return "video-details";
        }

        String videoId = thumbnailService.extractVideoID(videoUrlOrId);
        if (videoId == null) {
            model.addAttribute("error", "Invalid YouTube URL or Video ID");
            return "video-details";
        }

        try {
            video details = youTubeService.getVideoDetailsById(videoId);

            if (details == null) {
                model.addAttribute("error", "Video not found or API error");
                return "video-details";
            }

            model.addAttribute("videoUrlOrId", videoUrlOrId);
            model.addAttribute("videoDetails", details);

            return "video-details";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "video-details";
        }
    }
}
