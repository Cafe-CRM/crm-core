package com.cafe.crm.security.configs;


import com.cafe.crm.security.filters.ReCaptchaFilter;
import com.cafe.crm.security.handlers.CustomAuthenticationFailureHandler;
import com.cafe.crm.security.handlers.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Autowired
	public SecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
						  CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
						  CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
	}

	@Bean
	public static ServletListenerRegistrationBean httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
	}

	@Bean
	public ReCaptchaFilter reCaptchaFilter() throws Exception {
		ReCaptchaFilter reCaptchaFilter = new ReCaptchaFilter();
		reCaptchaFilter.setFilterProcessesUrl("/processing-url");
		reCaptchaFilter.setAuthenticationManager(authenticationManager());
		reCaptchaFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
		reCaptchaFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
		reCaptchaFilter.setSessionAuthenticationStrategy(new RegisterSessionAuthenticationStrategy(sessionRegistry()));
		return reCaptchaFilter;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		ReCaptchaFilter reCaptchaFilter = reCaptchaFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		http
				.authorizeRequests()
				.antMatchers("/manager/**").hasAnyAuthority("MANAGER", "BOSS")
				.antMatchers("/boss/**", "/worker/**", "/advertising/**").hasAuthority("BOSS")
				.antMatchers("/supervisor/**").hasAuthority("SUPERVISOR")
				.and()
				.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/processing-url")
				.successHandler(customAuthenticationSuccessHandler)
				.failureHandler(customAuthenticationFailureHandler)
				.usernameParameter("username")
				.passwordParameter("password")
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true)
				.permitAll()
				.and()
				.csrf().disable()
				.addFilterBefore(characterEncodingFilter, CsrfFilter.class)
				.addFilterBefore(reCaptchaFilter, UsernamePasswordAuthenticationFilter.class);

		http
				.sessionManagement()
				.maximumSessions(100)
				.maxSessionsPreventsLogin(false)
				.expiredUrl("/login?logout")
				.sessionRegistry(sessionRegistry());
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		SessionRegistry sessionRegistry = new SessionRegistryImpl();
		return sessionRegistry;
	}
}