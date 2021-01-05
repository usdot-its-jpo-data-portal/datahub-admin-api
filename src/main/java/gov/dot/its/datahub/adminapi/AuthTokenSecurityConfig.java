package gov.dot.its.datahub.adminapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.util.StringUtils;

import gov.dot.its.datahub.adminapi.utils.ApiUtils;

@Configuration
@EnableWebSecurity
@Order(1)
@ComponentScan("gov.dot.its.datahub.adminapi.utils")
public class AuthTokenSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${datahub.admin.api.security.token.name}")
	private String tokenName;

	@Value("${datahub.admin.api.security.token.key}")
	private String tokenKey;

	@Autowired
	private ApiUtils apiUtils;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (StringUtils.isEmpty(tokenKey)) {
			tokenKey = apiUtils.getMd5(apiUtils.getUUID()).toUpperCase();
		}
		PreAuthTokenHeaderFilter filter = new PreAuthTokenHeaderFilter(tokenName);
		filter.setAuthenticationManager(new AuthenticationManager() {
			@Override
			public Authentication authenticate(Authentication authentication)  {
				String principal = (String) authentication.getPrincipal();
				if (!tokenKey.equals(principal)) {
					throw new BadCredentialsException("Invalid Token.");
				}
				authentication.setAuthenticated(true);
				return authentication;
			}
		});

		http.antMatcher("/v?/**").csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilter(filter)
				.addFilterBefore(new ExceptionTranslationFilter(new Http403ForbiddenEntryPoint()), filter.getClass())
				.authorizeRequests().anyRequest().authenticated();

	}

}
