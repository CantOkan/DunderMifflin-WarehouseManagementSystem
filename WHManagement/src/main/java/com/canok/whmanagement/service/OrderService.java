package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.OrderDto;
import com.canok.whmanagement.entity.DeliveryStatus;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.messaging.OrderEventPublisher;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.repository.OrderRepository;
import com.canok.whmanagement.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final AuthorizationService authorizationService;

    public Boolean createOrder(OrderDto orderDto) {
        authorizationService.verifyClientOwnershipOrEmployee(orderDto.getClientId());
        if (!clientRepository.existsById(orderDto.getClientId())) {
            return false;
        }
        Order order = orderRepository.save(orderDto.convertToOrder());
        orderEventPublisher.publishOrderCreated(order);
        return true;
    }

    public List<Order> findClientOrderByClientId(Long clientId) {
        authorizationService.verifyClientOwnershipOrEmployee(clientId);
        if (!clientRepository.existsById(clientId)) {
            return Collections.emptyList();
        }
        return orderRepository.findByClientId(clientId);
    }

    public Order updateOrderStatus(String id, DeliveryStatus status) {
        authorizationService.verifyEmployeeAccess();
        Optional<Order> optional = orderRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Order order = optional.get();
        order.setDeliveryStatus(status);
        Order updated = orderRepository.save(order);
        orderEventPublisher.publishOrderStatusUpdated(updated);
        return updated;
    }

    public Boolean deleteOrder(String id) {
        authorizationService.verifyEmployeeAccess();
        if (!orderRepository.existsById(id)) {
            return false;
        }
        orderRepository.deleteById(id);
        return true;
    }

    public Map<String, List<Order>> groupOrdersByLocation() {
        authorizationService.verifyEmployeeAccess();
        List<Order> allOrders = orderRepository.findAll();
        Map<String, List<Order>> grouped = new LinkedHashMap<>();
        for (Order order : allOrders) {
            clientRepository.findById(order.getClientId()).ifPresent(client -> {
                String location = client.getAddress().getCity() + ", " + client.getAddress().getCountry();
                grouped.computeIfAbsent(location, k -> new ArrayList<>()).add(order);
            });
        }
        return grouped;
    }
}
