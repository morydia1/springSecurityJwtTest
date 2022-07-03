package sn.esp.authservice.sec.filters;

import org.springframework.web.filter.OncePerRequestFilter;
import sn.esp.authservice.sec.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationToken = request.getHeader(JwtUtil.AUTH_HEADER);
        if(request.getServletPath().equals("/refreshToken"))
            filterChain.doFilter(request,response);

        else JwtUtil.doFilterForAccess(authorizationToken,request,response,filterChain);
    }
}
