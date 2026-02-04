package org.mahedi.photoappalbums.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponseDto {
    private String albumId;
    private String userId;
    private String name;
    private String description;
}
