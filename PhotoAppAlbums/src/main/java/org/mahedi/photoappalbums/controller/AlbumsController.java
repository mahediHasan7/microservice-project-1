package org.mahedi.photoappalbums.controller;

import org.mahedi.photoappalbums.dto.AlbumResponseDto;
import org.mahedi.photoappalbums.service.AlbumsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumsController {

    @Autowired
    AlbumsService albumsService;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/users/{userId}")
    public List<AlbumResponseDto> userAlbums(@PathVariable String userId) {
        List<AlbumResponseDto> albumsEntities = albumsService.getAlbums(userId);
        if (albumsEntities == null || albumsEntities.isEmpty()) {
            return Collections.<AlbumResponseDto>emptyList();
        }

        logger.info("Returning {} albums", albumsEntities.size());
        return albumsEntities;
    }
}
