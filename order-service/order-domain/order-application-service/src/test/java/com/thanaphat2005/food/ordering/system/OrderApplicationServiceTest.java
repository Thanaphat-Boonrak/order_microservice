package com.thanaphat2005.food.ordering.system;


import com.thanaphat2005.food.ordering.system.domain.valueobject.*;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Customer;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Order;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Product;
import com.thanaphat2005.food.ordering.system.order.service.domain.entity.Restaurant;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderCommand;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.CreateOrderResponse;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.OrderAddress;
import com.thanaphat2005.food.ordering.system.order.service.dto.create.OrderItem;
import com.thanaphat2005.food.ordering.system.order.service.mapper.OrderDataMapper;
import com.thanaphat2005.food.ordering.system.domain.event.payload.OrderPaymentEventPayload;
import com.thanaphat2005.food.ordering.system.order.service.outbox.model.payment.OrderPaymentOutboxMessage;
import com.thanaphat2005.food.ordering.system.order.service.ports.input.service.OrderApplicationService;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.ai.order.noteinterpreter.OrderNoteInterpreter;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.CustomerRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.OrderRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.PaymentOutboxRepository;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.RestaurantRepository;
import com.thanaphat2005.food.ordering.system.outbox.OutboxStatus;
import com.thanaphat2005.food.ordering.system.saga.SagaStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.thanaphat2005.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfigure.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderNoteInterpreter orderNoteInterpreter;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    private final BigDecimal PRICE = new BigDecimal("200.00");
    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final String orderNotes = "no onions , with pickles , extra no spicy. Leave at the door.";
    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(PRICE)
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .orderNotes(orderNotes)
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        Customer customer = new Customer(new CustomerId(CUSTOMER_ID));
        OrderPreferences orderPreferences = OrderPreferences.builder().addIngredients(List.of("pickle")).spiceLevel(SpiceLevel.MEDIUM)
                .deliveryInstructions("Leave at the door").build();
        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(new Product(new ProductId(PRODUCT_ID),
                                "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new
                                BigDecimal("50.00")))))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));
        order.updateOrderPreferences(orderPreferences);
        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand))).thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderNoteInterpreter.interpret(orderNotes)).thenReturn(orderPreferences);
        when(paymentOutboxRepository.save((any(OrderPaymentOutboxMessage.class)))).thenReturn(getOrderPaymentOutboxMessage());
    }


    @Test
    public void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order created Successfully", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals(orderDomainException.getMessage(),
                "Total price: 250.00 is not equal to Order items total: 200.00!");
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        assertEquals(orderDomainException.getMessage(),
                "Order item price: 60.00 is not valid for product " + PRODUCT_ID);
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));

        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand));

        assertEquals(orderDomainException.getMessage(),
                "Restaurant with id " + RESTAURANT_ID + " is currently not active!");
    }

    private OrderPaymentOutboxMessage getOrderPaymentOutboxMessage() {
        OrderPaymentEventPayload orderPaymentEventPayload = OrderPaymentEventPayload.builder()
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(ZonedDateTime.now())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();

        return OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(SAGA_ID)
                .createdAt(ZonedDateTime.now())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderPaymentEventPayload))
                .orderStatus(OrderStatus.PENDING)
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .version(0)
                .build();
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        return objectMapper.writeValueAsString(orderPaymentEventPayload);
    }
}
