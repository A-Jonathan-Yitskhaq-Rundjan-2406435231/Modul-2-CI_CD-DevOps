package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        order = new Order("order-1", List.of(product), 1708560000L, "Safira");
    }

    @Test
    void addPaymentVoucherValidIsSuccess() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.METHOD_VOUCHER,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );

        assertNotNull(result.getId());
        assertEquals(PaymentServiceImpl.STATUS_SUCCESS, result.getStatus());
    }

    @Test
    void addPaymentVoucherInvalidIsRejected() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.METHOD_VOUCHER,
                Map.of("voucherCode", "INVALID")
        );

        assertEquals(PaymentServiceImpl.STATUS_REJECTED, result.getStatus());
    }

    @Test
    void addPaymentCodInvalidIsRejected() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.METHOD_COD,
                Map.of("address", "", "deliveryFee", "10000")
        );

        assertEquals(PaymentServiceImpl.STATUS_REJECTED, result.getStatus());
    }

    @Test
    void addPaymentCodValidIsSuccess() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.METHOD_COD,
                Map.of("address", "Depok", "deliveryFee", "10000")
        );

        assertEquals(PaymentServiceImpl.STATUS_SUCCESS, result.getStatus());
    }

    @Test
    void setStatusSuccessUpdatesOrderStatusToSuccess() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        payment.setOrder(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.setStatus(payment, PaymentServiceImpl.STATUS_SUCCESS);

        assertEquals(PaymentServiceImpl.STATUS_SUCCESS, result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void setStatusRejectedUpdatesOrderStatusToFailed() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        payment.setOrder(order);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.setStatus(payment, PaymentServiceImpl.STATUS_REJECTED);

        assertEquals(PaymentServiceImpl.STATUS_REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void getPaymentDelegatesToRepository() {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentRepository.getPayment("payment-1")).thenReturn(payment);

        Payment result = paymentService.getPayment("payment-1");

        assertEquals("payment-1", result.getId());
        verify(paymentRepository).getPayment("payment-1");
    }

    @Test
    void getAllPaymentsDelegatesToRepository() {
        when(paymentRepository.getAllPayments()).thenReturn(List.of(new Payment(), new Payment()));

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(2, result.size());
        verify(paymentRepository).getAllPayments();
    }

    @Test
    void addPaymentPersistsBuiltPayment() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.addPayment(
                order,
                PaymentServiceImpl.METHOD_VOUCHER,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentServiceImpl.METHOD_VOUCHER, paymentCaptor.getValue().getMethod());
    }
}
