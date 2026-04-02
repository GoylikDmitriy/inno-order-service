package com.goylik.order_service.client;

import com.goylik.order_service.client.fallback.UserServiceClientFallback;
import com.goylik.order_service.model.dto.client.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${client.user-service.url}",
        fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/internal/{id}}")
    ResponseEntity<UserResponse> getUserByIdInternal(@PathVariable Long id);
}
