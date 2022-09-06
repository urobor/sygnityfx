package pl.sygnity.fx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FxRatesApplicationConfiguration {

    @Bean
    public WebClient nbpRestClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("https://api.nbp.pl")
                .build();
    }
}
