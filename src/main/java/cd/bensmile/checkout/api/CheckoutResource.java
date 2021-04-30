package cd.bensmile.checkout.api;


import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CheckoutResource {

    private Map<String, String> paymentDataStore = new HashMap<>();

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
    public ResponseEntity<PaymentsResponse> payment(@RequestBody PaymentsRequest body, HttpServletRequest request) throws IOException, ApiException {


        log.info("########  request to get Adyen  initiatePayment {}", body);
        var paymentRequest = new PaymentsRequest();
        paymentRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
        paymentRequest.setMerchantAccount(merchantAccount);
        var amount = new Amount().currency("EUR").value(1000L);

        // reqiured 3D secure
        paymentRequest.additionalData(Collections.singletonMap("allow3DS2", "true"));
        paymentRequest.setOrigin("http://localhost:8080");
        paymentRequest.setShopperIP(request.getRemoteAddr());

        paymentRequest.setAmount(amount);
        var orderRef = UUID.randomUUID().toString();
        paymentRequest.setReference(orderRef);

        paymentRequest.setReturnUrl("http://localhost:8080/api/handleShoppedRedirect?orderRef=" + orderRef);

        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());


        var response = checkout.payments(paymentRequest);
        log.info("REST response to get Adyen payment methods {}", response);


        log.info("response.getAction() = {}", response.getAction() == null);


//        System.out.println("response.getAction() = " + response.getAction() == null);

        if(response.getAction() != null){
            if (!response.getAction().getPaymentData().isEmpty()){
                paymentDataStore.put(orderRef, response.getAction().getPaymentData());
            }
        }

        return ResponseEntity.ok().body(response);

    }


    @GetMapping("/handleShoppedRedirect")
    public RedirectView redirect(@RequestParam(required = false) String redirectResult, @RequestParam String orderRef) throws IOException, ApiException {

        var detailsRequest = new PaymentsDetailsRequest();
        if (redirectResult != null && !redirectResult.isEmpty()) {

            detailsRequest.setDetails(Collections.singletonMap("redirectResult", redirectResult));
        }

        detailsRequest.setPaymentData(paymentDataStore.get(orderRef));
        return getRedirectView(detailsRequest);
    }

    @PostMapping("/submitAddtionnalDetails")
    public ResponseEntity<?> submitAddtionnalDetails(@RequestBody PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST response to make Adyen payment details {}", detailsRequest);
        var response = checkout.paymentsDetails(detailsRequest);
        return ResponseEntity.ok().body(response);
    }

//    @PostMapping(path = "/handleShoppedRedirect", consumes = MediaType.APPLICATION_FORM_URLENCODED)

    @RequestMapping(value = "/handleShoppedRedirect", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    private RedirectView preview(@RequestParam("MD") String md, @RequestParam("paRes") String paRes, @RequestParam String orderRef) throws IOException, ApiException {

        var details = new HashMap<String, String>();

        details.put("MD", md);
        details.put("PaRes", paRes);

        var detailsRequest = new PaymentsDetailsRequest();

        detailsRequest.setDetails(details);
        detailsRequest.setPaymentData(paymentDataStore.get(orderRef));


        return null;
    }

    public RedirectView getRedirectView(final PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {

        var response = checkout.paymentsDetails(detailsRequest);
        var redirectURL = "/result/";
        switch (response.getResultCode()) {
            case AUTHORISED:
                redirectURL += "success";
                break;
            case PENDING:
            case RECEIVED:
                redirectURL += "pending";
                break;
            case REFUSED:
                redirectURL += "failed";
                break;

            default:
                redirectURL += "error";
                break;
        }

        return new RedirectView(redirectURL);
    }

}
