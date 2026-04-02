package com.goylik.order_service.specification;

import com.goylik.order_service.model.entity.Order;
import com.goylik.order_service.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSpecificationTest {
    @Test
    void createdBetweenAndHasStatuses_ShouldBuildSpecification() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();
        List<OrderStatus> statuses = List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);

        Specification<Order> spec = OrderSpecification
                .createdBetweenAndHasStatuses(from, to, statuses);

        assertThat(spec).isNotNull();
    }
}
