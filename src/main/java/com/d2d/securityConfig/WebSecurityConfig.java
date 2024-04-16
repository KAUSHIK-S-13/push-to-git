package com.d2d.securityConfig;

import com.d2d.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
 public class WebSecurityConfig extends  WebSecurityConfigurerAdapter{

		@Autowired
		private JwtUserDetailsService jwtUserDetailsService;
		@Autowired
		private JwtRequestFilter jwtRequestFilter;
		@Autowired
		private JwtAuthenticationEntryPoint entryPoint;
		@Autowired
		public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception
		{
		auth.userDetailsService(jwtUserDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
}

		@Bean
		@Override
		 public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
}

		@Override
		protected void configure(HttpSecurity httpSecurity) throws Exception {
		        httpSecurity.csrf().disable()
		/*
                 * don't authenticate this particular request
                 */
			.authorizeRequests().antMatchers(Constant.LOGIN,
                        Constant.REFRESH,Constant.USER_SIGNUP,Constant.GOOGLE_SIGNUP,Constant.TEST_1,
								"/actuator/health"
								,"/swagger-resources/**",
								"/swagger-ui/**",
								"/v2/api-docs/**",
								"/v3/api-docs/**",
								"/webjars/**")
                .permitAll().
                /*
                 * all other requests need to be authenticated
                 */
                anyRequest().authenticated().and().
                /*
                 * make sure we use state less session; session won't be used to store user's
                 * state
                 */
                exceptionHandling().authenticationEntryPoint(entryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        httpSecurity.cors();

}



      @Override
	  public void configure(WebSecurity web) throws Exception {
		    	web.ignoring().antMatchers(
						Constant.LOGIN,
						Constant.REFRESH,
						"/swagger*/**",
						"/configuration/**",
						"/v2/api-docs",
						"/v3/api-docs/**");
		}
}

