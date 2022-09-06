package pl.sygnity.fx;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class FxApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FxRatesRestController fxRatesRestController;

	@MockBean
	FxRatesService fxRatesService;

	@Before
	public void setup() {
		Mockito.when(fxRatesService.calculateFxRate("usd", "gbp", LocalDate.of(2012, 01, 01), BigDecimal.ONE)).thenReturn(BigDecimal.TEN);
	}

	@Test
	void testWeekend() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
						.get("/api/v1/fxrates/gbp/usd/2012-01-01/1"))
				.andDo(print());
	}

	@Test
	void contextLoads() {
	}

}
