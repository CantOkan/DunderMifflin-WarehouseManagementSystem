package com.canok.whmanagement.dto;

import com.canok.whmanagement.entity.DeliveryStatus;
import com.canok.whmanagement.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDto {

    private Long clientId;
    private String orderName;
    private LocalDateTime created;
    private LocalDateTime deliveryDate;
    private DeliveryStatus deliveryStatus;
    private Map<String,Integer> items;

    public Order convertToOrder(){

        String uniqueOrderName=orderName+"-".concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return Order.builder().clientId(clientId).orderName(uniqueOrderName).created(LocalDateTime.now())
                .deliveryDate(LocalDateTime.now()).deliveryStatus(deliveryStatus)
                .items(items).build();
    }
}
