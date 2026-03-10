package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    static final String STATUS_SUCCESS = "SUCCESS";
    static final String STATUS_REJECTED = "REJECTED";

    static final String METHOD_VOUCHER = "VOUCHER_CODE";
    static final String METHOD_COD = "CASH_ON_DELIVERY";
    static final String METHOD_BANK_TRANSFER = "BANK_TRANSFER";

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setPaymentData(paymentData);
        payment.setStatus(deriveInitialStatus(method, paymentData));
        return paymentRepository.save(payment);
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        if (STATUS_SUCCESS.equals(status)) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (STATUS_REJECTED.equals(status)) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.getPayment(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }

    private String deriveInitialStatus(String method, Map<String, String> paymentData) {
        if (METHOD_VOUCHER.equals(method)) {
            return validateVoucher(paymentData) ? STATUS_SUCCESS : STATUS_REJECTED;
        }
        if (METHOD_COD.equals(method)) {
            return hasValue(paymentData, "address") && hasValue(paymentData, "deliveryFee")
                    ? STATUS_SUCCESS : STATUS_REJECTED;
        }
        if (METHOD_BANK_TRANSFER.equals(method)) {
            return hasValue(paymentData, "bankName") && hasValue(paymentData, "referenceCode")
                    ? STATUS_SUCCESS : STATUS_REJECTED;
        }
        return STATUS_REJECTED;
    }

    private boolean validateVoucher(Map<String, String> paymentData) {
        if (!hasValue(paymentData, "voucherCode")) {
            return false;
        }
        String voucherCode = paymentData.get("voucherCode");
        if (voucherCode.length() != 16) {
            return false;
        }
        if (!voucherCode.startsWith("ESHOP")) {
            return false;
        }

        int digitCount = 0;
        for (int i = 0; i < voucherCode.length(); i++) {
            if (Character.isDigit(voucherCode.charAt(i))) {
                digitCount += 1;
            }
        }
        return digitCount == 8;
    }

    private boolean hasValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return false;
        }
        String value = paymentData.get(key);
        return value != null && !value.isBlank();
    }
}
