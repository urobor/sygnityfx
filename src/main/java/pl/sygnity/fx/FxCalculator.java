package pl.sygnity.fx;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

        //get 'to' currency to pln rate

        //calculate final rate

        //get amount in 'to' currency

        return BigDecimal.TEN;
    }
}
