package pl.sygnity.fx;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class FxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NonNull
    LocalDate date;
    @NonNull
    String code;
    @NonNull
    BigDecimal fxRateValue;
}
