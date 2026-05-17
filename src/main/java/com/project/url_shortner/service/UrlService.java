package com.project.url_shortner.service;

import com.project.url_shortner.entity.UrlInfo;
import com.project.url_shortner.repository.UrlInfoRepo;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;

@Service
public class UrlService {

    private final UrlInfoRepo urlInfoRepo;

    public UrlService(UrlInfoRepo urlInfoRepo) {
        this.urlInfoRepo = urlInfoRepo;
    }

    public String createShortUrl(String url){

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        // https://gitlab.com/presto
        String tiny = urlEncode(url);
        // tiny.com/abcdefgh

        UrlInfo ui = new UrlInfo();
        ui.setShortUrl(tiny);
        ui.setUrl(url);
        String key = hashFunc(url);
        ui.setKey(key);
        urlInfoRepo.save(ui);

        return tiny;
    }

    private String urlEncode(String url) {
        return "tiny.com/" + hashFunc(url);
    }
    private String hashFunc(String url) {
        //return all string in 8 chars
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(url.getBytes());
            // Convert to Base64 and take first 8 chars
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            return encoded.substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public String getFullUrl(String surl){
        // tiny.com/abcdefgh

//        String key = surl.substring(9);
        // check in db
        UrlInfo entity = urlInfoRepo.findByKey(surl)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        //return string url
        //https://gitlab.com/presto
        return entity.getUrl();

    }


}
