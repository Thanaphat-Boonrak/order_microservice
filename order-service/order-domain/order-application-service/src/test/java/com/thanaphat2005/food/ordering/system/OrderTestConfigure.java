package com.thanaphat2005.food.ordering.system;


import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainService;
import com.thanaphat2005.food.ordering.system.order.service.domain.OrderDomainServiceImpl;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.ai.order.noteinterpreter.OrderNoteInterpreter;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.thanaphat2005.food.ordering.system.order.service.ports.output.repository.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.thanaphat2005.food.ordering.system")
public class OrderTestConfigure {


    @Bean
    public PaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher(){
        return Mockito.mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher(){
        return Mockito.mock(RestaurantApprovalRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository(){
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository(){
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository(){
        return Mockito.mock(RestaurantRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService(){
        return new OrderDomainServiceImpl();
    }


    @Bean
    public PaymentOutboxRepository paymentOutboxRepository(){
        return Mockito.mock(PaymentOutboxRepository.class);
    }

    @Bean
    public ApprovalOutboxRepository approvalOutboxRepository(){
        return Mockito.mock(ApprovalOutboxRepository.class);
    }

    @Bean
    public OrderNoteInterpreter orderNoteInterpreter(){
        return Mockito.mock(OrderNoteInterpreter.class);
    }
}
