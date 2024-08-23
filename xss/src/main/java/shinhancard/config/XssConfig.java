package shinhancard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shinhancard.filter.XssFilter;
import shinhancard.properties.XssProperties;

@Configuration
public class XssConfig {

	@Bean
	public XssFilter xssFilter(XssProperties xssProperties) {
		return new XssFilter(xssProperties);
	}
}
