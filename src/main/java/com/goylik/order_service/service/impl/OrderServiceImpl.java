package com.goylik.order_service.service.impl;

import com.goylik.order_service.client.UserServiceClient;
import com.goylik.order_service.exception.ItemNotFoundException;
import com.goylik.order_service.exception.OrderNotFoundException;
import com.goylik.order_service.mapper.OrderMapper;
import com.goylik.order_service.model.dto.client.UserResponse;
import com.goylik.order_service.model.dto.request.CreateOrderRequest;
import com.goylik.order_service.model.dto.request.FilterRequest;
import com.goylik.order_service.model.dto.request.OrderItemRequest;
import com.goylik.order_service.model.dto.request.UpdateOrderRequest;
import com.goylik.order_service.model.dto.response.OrderResponse;
import com.goylik.order_service.model.entity.Item;
import com.goylik.order_service.model.entity.Order;
import com.goylik.order_service.model.entity.OrderItem;
import com.goylik.order_service.repository.ItemRepository;
import com.goylik.order_service.repository.OrderRepository;
import com.goylik.order_service.service.OrderService;
import com.goylik.order_service.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;

    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        var order = Order.create(request.userId());
        var items = buildOrderItems(request.items(), order);

        order.setItems(items);

        return enrichWithUserInfo(orderMapper.toResponse(orderRepository.save(order)));
    }

    private List<OrderItem> buildOrderItems(List<OrderItemRequest> itemRequests, Order order) {
        List<Long> itemIds = itemRequests.stream()
                .map(OrderItemRequest::itemId)
                .toList();

        Map<Long, Item> itemsById = itemRepository.findAllById(itemIds)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        itemIds.forEach(id -> {
            if (!itemsById.containsKey(id)) {
                throw new ItemNotFoundException("Item not found with id = " + id);
            }
        });

        return itemRequests.stream()
                .map(req -> OrderItem.create(
                        order,
                        itemsById.get(req.itemId()),
                        req.quantity()))
                .toList();
    }

    private OrderResponse enrichWithUserInfo(OrderResponse response) {
        UserResponse user = userServiceClient.getUserByIdInternal(response.userId()).getBody();
        return new OrderResponse(
                response.id(),
                response.userId(),
                user,
                response.status(),
                response.totalPrice(),
                response.items(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        var order = fetchByIdWithItemsOrThrow(id);
        return enrichWithUserInfo(orderMapper.toResponse(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAll(Pageable pageable, FilterRequest request) {
        var specs = OrderSpecification
                .createdBetweenAndHasStatuses(request.from(), request.to(), request.statuses());

        return orderRepository
                .findAll(specs, pageable)
                .map(orderMapper::toResponse)
                .map(this::enrichWithUserInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toResponse)
                .map(this::enrichWithUserInfo);

    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, UpdateOrderRequest request) {
        var order = fetchByIdWithItemsOrThrow(id);

        if (request.status() != null) {
            order.setStatus(request.status());
        }

        if (request.items() != null && !request.items().isEmpty()) {
            var newItems = buildOrderItems(request.items(), order);
            order.setItems(newItems);
        }

        var updatedOrder = orderRepository.save(order);
        return enrichWithUserInfo(orderMapper.toResponse(updatedOrder));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        var order = fetchByIdOrThrow(id);
        orderRepository.delete(order);
    }

    private Order fetchByIdWithItemsOrThrow(Long id) {
        return fetchOrder(id, () -> orderRepository.findByIdWithItems(id));
    }

    private Order fetchByIdOrThrow(Long id) {
        return fetchOrder(id, () -> orderRepository.findById(id));
    }

    private Order fetchOrder(Long id, Supplier<Optional<Order>> fetcher) {
        return fetcher.get()
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id = " + id));
    }
}
