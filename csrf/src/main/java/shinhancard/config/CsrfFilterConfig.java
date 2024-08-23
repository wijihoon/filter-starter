package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shinhancard.filter.CsrfFilter;
import shinhancard.properties.CsrfProperties;

@Configuration
public class CsrfFilterConfig {

	@Bean
	public CsrfFilter csrfFilter(CsrfProperties csrfProperties) {
		return new CsrfFilter(csrfProperties);
	}
}
