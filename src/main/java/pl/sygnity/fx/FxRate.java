package pl.sygnity.fx;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FxRate {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    LocalDate date;
    String code;
    BigDecimal value;
}
