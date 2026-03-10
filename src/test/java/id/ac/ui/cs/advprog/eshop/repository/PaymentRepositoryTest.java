package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentRepositoryTest {
    private PaymentRepository paymentRepository;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
        payment = new Payment();
        payment.setId("payment-1");
        payment.setMethod("VOUCHER_CODE");
        payment.setStatus("SUCCESS");
        payment.setPaymentData(Map.of("voucherCode", "ESHOP1234ABC5678"));
        payment.setOrder(new Order("order-1", List.of(createProduct()), 1708560000L, "Safira"));
    }

    @Test
    void saveAndGetPaymentWorks() {
        paymentRepository.save(payment);

        Payment result = paymentRepository.getPayment("payment-1");
        assertNotNull(result);
        assertEquals("payment-1", result.getId());
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void saveUpdatesExistingPayment() {
        paymentRepository.save(payment);
        payment.setStatus("REJECTED");
        paymentRepository.save(payment);

        Payment result = paymentRepository.getPayment("payment-1");
        assertEquals("REJECTED", result.getStatus());
        assertEquals(1, paymentRepository.getAllPayments().size());
    }

    @Test
    void getAllPaymentsReturnsAll() {
        Payment payment2 = new Payment();
        payment2.setId("payment-2");
        payment2.setMethod("CASH_ON_DELIVERY");
        payment2.setStatus("SUCCESS");
        payment2.setPaymentData(Map.of("address", "UI", "deliveryFee", "10000"));
        payment2.setOrder(new Order("order-2", List.of(createProduct()), 1708560000L, "Safira"));

        paymentRepository.save(payment);
        paymentRepository.save(payment2);

        assertEquals(2, paymentRepository.getAllPayments().size());
    }

    private Product createProduct() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        return product;
    }
}
