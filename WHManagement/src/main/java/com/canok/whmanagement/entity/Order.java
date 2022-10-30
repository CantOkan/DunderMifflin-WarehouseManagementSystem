package com.canok.whmanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private Long clientId;
    @Indexed(unique = true)
    private String orderName;
    private LocalDateTime created;
    private LocalDateTime deliveryDate;
    private DeliveryStatus deliveryStatus;
    private Map<String,Integer> items;


}
