package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.OrderDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.entity.DeliveryStatus;
import com.canok.whmanagement.entity.Order;
import com.canok.whmanagement.messaging.OrderEventPublisher;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.repository.OrderRepository;
import com.canok.whmanagement.security.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private OrderEventPublisher orderEventPublisher;
    @Mock private AuthorizationService authorizationService;

    @InjectMocks
    private OrderService orderService;

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        orderDto = OrderDto.builder()
                .clientId(1L)
                .orderName("Paper")
                .created(LocalDateTime.now())
                .deliveryDate(LocalDateTime.now())
                .deliveryStatus(DeliveryStatus.Processing)
                .items(Map.of("A4", 10))
                .build();
    }

    @Test
    void createOrder_shouldReturnFalse_whenClientMissing() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        assertThat(orderService.createOrder(orderDto)).isFalse();
        verify(authorizationService).verifyClientOwnershipOrEmployee(1L);
        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishOrderCreated(any());
    }

    @Test
    void createOrder_shouldSaveAndPublishEvent_whenClientExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        Order saved = Order.builder().id("o1").clientId(1L).build();
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        assertThat(orderService.createOrder(orderDto)).isTrue();

        verify(orderRepository).save(any(Order.class));
        verify(orderEventPublisher).publishOrderCreated(saved);
    }

    @Test
    void findClientOrderByClientId_shouldReturnEmpty_whenClientMissing() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        assertThat(orderService.findClientOrderByClientId(1L)).isEmpty();
        verify(authorizationService).verifyClientOwnershipOrEmployee(1L);
        verify(orderRepository, never()).findByClientId(any());
    }

    @Test
    void findClientOrderByClientId_shouldReturnOrders_whenClientExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        List<Order> orders = List.of(Order.builder().id("o1").clientId(1L).build());
        when(orderRepository.findByClientId(1L)).thenReturn(orders);

        assertThat(orderService.findClientOrderByClientId(1L)).hasSize(1);
    }

    @Test
    void updateOrderStatus_shouldReturnNull_whenOrderMissing() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThat(orderService.updateOrderStatus("missing", DeliveryStatus.Delivered)).isNull();
        verify(authorizationService).verifyEmployeeAccess();
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_shouldUpdateAndPublish() {
        Order existing = Order.builder().id("o1").deliveryStatus(DeliveryStatus.Processing).build();
        when(orderRepository.findById("o1")).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updateOrderStatus("o1", DeliveryStatus.Delivered);

        assertThat(result.getDeliveryStatus()).isEqualTo(DeliveryStatus.Delivered);
        verify(orderEventPublisher).publishOrderStatusUpdated(result);
    }
    @Test
    void deleteOrder_shouldReturnFalse_whenMissing() {
        when(orderRepository.existsById("o1")).thenReturn(false);
        assertThat(orderService.deleteOrder("o1")).isFalse();
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void deleteOrder_shouldDelete_whenExists() {
        when(orderRepository.existsById("o1")).thenReturn(true);
        assertThat(orderService.deleteOrder("o1")).isTrue();
        verify(orderRepository).deleteById("o1");
    }

    @Test
    void groupOrdersByLocation_shouldGroupByCityAndCountry() {
        Address berlin = Address.builder().city("Berlin").country("DE").build();
        Address paris = Address.builder().city("Paris").country("FR").build();
        Client c1 = Client.builder().id(1L).address(berlin).build();
        Client c2 = Client.builder().id(2L).address(paris).build();

        Order o1 = Order.builder().id("o1").clientId(1L).build();
        Order o2 = Order.builder().id("o2").clientId(2L).build();
        Order o3 = Order.builder().id("o3").clientId(1L).build();

        when(orderRepository.findAll()).thenReturn(List.of(o1, o2, o3));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(clientRepository.findById(2L)).thenReturn(Optional.of(c2));

        Map<String, List<Order>> grouped = orderService.groupOrdersByLocation();

        assertThat(grouped).hasSize(2);
        assertThat(grouped.get("Berlin, DE")).containsExactly(o1, o3);
        assertThat(grouped.get("Paris, FR")).containsExactly(o2);
        verify(authorizationService).verifyEmployeeAccess();
    }

    @Test
    void groupOrdersByLocation_shouldSkipOrdersWithMissingClient() {
        Order o1 = Order.builder().id("o1").clientId(99L).build();
        when(orderRepository.findAll()).thenReturn(List.of(o1));
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        Map<String, List<Order>> grouped = orderService.groupOrdersByLocation();

        assertThat(grouped).isEmpty();
    }
}