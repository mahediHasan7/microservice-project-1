package org.mahedi.photoappalbums.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AlbumEntity {
    private long id;
    private String albumId;
    private String userId;
    private String name;
    private String description;
}
