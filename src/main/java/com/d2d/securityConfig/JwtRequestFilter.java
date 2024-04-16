package com.d2d.securityConfig;

import com.d2d.constant.Constant;
import com.d2d.entity.Users;
import com.d2d.exception.ErrorCode;
import com.d2d.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JWTUtils jwtTokenUtil;

    @Value("${google.key}")
    private String googleKey;

    public static final ThreadLocal<Long> userId = new ThreadLocal<>();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (!checker(request)) {
                final String requestTokenHeader = request.getHeader("Authorization");
                final String provider = request.getHeader("Provider");

                String token = requestTokenHeader.replace("Bearer ", "");

                if (provider!=null && provider.equalsIgnoreCase("google")) {
                    HttpTransport transport = new NetHttpTransport();

                    JsonFactory jsonFactory = new GsonFactory();

                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                            .setAudience(Collections.singletonList(googleKey))
                            .build();
                    GoogleIdToken idToken = verifier.verify(token);
                    GoogleIdToken.Payload payload = idToken.getPayload();
                    String email = payload.getEmail();
                    final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    Optional<Users> users=usersRepository.findByEmail(email);
                    users.ifPresent(value -> userId.set(value.getId()));

                } else {

                    String username = null;
                    String jwtToken = null;

                    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                        jwtToken = requestTokenHeader.replace("Bearer ", "");
                        try {

                            username = jwtTokenUtil.getEmail(jwtToken);
                            Optional<Users> users=usersRepository.findByEmail(username);
                            users.ifPresent(value -> userId.set(value.getId()));
                        } catch (IllegalArgumentException e) {
                            logger.warn("Unable to get JWT Token..!");
                            response.setStatus(401);
                            throw new ServletException(ErrorCode.D2D_20.getMessage());
                        } catch (ExpiredJwtException e) {
                            logger.warn("JWT Token has expired");
                            response.setStatus(401);
                            throw new ServletException(ErrorCode.D2D_21.getMessage());
                        }
                    } else {
                        response.setStatus(401);
                        logger.warn("JWT Token does not begin with Bearer String");
                        throw new ServletException(ErrorCode.D2D_22.getMessage());

                    }

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

                        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = jwtTokenUtil
                                    .getAuthentication(jwtToken, SecurityContextHolder.getContext().getAuthentication(),
                                            userDetails);
                            usernamePasswordAuthenticationToken
                                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        }
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ServletException servletException) {
            Map<String, Object> errorResponse = new HashMap<>( );
            errorResponse.put("statusCode", response.getStatus( ));
            errorResponse.put("statusMessage", servletException.getMessage( ));
            response.getWriter( ).write(new ObjectMapper( ).writeValueAsString(errorResponse));
        } catch (Exception exception) {
            response.setStatus(401);
            Map<String, Object> exceptionResponse = new HashMap<>( );
            exceptionResponse.put("statusCode", response.getStatus( ));
            exceptionResponse.put("statusMessage", exception.getMessage( ));
            response.getWriter( ).write(new ObjectMapper( ).writeValueAsString(exceptionResponse));
        }
    }

    private boolean checker(HttpServletRequest request) throws GeneralSecurityException, IOException {
        if (request.getRequestURI( ).startsWith("/actuator/health")) {
            return true;
        }
        if (request.getRequestURI( ).equalsIgnoreCase("/v3/api-docs/**")) {
            return true;
        }
        if (request.getRequestURI( ).equalsIgnoreCase("/user/save")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/v3/api-docs")) {
            return true;
        }
        if (request.getRequestURI( ).equalsIgnoreCase("/v3/api-docs/swagger-config")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/configuration")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/index.html")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/swagger-ui.css")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/index.css")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/swagger-initializer.js")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/swagger-ui-bundle.js")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/swagger-ui-standalone-preset.js")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/favicon-32x32.png")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/swagger-ui/favicon-16x16.png")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith(Constant.USER_SIGNUP)) {
            return true;
        }
        if (request.getRequestURI( ).startsWith(Constant.TEST_1)) {
            return true;
        }

        if (request.getRequestURI( ).startsWith(Constant.GOOGLE_SIGNUP)) {
            return true;
        }
        if (request.getRequestURI( ).startsWith("/favicon.ico")) {
            return true;
        }
        if (request.getRequestURI( ).startsWith(Constant.LOGIN)) {
            return true;
        }
        return request.getRequestURI().startsWith(Constant.REFRESH);
    }


}
