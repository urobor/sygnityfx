package pl.sygnity.fx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FxRatesRepository extends JpaRepository<FxRate, Long> {
    FxRate findByCodeAndDate(String code, LocalDate date);
}
