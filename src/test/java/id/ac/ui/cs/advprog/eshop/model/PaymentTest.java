package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PaymentTest {

    @Test
    void paymentStoresFields() {
        Payment payment = new Payment();
        Order order = new Order("id-1", java.util.List.of(createProduct()), 1708560000L, "Safira");

        payment.setId("payment-1");
        payment.setMethod("VOUCHER_CODE");
        payment.setStatus("SUCCESS");
        payment.setPaymentData(Map.of("voucherCode", "ESHOP1234ABC5678"));
        payment.setOrder(order);

        assertEquals("payment-1", payment.getId());
        assertEquals("VOUCHER_CODE", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("ESHOP1234ABC5678", payment.getPaymentData().get("voucherCode"));
        assertSame(order, payment.getOrder());
    }

    private Product createProduct() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        return product;
    }
}
