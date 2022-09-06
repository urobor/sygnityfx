package pl.sygnity.fx;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class FxApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	FxRatesService fxRatesService;

	@Test
	void testWeekend() throws Exception {
		given(this.fxRatesService.calculateFxRate("usd", "gbp", LocalDate.of(2012, 01, 01), BigDecimal.ONE)).willThrow(new ResponseStatusException(BAD_REQUEST, "Only working days"));
		mockMvc.perform(MockMvcRequestBuilders
						.get("/api/v1/fxrates/usd/gbp/2012-01-01/1"))
				.andExpect(status().is4xxClientError())
				.andExpect(status().reason("Only working days"));
		Mockito.verify(fxRatesService).calculateFxRate("usd", "gbp", LocalDate.of(2012, 01, 01), BigDecimal.ONE);
	}

	@Test
	void testWeek() throws Exception {
		given(this.fxRatesService.calculateFxRate("usd", "gbp", LocalDate.of(2012, 01, 02), BigDecimal.ONE)).willReturn(BigDecimal.TEN);
		mockMvc.perform(MockMvcRequestBuilders
						.get("/api/v1/fxrates/usd/gbp/2012-01-02/1"))
				.andExpect(status().isOk())
				.andExpect(content().string("10"));
		Mockito.verify(fxRatesService).calculateFxRate("usd", "gbp", LocalDate.of(2012, 01, 02), BigDecimal.ONE);
	}

	@Test
	void contextLoads() {
	}

}
