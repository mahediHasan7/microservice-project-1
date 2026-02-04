package org.mahedi.photoappapiuser.client;


import org.mahedi.photoappapiuser.dto.AlbumResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "albums-ws")
public interface AlbumServiceClient {
    @GetMapping("/albums/users/{userId}")
    List<AlbumResponseDto> getAlbumsByUserId(@PathVariable("userId") Long userId);
}
