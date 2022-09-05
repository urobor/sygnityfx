package pl.sygnity.fx;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sygnity.fx.nbp.response.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class FxCalculator {

    @GetMapping("/fxrates/{fromCurrency}/{toCurrency}/{date}/{amount}")
    public Float calculateRate(@PathVariable("fromCurrency") String srcCode,
                                    @PathVariable("toCurrency") String dstCode,
                                    @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                    @PathVariable("amount") BigDecimal amount) {
        //get 'from' currency to pln rate
        WebClient client = WebClient.create("https://api.nbp.pl");
        Currency c = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchangerates/rates/a/{currencyCode}/{date}")
                        .build(srcCode, date))
                .retrieve().bodyToMono(Currency.class).block();
        Float fromRate = c.getRates().get(0).getMid();

        //get 'to' currency to pln rate
        c = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchangerates/rates/a/{currencyCode}/{date}")
                        .build(dstCode, date))
                .retrieve().bodyToMono(Currency.class).block();
        Float toRate = c.getRates().get(0).getMid();

        //calculate final rate
        Float finalRate = fromRate / toRate;

        //get amount in 'to' currency
        Float convertedAmount = amount.floatValue() * finalRate;

        return convertedAmount;
    }
}
