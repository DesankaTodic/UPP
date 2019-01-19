package ftn.UppProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@SpringBootApplication
@Configuration
@EnableWebSecurity
public class UppProjectApplication extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/registration").permitAll().antMatchers("/choosingCategory").permitAll().antMatchers("/submitRegistration").permitAll()
				.antMatchers("/validationDailed").permitAll().antMatchers("/verificationPage").permitAll()
				.antMatchers("/verificationFailedPage").permitAll().antMatchers("/mailNotification").permitAll()
				.antMatchers("/fillOffer").permitAll().antMatchers("/addingMoreInfo").permitAll()
				.antMatchers("/ratingClient").permitAll().anyRequest().authenticated().and().formLogin()
				.successHandler((req, res, auth) -> { // Success handler invoked after successful authentication
					for (GrantedAuthority authority : auth.getAuthorities()) {
						System.out.println(authority.getAuthority());
					}
					System.out.println(auth.getName());
					res.sendRedirect("/submitLogin"); // Redirect user to index/home page
				})

				.permitAll().and().logout().permitAll().and().csrf().disable();
	}

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		return resolver;
	}

	public static void main(String[] args) {
		SpringApplication.run(UppProjectApplication.class, args);
	}
}
