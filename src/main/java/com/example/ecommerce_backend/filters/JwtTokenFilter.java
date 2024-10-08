package com.example.ecommerce_backend.filters;


import com.example.ecommerce_backend.components.JwtTokenUtils;
import com.example.ecommerce_backend.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor

public class JwtTokenFilter extends OncePerRequestFilter{
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;
    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if(isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
                return;
            }
//            final String token = jwtTokenUtil.getTokenFromCookie(request);
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "authHeader null or not started with Bearer");
                return;
            }
            final String token = authHeader.substring(7);
            final String email = jwtTokenUtil.getEmail(token);
            if (email != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(email);
                if(jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
             }
            filterChain.doFilter(request, response); //enable bypass
        }catch (Exception e) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }

    }
    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(

                Pair.of(String.format("%s/products**", apiPrefix), "POST"),
                Pair.of(String.format("%s/products**", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories**", apiPrefix), "GET"),
                Pair.of(String.format("%s/subcategories**", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/auth/google/code", apiPrefix),"POST"),

                Pair.of(String.format("%s/orders**", apiPrefix), "POST"),
                Pair.of(String.format("%s/orders**", apiPrefix), "GET"),
                Pair.of(String.format("%s/order_details**", apiPrefix), "GET")
//                Pair.of(String.format("%s/users/refreshToken", apiPrefix), "POST"),

        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        for (Pair<String, String> token : bypassTokens) {
            String path = token.getFirst();
            String method = token.getSecond();
            // Check if the request path and method match any pair in the bypassTokens list
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}

//@Component
//@RequiredArgsConstructor
//public class JwtTokenFilter extends OncePerRequestFilter {
//    @Value("${api.prefix}")
//    private String apiPrefix;
//    private final UserDetailsService userDetailsService;
//    private final JwtTokenUtils jwtTokenUtil;
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            if (isBypassToken(request)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // Lấy token từ cookie
//            final String token = jwtTokenUtil.getTokenFromCookie(request);
//            if (token == null) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is missing");
//                return;
//            }
//
//            final String email = jwtTokenUtil.getEmail(token);
//
//            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                User userDetails = (User) userDetailsService.loadUserByUsername(email);
//                if (jwtTokenUtil.validateToken(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken authenticationToken =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                }
//            }
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write(e.getMessage());
//        }
//    }
//
//    private boolean isBypassToken(@NonNull HttpServletRequest request) {
//        final List<Pair<String, String>> bypassTokens = Arrays.asList(
//                Pair.of(String.format("%s/products**", apiPrefix), "POST"),
//                Pair.of(String.format("%s/products**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/categories**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/subcategories**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/details", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/google-signin", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/verify-otp", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/details**", apiPrefix), "PUT"),
//                Pair.of(String.format("%s/orders**", apiPrefix), "POST"),
//                Pair.of(String.format("%s/orders**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/order_details**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/auth**", apiPrefix), "POST")
//
//
//        );
//
//        String requestPath = request.getServletPath();
//        String requestMethod = request.getMethod();
//
//        for (Pair<String, String> token : bypassTokens) {
//            String path = token.getFirst();
//            String method = token.getSecond();
//            if (requestPath.matches(path.replace("**", ".*")) && requestMethod.equalsIgnoreCase(method)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
