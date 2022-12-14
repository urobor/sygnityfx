package pl.sygnity.fx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

@Service
public class FxRatesService {

    @Autowired
    private FxRatesRepository repository;
    @Autowired
    private WebClient nbpRestClient;

    public BigDecimal calculateFxRate(String fromCurrencyCode, String toCurrencyCode, LocalDate date, BigDecimal amount) {
        if(isWeekend(date)) {
            throw new ResponseStatusException(BAD_REQUEST, "Only working days");
        }
        BigDecimal fromRateValue = getFxRateWithCache(fromCurrencyCode, date);

        //get 'to' currency to pln rate
        BigDecimal toRate = getFxRateWithCache(toCurrencyCode, date);

        //calculate final rate
        BigDecimal finalRate = fromRateValue.divide(toRate, 16, RoundingMode.HALF_UP);

        //get amount in 'to' currency
        BigDecimal convertedAmount = amount.multiply(finalRate);

        return convertedAmount.round(new MathContext(3));
    }

    private BigDecimal getFxRateWithCache(String fromCurrencyCode, LocalDate date) {
        FxRate fromRate = repository.findByCodeAndDate(fromCurrencyCode, date);
        BigDecimal fromRateValue;
        if(fromRate != null) {
            fromRateValue = fromRate.getFxRateValue();
        } else {
            fromRateValue = getFxRateFromTableA(fromCurrencyCode, date);
            repository.save(new FxRate(date, fromCurrencyCode, fromRateValue));
        }
        return fromRateValue;
    }

    private BigDecimal getFxRateFromTableA(String currencyCode, LocalDate date) {
        Currency c = nbpRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/exchangerates/rates/a/{currencyCode}/{date}")
                        .build(currencyCode, date))
                .retrieve().bodyToMono(Currency.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex)).block();
        if(c == null) {
            throw new ResponseStatusException(NOT_FOUND, "Currency (" + currencyCode + ") not found for given date: " + date);
        }
        return new BigDecimal(c.getRates().get(0).getMid());
    }

    private static boolean isWeekend(final LocalDate ld)
    {
        DayOfWeek day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }
}
