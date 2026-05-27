package com.kode_project.ebooking.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kode_project.ebooking.dto.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String jwtSecret;
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret) {
        this.authenticationManager = authenticationManager;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,HttpServletResponse response)
            throws AuthenticationException{
        LoginRequestDto user = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.motDePasse()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {
        org.springframework.security.core.userdetails.User userSpring =
                (org.springframework.security.core.userdetails.User) authResult.getPrincipal();

        List<String> roles = new ArrayList<>();
        assert userSpring != null;
        userSpring.getAuthorities().forEach(authority ->
            roles.add(authority.getAuthority())
        );

        String jwt = JWT.create()
                .withSubject(userSpring.getUsername())
                .withClaim("role", roles.getFirst())
                .withExpiresAt(new Date(System.currentTimeMillis()+ SecParams.EXP_TIME))
                .sign(Algorithm.HMAC256(jwtSecret));

        // Configuration de la réponse JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Construction de l'objet de réponse aligné sur l'interface Angular
        Map<String, Object> body = new HashMap<>();
        body.put("token", SecParams.BEARER + jwt); // Inclus "Bearer "
        body.put("email", userSpring.getUsername());
        body.put("type", "Bearer");
        body.put("roles", Collections.singletonList(roles.getFirst()));

        // Écriture du JSON dans le body
        String json = new ObjectMapper().writeValueAsString(body);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        Map<String, Object> data = new HashMap<>();

        if (failed instanceof DisabledException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            data.put("errorCause", "disabled");
            data.put("message", "L'utilisateur est désactivé !");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            data.put("errorCause", "bad_credentials");
            data.put("message", "Email ou mot de passe incorrect.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);
        PrintWriter writer = response.getWriter();
        writer.println(json);
        writer.flush();
    }
}
