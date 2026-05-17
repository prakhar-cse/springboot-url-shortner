package com.project.url_shortner.controller;

import com.project.url_shortner.dto.CreateShortUrlRequest;
import com.project.url_shortner.entity.UrlInfo;
import com.project.url_shortner.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlInfo> createShortUrl(@RequestBody CreateShortUrlRequest body) {

        String shortUrl = urlService.createShortUrl(body.getUrl());

        UrlInfo response = new UrlInfo();
        response.setUrl(body.getUrl());
        response.setShortUrl(shortUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sUrl}")
    public ResponseEntity<UrlInfo> getUrl(@PathVariable String sUrl) {
        String fullUrl = urlService.getFullUrl(sUrl);

        UrlInfo r = new UrlInfo();
        r.setUrl(fullUrl);
        r.setShortUrl(sUrl);
        return ResponseEntity.ok(r);
    }
}
