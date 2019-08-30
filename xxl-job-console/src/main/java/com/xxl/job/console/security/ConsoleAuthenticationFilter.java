package com.xxl.job.console.security;

import com.xxl.job.console.config.XxlJobConsoleConfig;
import com.xxl.job.console.core.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * console登录授权校验
 * @author esun
 * @date: 2019-06-19
 * @version v1.0
 */
public class ConsoleAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleAuthenticationFilter.class);

    /**
     * 允许 不拦截url
     */
    private Set<String> permitUrls = new LinkedHashSet();

    public void addPermitUrls(String... urlPatterns) {
        Collections.addAll(this.permitUrls, urlPatterns);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        //return permitUrls.stream().anyMatch(p -> pathMatcher.match(p, request.getServletPath()));

        try{
            String[] excludeUrlPatterns = StringUtils.toStringArray(this.permitUrls);
            return PatternMatchUtils.simpleMatch(excludeUrlPatterns, request.getServletPath());
        }catch (Exception e){
            logger.error("服务发生未知异常", e);
        }
        return false;
    }

    private UserDetailsService defaultUserDetailsService;

    public void setUserUserDetailsService(UserDetailsService userUserDetailsService){
        defaultUserDetailsService=userUserDetailsService;
    }


    @Autowired
    XxlJobConsoleConfig consoleConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(httpServletRequest);
            setLocale(httpServletRequest);

            if (StringUtils.hasText(jwt) && JwtTokenUtil.validateToken(jwt, consoleConfig.getJwtSecret())) {
                String userName = JwtTokenUtil.getSubjectFromJWT(jwt, consoleConfig.getJwtSecret());

                /*
                    Note that you could also encode the user's username and roles inside JWT claims
                    and create the UserDetails object by parsing those claims from the JWT.
                    That would avoid the following database hit. It's completely up to you.
                 */
                UserDetails userDetails = defaultUserDetailsService.loadUserByUsername(userName);
                if (userDetails != null && userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
//                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
//                            "authorized未授权");
                    SecurityContextHolder.clearContext();
                }
            } else {
//                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
//                        "authorized已经过期 authorized验证失败");
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            logger.error("服务发生未知异常", e);
//            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//                    "服务发生未知异常");
        }finally {
            LocaleContextHolder.resetLocaleContext();
        }
//        httpServletResponse.setContentType("application/json; charset=UTF-8");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * 通过请求头部判断token
     * @param request
     * @return
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") && bearerToken.length() > 7) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    /**
     * 国际化支持
     * @param request
     */
    private void setLocale(HttpServletRequest request){
        String newLocale = request.getHeader("locale");
        if (newLocale != null) {
            Locale locale = StringUtils.parseLocaleString(newLocale.toLowerCase());
            LocaleContextHolder.setLocale(locale);
        }
    }
}
