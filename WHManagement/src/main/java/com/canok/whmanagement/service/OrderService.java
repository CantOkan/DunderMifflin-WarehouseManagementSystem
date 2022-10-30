package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.dto.OrderDto;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ClientRepository clientRepository;

    public Boolean createOrder(OrderDto orderDto){
        Optional<Client> optClient=clientRepository.findById(orderDto.getClientId());
        if(optClient.isPresent()){

            orderRepository.save(orderDto.convertToOrder());
            return true;
        }

        return false;
    }

    public List<Order> findClientOrderByClientId(Long clientId){
        Optional<Client> optClient=clientRepository.findById(clientId);
        if(optClient.isPresent()){

            return orderRepository.findByClientId(clientId);
        }

        return new ArrayList<Order>();
    }
}
