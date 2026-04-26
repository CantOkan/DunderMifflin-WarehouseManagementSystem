package com.canok.whmanagement.controller;


import com.canok.whmanagement.dto.OrderDto;
import com.canok.whmanagement.entity.DeliveryStatus;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto) {
        Boolean result = orderService.createOrder(orderDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> findOrderByClientId(@PathVariable Long clientId) {
        List<Order> orderList = orderService.findClientOrderByClientId(clientId);
        return ResponseEntity.ok(orderList);
    }

    @PatchMapping("/update/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String id, @RequestParam DeliveryStatus status) {
        Order result = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        Boolean result = orderService.deleteOrder(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/group-by-location")
    public ResponseEntity<?> groupOrdersByLocation() {
        Map<String, List<Order>> result = orderService.groupOrdersByLocation();
        return ResponseEntity.ok(result);
    }
}
