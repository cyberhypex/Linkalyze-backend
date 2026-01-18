package com.Link.Linkalyz.controllers;

import com.Link.Linkalyz.dtos.ClickEventDTO;
import com.Link.Linkalyz.dtos.UrlMappingDTO;
import com.Link.Linkalyz.models.User;
import com.Link.Linkalyz.service.UrlMappingService;
import com.Link.Linkalyz.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(
            @PathVariable String shortUrl,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();

        List<ClickEventDTO> clickEventDTOS =
                urlMappingService.getClickEventsByDate(
                        shortUrl,
                        startDateTime,
                        endDateTime
                );

        return ResponseEntity.ok(clickEventDTOS);
    }

    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate,Long>> getTotalClicksByDate(Principal principal,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate
                                                                    ){
        DateTimeFormatter formatter=DateTimeFormatter.ISO_LOCAL_DATE;
        User user=userService.findByUsername(principal.getName());
        LocalDate start=LocalDate.parse(startDate,formatter);
        LocalDate end=LocalDate.parse(endDate,formatter);
        Map<LocalDate,Long> totalClicks= urlMappingService.getTotalClicksByUserAndDate(user,start,end);
        return ResponseEntity.ok(totalClicks);
    }

}
