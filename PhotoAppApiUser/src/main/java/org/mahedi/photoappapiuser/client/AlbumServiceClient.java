package org.mahedi.photoappapiuser.client;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.mahedi.photoappapiuser.dto.AlbumResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "albums-ws")
public interface AlbumServiceClient {
    @GetMapping("/albums/users/{userId}")
    // Resilience4j
    @Retry(name = "albums-ws")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsByUserIdFallback")
    List<AlbumResponseDto> getAlbumsByUserId(@PathVariable("userId") Long userId);

    // we need to provide a default implementation for the abstract method which will work as the fallback method
    // The default method need to follow the correct signature and as parameter include the Throwable for the exception)
    default List<AlbumResponseDto> getAlbumsByUserIdFallback(Long userId, Throwable exception) {
        System.out.println("Param = " + userId);
        System.out.println("Exception took place: " + exception.getLocalizedMessage());
        return new ArrayList<>();
    }
}
