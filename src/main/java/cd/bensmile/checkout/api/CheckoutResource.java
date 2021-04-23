package cd.bensmile.checkout.api;


import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CheckoutResource {

    private String merchantAccount ="CkStoreECOM";
    private String apiKey ="AQEihmfuXNWTK0Qc+iSTmVcsqPaeRffDolbEW/60TgIO5FZgNRDBXVsNvuR83LVYjEgiTGAH-OtCbBryhpw+0p4Dw2AnK6TOIYzTJhyepErcKL8BAq9w=-y~[<ZrtjGxYT7548";
    final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final Checkout checkout;

    public  CheckoutResource(){
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
}
