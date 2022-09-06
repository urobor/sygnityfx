package pl.sygnity.fx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class FxApplication {

	public static void main(String[] args) {
		SpringApplication.run(FxApplication.class, args);
	}

}
