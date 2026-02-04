package org.mahedi.photoappalbums.mapper;

import org.mahedi.photoappalbums.dto.AlbumResponseDto;
import org.mahedi.photoappalbums.entity.AlbumEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {
    private final ModelMapper modelMapper;

    public AlbumMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public AlbumResponseDto toAlbumResponseDto(AlbumEntity album) {
        return modelMapper.map(album, AlbumResponseDto.class);
    }
}
