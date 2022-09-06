package pl.sygnity.fx;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({FxRatesRestController.class})
public class CustomerControllerLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FxRatesService fxRatesService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    FxRatesRepository fxRatesRepository;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void save_customer_success() throws Exception {

        FxRate rate = new FxRate(LocalDate.of(2012, 01, 02),
                "USD", BigDecimal.valueOf(3.5));

        FxRate rate2 = new FxRate(LocalDate.of(2012, 01, 02),
                "GBP", BigDecimal.valueOf(5));

        when(fxRatesRepository.findByCodeAndDate("USD", LocalDate.of(2012, 01, 01)))
                .thenReturn(rate);
        when(fxRatesRepository.findByCodeAndDate("GBP", LocalDate.of(2012, 01, 01)))
                        .thenReturn(rate2);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/fxrates/gbp/usd/2012-01-01/1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }
}