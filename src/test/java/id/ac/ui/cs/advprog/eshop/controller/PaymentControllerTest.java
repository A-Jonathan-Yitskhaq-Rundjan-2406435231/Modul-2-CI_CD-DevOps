package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void paymentDetailFormRenders() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetail"));
    }

    @Test
    void paymentDetailRendersResult() throws Exception {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        mockMvc.perform(get("/payment/detail/payment-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetailResult"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService).getPayment("payment-1");
    }

    @Test
    void paymentAdminListRenders() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of());

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminList"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    void paymentAdminDetailRedirectsWhenMissing() throws Exception {
        when(paymentService.getPayment("missing")).thenReturn(null);

        mockMvc.perform(get("/payment/admin/detail/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));
    }

    @Test
    void paymentAdminDetailRendersWhenFound() throws Exception {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        mockMvc.perform(get("/payment/admin/detail/payment-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void paymentAdminSetStatusUpdates() throws Exception {
        Payment payment = new Payment();
        payment.setId("payment-1");
        when(paymentService.getPayment("payment-1")).thenReturn(payment);
        when(paymentService.setStatus(payment, "SUCCESS")).thenReturn(payment);

        mockMvc.perform(post("/payment/admin/set-status/payment-1")
                        .param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminDetail"))
                .andExpect(model().attributeExists("payment"));

        verify(paymentService).setStatus(payment, "SUCCESS");
    }

    @Test
    void paymentAdminSetStatusRedirectsWhenMissing() throws Exception {
        when(paymentService.getPayment("missing")).thenReturn(null);

        mockMvc.perform(post("/payment/admin/set-status/missing")
                        .param("status", "SUCCESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/admin/list"));
    }
}
