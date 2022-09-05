package pl.sygnity.fx;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import pl.sygnity.fx.nbp.response.model.Currency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class FxCalculator {

    @GetMapping("/fxrates/{fromCurrency}/{toCurrency}/{date}/{amount}")
    public BigDecimal calculateRate(@PathVariable("fromCurrency") String srcCode,
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
        String fromRate = c.getRates().get(0).getMid();

        //get 'to' currency to pln rate
        c = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchangerates/rates/a/{currencyCode}/{date}")
                        .build(dstCode, date))
                .retrieve().bodyToMono(Currency.class).block();
        String toRate = c.getRates().get(0).getMid();

        //calculate final rate
        BigDecimal finalRate = new BigDecimal(fromRate).divide(new BigDecimal(toRate), 16, RoundingMode.HALF_UP);

        //get amount in 'to' currency
        BigDecimal convertedAmount = amount.multiply(finalRate);

        return convertedAmount.round(new MathContext(3));
    }
}
