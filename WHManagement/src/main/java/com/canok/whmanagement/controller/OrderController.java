package com.canok.whmanagement.controller;


import com.canok.whmanagement.dto.OrderDto;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {


    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto){
        Boolean result=orderService.createOrder(orderDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<?> findOrderByClientId(@PathVariable Long clientId){
        List<Order> orderList=orderService.findClientOrderByClientId(clientId);
        return ResponseEntity.ok(orderList);
    }
}
