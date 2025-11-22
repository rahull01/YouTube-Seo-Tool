package com.youtubeseotools.youtubeseotools.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class pageController
{
    @GetMapping({"/","/home"})
    public String home()
    {
        return "home";
    }

    @GetMapping("/video-details")
    public String videoDetails()
    {
        return "video-details";
    }

    @GetMapping("/seo")
    public String seoPage()
    {
        return "seo";  // your seo.html page
    }
}
