package com.youtubeseotools.youtubeseotools.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.youtubeseotools.youtubeseotools.Services.thumbnailService;

@Controller
public class thumbnailController
{
  @Autowired
  thumbnailService thumbnailService;  


  @GetMapping({"/thumbnail"})  
  public String thumbnail()
  {
    return "thumbnails";
  }
  
  @PostMapping("/get-thumbnail")
  public String showThumbnail(@RequestParam("videoUrlOrId") String videoUrlId , Model model)
  {
    String videoId = thumbnailService.extractVideoID(videoUrlId);
    if(videoId == null)
    {
        model.addAttribute( "error", "Invalid YouTube URL or Video ID");
        return "thumbnails";
    }
    String thumbnailUrl1 = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
    model.addAttribute("thumbnailUrl", thumbnailUrl1);
    return "thumbnails";
  }
}
