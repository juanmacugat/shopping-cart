import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.payment.Payer;
import com.mercadopago.resources.datastructures.preference.Item;
import com.mercadopago.resources.datastructures.preference.PaymentMethods;
import io.javalin.Javalin;

import java.util.*;

public class Application {

    public static void main(String[] args) {

        String accessToken = System.getenv("ACCESS_TOKEN");;

        Javalin app = Javalin.create(config-> {
            config.defaultContentType = "application/json";
            config.enableCorsForAllOrigins();
        }).start(8080);

        app.get("/checkout", ctx -> ctx.render("checkout.html"));
        app.get("/success", ctx -> ctx.render("success.html"));
        app.get("/fail", ctx -> ctx.render("fail.html"));

        app.get("/payment-button", ctx -> {
            Item item = new Item().setDescription("Boton de pago").setUnitPrice(100f).setQuantity(1);
            Preference preference = new Preference()
                    .setBinaryMode(true)
                    .setPaymentMethods(new PaymentMethods().setExcludedPaymentMethods("rapipago", "pagofacil"))
                    .setExternalReference(UUID.randomUUID().toString())
                    .setItems(new ArrayList(Collections.singleton(item)))
                    .save(MPRequestOptions.builder().setAccessToken(accessToken).build());
            System.out.println(preference.getInitPoint());
        });

        app.post("/pay", ctx -> {

            String cardToken = ctx.formParam("token");
            String issuerId = ctx.formParam("issuer_id");
            String installments = ctx.formParam("installments");
            String paymentMethodId = ctx.formParam("payment_method_id");

            Payment payment = null;

            try{
                payment = new Payment()
                        .setPaymentMethodId(paymentMethodId)
                        .setTransactionAmount(100f)
                        .setInstallments(Integer.valueOf(installments))
                        .setToken(cardToken)
                        .setIssuerId(issuerId)
                        .setPayer(new Payer().setEmail("juanma.91c@gmail.com"))
                        .save(MPRequestOptions.builder().setAccessToken(accessToken).build());
            }catch (MPException e){
                ctx.redirect("/fail");
            }

            if (payment.getStatus() != Payment.Status.approved) {
                ctx.redirect("/fail");
            } else {
                ctx.redirect("/success");
            }
        });

        app.post("/callback", ctx->{

            Payment payment = Payment.findById("", true, MPRequestOptions.builder().setAccessToken(accessToken).build());
            System.out.println(payment.getId() + " " + payment.getStatus());
        });
    }
}
