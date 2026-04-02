package com.goylik.order_service.client.fallback;

import com.goylik.order_service.client.UserServiceClient;
import com.goylik.order_service.exception.client.UserServiceUnavailableException;
import com.goylik.order_service.model.dto.client.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public ResponseEntity<UserResponse> getUserByIdInternal(Long id) {
        log.warn("Can't get access to the user service. Can't get user info with id = {}", id);
        throw new UserServiceUnavailableException("User service is unavailable.");
    }
}
