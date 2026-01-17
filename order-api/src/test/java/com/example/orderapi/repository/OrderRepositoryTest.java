package com.example.orderapi.repository;

import com.example.orderapi.entity.OrderEntity;
import com.example.orderapi.entity.OrderItemEntity;
import com.example.orderapi.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("OrderRepository Database Operations Tests")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private OrderEntity testOrder;

    @BeforeEach
    void setUp() {
        testOrder = OrderEntity.builder()
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .totalAmount(new BigDecimal("199.99"))
                .status(OrderStatus.PENDING)
                .address("123 Main St, City")
                .orderDate(LocalDateTime.now())
                .build();

        OrderItemEntity item1 = OrderItemEntity.builder()
                .itemName("Laptop")
                .quantity(1)
                .price(new BigDecimal("149.99"))
                .build();

        OrderItemEntity item2 = OrderItemEntity.builder()
                .itemName("Mouse")
                .quantity(2)
                .price(new BigDecimal("25.00"))
                .build();

        testOrder.addItem(item1);
        testOrder.addItem(item2);
    }

    @Test
    @DisplayName("Should save order to database")
    void testSaveOrder() {
        OrderEntity savedOrder = orderRepository.save(testOrder);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(savedOrder.getCustomerEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedOrder.getItems()).hasSize(2);
    }

    @Test
    @DisplayName("Should find order by ID")
    void testFindById() {
        OrderEntity savedOrder = entityManager.persistAndFlush(testOrder);

        Optional<OrderEntity> found = orderRepository.findById(savedOrder.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return empty when order not found")
    void testFindByIdNotFound() {
        Optional<OrderEntity> found = orderRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all orders")
    void testFindAll() {
        entityManager.persistAndFlush(testOrder);

        OrderEntity secondOrder = OrderEntity.builder()
                .customerName("Jane Smith")
                .customerEmail("jane.smith@example.com")
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.CONFIRMED)
                .orderDate(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(secondOrder);

        List<OrderEntity> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("Should find orders by customer email")
    void testFindByCustomerEmail() {
        entityManager.persistAndFlush(testOrder);

        List<OrderEntity> orders = orderRepository.findByCustomerEmail("john.doe@example.com");

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find orders by status")
    void testFindByStatus() {
        entityManager.persistAndFlush(testOrder);

        OrderEntity shippedOrder = OrderEntity.builder()
                .customerName("Jane Smith")
                .customerEmail("jane.smith@example.com")
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.SHIPPED)
                .orderDate(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(shippedOrder);

        List<OrderEntity> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        List<OrderEntity> shippedOrders = orderRepository.findByStatus(OrderStatus.SHIPPED);

        assertThat(pendingOrders).hasSize(1);
        assertThat(shippedOrders).hasSize(1);
    }

    @Test
    @DisplayName("Should find orders by customer name containing")
    void testFindByCustomerNameContaining() {
        entityManager.persistAndFlush(testOrder);

        List<OrderEntity> orders = orderRepository.findByCustomerNameContainingIgnoreCase("john");

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getCustomerEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should update order")
    void testUpdateOrder() {
        OrderEntity savedOrder = entityManager.persistAndFlush(testOrder);

        savedOrder.setStatus(OrderStatus.SHIPPED);
        savedOrder.setAddress("456 New Address, City");
        orderRepository.save(savedOrder);

        entityManager.clear();

        OrderEntity updatedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(updatedOrder.getAddress()).isEqualTo("456 New Address, City");
    }

    @Test
    @DisplayName("Should delete order")
    void testDeleteOrder() {
        OrderEntity savedOrder = entityManager.persistAndFlush(testOrder);
        Long orderId = savedOrder.getId();

        orderRepository.deleteById(orderId);

        Optional<OrderEntity> deleted = orderRepository.findById(orderId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should count orders")
    void testCountOrders() {
        entityManager.persistAndFlush(testOrder);

        OrderEntity secondOrder = OrderEntity.builder()
                .customerName("Jane Smith")
                .customerEmail("jane.smith@example.com")
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.CONFIRMED)
                .orderDate(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(secondOrder);

        long count = orderRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should check if order exists")
    void testExistsById() {
        OrderEntity savedOrder = entityManager.persistAndFlush(testOrder);

        boolean exists = orderRepository.existsById(savedOrder.getId());
        boolean notExists = orderRepository.existsById(999L);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
