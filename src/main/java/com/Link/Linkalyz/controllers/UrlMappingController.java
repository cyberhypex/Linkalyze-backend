package com.Link.Linkalyz.controllers;

import com.Link.Linkalyz.dtos.UrlMappingDTO;
import com.Link.Linkalyz.models.User;
import com.Link.Linkalyz.service.UrlMappingService;
import com.Link.Linkalyz.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {

    private UrlMappingService urlMappingService;
    private UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(@RequestBody Map<String,String> request,
                                                        Principal principal
                                                        ){
        String originalUrl=request.get("originalUrl");
      User user=  userService.findByUsername(principal.getName());
        //call service mthod
       UrlMappingDTO urlMappingDTO= urlMappingService.createShortUrl(originalUrl,user);
        return ResponseEntity.ok(urlMappingDTO);
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal){
        User user=userService.findByUsername(principal.getName());
        List<UrlMappingDTO> urls=urlMappingService.getUrlsByUser(user);
        return ResponseEntity.ok(urls);

    }
}
