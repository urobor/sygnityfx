package pl.sygnity.fx;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import pl.sygnity.fx.nbp.response.model.Currency;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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
                .retrieve().bodyToMono(Currency.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex)).block();
        if(c == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        String fromRate = c.getRates().get(0).getMid();

        //get 'to' currency to pln rate
        c = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchangerates/rates/a/{currencyCode}/{date}")
                        .build(dstCode, date))
                .retrieve().bodyToMono(Currency.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex)).block();
        if(c == null) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }

        String toRate = c.getRates().get(0).getMid();

        //calculate final rate
        BigDecimal finalRate = new BigDecimal(fromRate).divide(new BigDecimal(toRate), 16, RoundingMode.HALF_UP);

        //get amount in 'to' currency
        BigDecimal convertedAmount = amount.multiply(finalRate);

        return convertedAmount.round(new MathContext(3));
    }
}
