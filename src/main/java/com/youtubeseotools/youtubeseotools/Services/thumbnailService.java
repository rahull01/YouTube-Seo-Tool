package com.youtubeseotools.youtubeseotools.Services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class thumbnailService {

    public String extractVideoID(String url) {

        // Agar already 11 char ka ID hai
        if (url != null && url.matches("^[a-zA-Z0-9_-]{11}$")) {
            return url;
        }

        String[] patterns = {
                "https?://(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]{11})",
                "https?://(?:www\\.)?youtu\\.be/([a-zA-Z0-9_-]{11})",
                "https?://(?:www\\.)?youtube\\.com/embed/([a-zA-Z0-9_-]{11})",
                "https?://(?:www\\.)?youtube\\.com/v/([a-zA-Z0-9_-]{11})"
        };

        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern).matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }
}
