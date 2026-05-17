package com.project.url_shortner.repository;

import com.project.url_shortner.entity.UrlInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlInfoRepo extends JpaRepository<UrlInfo,Long> {

    Optional<UrlInfo> findByShortUrl(String shortUrl);
    Optional<UrlInfo> findByKey(String key);
}
