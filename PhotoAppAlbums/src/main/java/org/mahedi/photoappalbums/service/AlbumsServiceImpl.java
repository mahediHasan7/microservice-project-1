package org.mahedi.photoappalbums.service;

import org.mahedi.photoappalbums.dto.AlbumResponseDto;
import org.mahedi.photoappalbums.entity.AlbumEntity;
import org.mahedi.photoappalbums.mapper.AlbumMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumsServiceImpl implements AlbumsService {
    private final AlbumMapper albumMapper;

    public AlbumsServiceImpl(AlbumMapper albumMapper) {
        this.albumMapper = albumMapper;
    }


    @Override
    public List<AlbumResponseDto> getAlbums(String userId) {
        List<AlbumEntity> returnValue = new ArrayList<>();

        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setUserId(userId);
        albumEntity.setAlbumId("album1Id");
        albumEntity.setDescription("album 1 description");
        albumEntity.setId(1L);
        albumEntity.setName("album 1 name");

        AlbumEntity albumEntity2 = new AlbumEntity();
        albumEntity2.setUserId(userId);
        albumEntity2.setAlbumId("album2Id");
        albumEntity2.setDescription("album 2 description");
        albumEntity2.setId(2L);
        albumEntity2.setName("album 2 name");

        returnValue.add(albumEntity);
        returnValue.add(albumEntity2);

        return returnValue.stream().map(album -> albumMapper.toAlbumResponseDto(album)).collect(Collectors.toList());
    }

}
