package org.mahedi.photoappalbums.service;

import org.mahedi.photoappalbums.dto.AlbumResponseDto;

import java.util.List;

public interface AlbumsService {
    List<AlbumResponseDto> getAlbums(String userId);
}
