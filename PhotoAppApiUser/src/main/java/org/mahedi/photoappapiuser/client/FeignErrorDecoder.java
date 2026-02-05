package org.mahedi.photoappapiuser.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    // methodKey: it has the Album Controller's method name (in general, route's controller method name)
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else if (response.status() >= 500) {
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        } else {
            return new Exception(response.reason());
        }
    }
}
