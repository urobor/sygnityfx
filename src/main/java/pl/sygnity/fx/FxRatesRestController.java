package pl.sygnity.fx;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1")
public class FxRatesRestController {

    @Autowired
    FxRatesService fxRatesService;
    @GetMapping("/fxrates/{fromCurrency}/{toCurrency}/{date}/{amount}")
    public BigDecimal calculateRate(@PathVariable("fromCurrency") String fromCurrencyCode,
                                    @PathVariable("toCurrency") String toCurrencyCode,
                                    @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                    @PathVariable("amount") BigDecimal amount) {
        return fxRatesService.calculateFxRate(fromCurrencyCode, toCurrencyCode, date, amount);
    }
}
