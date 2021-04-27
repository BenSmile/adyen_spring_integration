package cd.bensmile.checkout.api;


import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CheckoutResource {

    private String merchantAccount = "CkStoreECOM";
    private String merchantAccount2 = "CkStore";
    private String apiKey = "AQEihmfuXNWTK0Qc+iSTmVcsqPaeRffDolbEW/60TgIO5FZgNRDBXVsNvuR83LVYjEgiTGAH-OtCbBryhpw+0p4Dw2AnK6TOIYzTJhyepErcKL8BAq9w=-y~[<ZrtjGxYT7548";
    final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final Checkout checkout;

    public CheckoutResource() {
        var client = new Client(apiKey, Environment.TEST);
        this.checkout = new Checkout(client);
    }

    @PostMapping("getPaymentMethods")
    public ResponseEntity<PaymentMethodsResponse> getPaymentMethods() throws IOException, ApiException {

        var paymentMethodsRequest = new PaymentMethodsRequest();

        paymentMethodsRequest.setMerchantAccount(merchantAccount);
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        log.info("REST request to get Adyen payment methods {}", paymentMethodsRequest);

        var response = checkout.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
            .body(response);
    }


    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentsResponse> payment(@RequestBody PaymentsRequest body)  throws IOException, ApiException{

        log.info("########  request to get Adyen  initiatePayment {}", body);
        var paymentRequest = new PaymentsRequest();
        paymentRequest.setMerchantAccount(merchantAccount);
        var amount = new Amount().currency("EUR").value(1000L);

        paymentRequest.setAmount(amount);
        var orderRef = UUID.randomUUID().toString();
        paymentRequest.setReference(orderRef);

        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());


        var response = checkout.payments(paymentRequest);
        log.info("REST response to get Adyen payment methods {}", response);

        return ResponseEntity.ok().body(response);

    }
}
