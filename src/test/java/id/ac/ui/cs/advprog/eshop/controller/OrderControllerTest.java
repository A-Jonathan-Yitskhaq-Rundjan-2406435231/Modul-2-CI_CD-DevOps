package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createOrderPageRendersForm() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderCreate"));
    }

    @Test
    void createOrderPostRendersCreatedPage() throws Exception {
        Order order = new Order("order-1", List.of(createProduct()), 1708560000L, "Safira");
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/order/create")
                        .param("author", "Safira")
                        .param("productName", "Keyboard")
                        .param("productQuantity", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderCreated"))
                .andExpect(model().attributeExists("order"));

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    void historyFormRenders() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"));
    }

    @Test
    void historyListRendersResults() throws Exception {
        when(orderService.findAllByAuthor("Safira")).thenReturn(List.of());

        mockMvc.perform(post("/order/history")
                        .param("author", "Safira"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistoryList"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    void payOrderPageRedirectsWhenMissing() throws Exception {
        when(orderService.findById("missing")).thenReturn(null);

        mockMvc.perform(get("/order/pay/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/history"));
    }

    @Test
    void payOrderPageRendersWhenFound() throws Exception {
        Order order = new Order("order-1", List.of(createProduct()), 1708560000L, "Safira");
        when(orderService.findById("order-1")).thenReturn(order);

        mockMvc.perform(get("/order/pay/order-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderPay"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void payOrderPostCreatesPayment() throws Exception {
        Order order = new Order("order-1", List.of(createProduct()), 1708560000L, "Safira");
        Payment payment = new Payment();
        payment.setId("payment-1");
        payment.setStatus("SUCCESS");
        when(orderService.findById("order-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("VOUCHER_CODE"), any(Map.class))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/order-1")
                        .param("method", "VOUCHER_CODE")
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentResult"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService).addPayment(eq(order), eq("VOUCHER_CODE"), any(Map.class));
    }

    @Test
    void payOrderPostCreatesPaymentCod() throws Exception {
        Order order = new Order("order-1", List.of(createProduct()), 1708560000L, "Safira");
        Payment payment = new Payment();
        payment.setId("payment-2");
        payment.setStatus("SUCCESS");
        when(orderService.findById("order-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("CASH_ON_DELIVERY"), any(Map.class))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/order-1")
                        .param("method", "CASH_ON_DELIVERY")
                        .param("address", "Depok")
                        .param("deliveryFee", "10000"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentResult"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService).addPayment(eq(order), eq("CASH_ON_DELIVERY"), any(Map.class));
    }

    @Test
    void payOrderPostCreatesPaymentBankTransfer() throws Exception {
        Order order = new Order("order-1", List.of(createProduct()), 1708560000L, "Safira");
        Payment payment = new Payment();
        payment.setId("payment-3");
        payment.setStatus("SUCCESS");
        when(orderService.findById("order-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("BANK_TRANSFER"), any(Map.class))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/order-1")
                        .param("method", "BANK_TRANSFER")
                        .param("bankName", "BCA")
                        .param("referenceCode", "REF123"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentResult"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService).addPayment(eq(order), eq("BANK_TRANSFER"), any(Map.class));
    }

    @Test
    void payOrderPostRedirectsWhenMissing() throws Exception {
        when(orderService.findById("missing")).thenReturn(null);

        mockMvc.perform(post("/order/pay/missing")
                        .param("method", "VOUCHER_CODE")
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/history"));
    }

    @Test
    void payOrderPostUnknownMethodStillCreatesPayment() throws Exception {
        Order order = new Order("order-1", List.of(createProduct()), 1708560000L, "Safira");
        Payment payment = new Payment();
        payment.setId("payment-4");
        when(orderService.findById("order-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("UNKNOWN"), any(Map.class))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/order-1")
                        .param("method", "UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentResult"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService).addPayment(eq(order), eq("UNKNOWN"), any(Map.class));
    }

    private Product createProduct() {
        Product product = new Product();
        product.setProductId("prod-1");
        product.setProductName("Keyboard");
        product.setProductQuantity(2);
        return product;
    }
}
