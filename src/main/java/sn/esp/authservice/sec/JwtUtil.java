package sn.esp.authservice.sec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import sn.esp.authservice.sec.entities.AppUser;
import sn.esp.authservice.sec.service.AccountService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {
    public static final String SECRET ="mySecret123";
    public static final String AUTH_HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";
    public static final String ERROR_MESSAGE = "error-message";

    public static final long EXPIRE_ACCESS_TOKEN = 2*60*1000;
    public static final long EXPIRE_REFRESH_TOKEN = 15*60*1000;

    public static void doFilterForAccess(String authorizationToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        if(authorizationToken != null && authorizationToken.startsWith(JwtUtil.PREFIX)){
            try{
                String jwt = authorizationToken.substring(JwtUtil.PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(JwtUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String username = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                for(String r: roles){
                    authorities.add(new SimpleGrantedAuthority(r));
                }
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username,null,authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                filterChain.doFilter(request,response);

            }catch(Exception e){
                response.setHeader(JwtUtil.ERROR_MESSAGE,e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }

        }else{
            filterChain.doFilter(request,response);
        }
    }

    public static void doFilterForRefresh(String authorizationToken,AccountService accountService ,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException,Exception{
        if(authorizationToken != null && authorizationToken.startsWith(JwtUtil.PREFIX)){
            try{
                String jwt = authorizationToken.substring(JwtUtil.PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(JwtUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String username = decodedJWT.getSubject();
                AppUser appUser = accountService.loadUserByUsername(username);
                String jwtAccesToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + JwtUtil.EXPIRE_REFRESH_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",appUser.getAppRoles().stream().map(r->r.getRoleName()).collect(Collectors.toList()))
                        .sign(algorithm);

                JwtUtil.sendTokens(jwtAccesToken,jwt,response);

            }catch(Exception e){
                response.setHeader(JwtUtil.ERROR_MESSAGE,e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }

        }else{
            throw new RuntimeException("Refresh Token required");
        }
    }


    public static void createTokens(User user, HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException {
        Algorithm algorithm = Algorithm.HMAC256(JwtUtil.SECRET);

        String jwtAccesToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtUtil.EXPIRE_ACCESS_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
                .sign(algorithm);

        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtUtil.EXPIRE_REFRESH_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        JwtUtil.sendTokens(jwtAccesToken,jwtRefreshToken,response);
    }

    public static void sendTokens(String JwtAccessToken, String JwtRefreshToken, HttpServletResponse response) throws IOException {
        Map<String,String> idToken = new HashMap<>();
        idToken.put("AccesToken",JwtAccessToken);
        idToken.put("RefreshToken",JwtRefreshToken);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),idToken);
    }
}
