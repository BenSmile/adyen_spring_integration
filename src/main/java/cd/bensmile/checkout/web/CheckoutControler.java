package cd.bensmile.checkout.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CheckoutControler {

    private String clientKey="test_WE5SMESZJNHUPCTGJODDIII26IHHMLGE";

    @GetMapping("/")
    public String preview(){
        return "preview";
    }

    @GetMapping("/checkout")
    public String checkout( Model model){
        model.addAttribute("clientKey", clientKey);
        return "checkout";
    }
}
