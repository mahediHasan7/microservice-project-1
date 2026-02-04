package org.mahedi.photoappalbums.controller;

import lombok.RequiredArgsConstructor;
import org.mahedi.photoappalbums.dto.AlbumResponseDto;
import org.mahedi.photoappalbums.service.AlbumsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumsController {
    private final AlbumsService albumsService;

    @GetMapping("/users/{userId}")
    public List<AlbumResponseDto> userAlbums(@PathVariable String userId) {
        List<AlbumResponseDto> albums = albumsService.getAlbums(userId);
        if (albums == null || albums.isEmpty()) {
            return Collections.emptyList();
        }
        return albums;
    }
}
