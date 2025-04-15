package com.Link.Linkalyz.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try{
            //Get JWT from header

            String jwt=jwtTokenProvider.getJwtFromHeader(request);
            if(jwt!=null && jwtTokenProvider.validateToken(jwt)){
                String username=jwtTokenProvider.getUserNameFromJwtToken(jwt);
                UserDetails userDetails=userDetailsService.loadUserByUsername(username);
                if(userDetails!=null){
                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            //Validate Token

            //If valid , get userDetails -> Load user -> Set auth context
        } catch (Exception e){
    e.printStackTrace();
        }

        filterChain.doFilter(request,response); //be sure to add this so that filter flow is not affected
    }
}
