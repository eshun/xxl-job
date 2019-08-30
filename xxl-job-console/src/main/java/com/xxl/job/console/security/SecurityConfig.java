package com.xxl.job.console.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xxl.job.console.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Security拦截器
 * @author esun
 * @date: 2019-06-19
 * @version v1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 不拦截地址
     */
    String[] notFiltesUrls={"/","/**.css","/icons/**","/**.js","/**.json","/favicon.png","/favicon.ico","/auth/**","/api","/api/**","/h2-console","/h2-console/**"};

    String[] swaggerUrls={"/swagger-ui.html","/webjars/**","/v2/**","/swagger-resources","/swagger-resources/**"};

    @Autowired
    private UserServiceImpl userService;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE");
            }

            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

                Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
                while(iterator.hasNext()){
                    HttpMessageConverter<?> converter = iterator.next();
                    if(converter instanceof MappingJackson2HttpMessageConverter){
                        iterator.remove();
                    }
                }

                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

                ObjectMapper objectMapper = converter.getObjectMapper();
                /**
                 * 序列换成json时,将所有的long变成string
                 * 因为js中得数字类型不能包含所有的java long值
                 */
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
                simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
                objectMapper.registerModule(simpleModule);
                //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                //objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                //esun api 时间转换有问题注释掉

                /**
                 * 处理中文乱码问题
                 */
                List<MediaType> mediaTypes = Arrays.asList(
                        MediaType.APPLICATION_JSON_UTF8,
                        MediaType.TEXT_PLAIN,
                        MediaType.TEXT_HTML,
                        MediaType.TEXT_XML,
                        MediaType.APPLICATION_OCTET_STREAM);

                converter.setSupportedMediaTypes(mediaTypes);
                converter.setObjectMapper(objectMapper);

                converters.add(converter);
            }
        };
    }

    @Bean
    public AuthenticationFilter consoleAuthenticationFilter() {
        AuthenticationFilter authenticationFilter =new AuthenticationFilter();
        authenticationFilter.addPermitUrls(notFiltesUrls);
        authenticationFilter.addPermitUrls(swaggerUrls);
        authenticationFilter.setUserUserDetailsService(userService);

        return authenticationFilter;
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/druid/**");
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http    // 添加本地JWT security security
                .addFilterBefore(consoleAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .cors()//跨域访问
                .and()
                .csrf()
                .disable()//防止CSRF攻击
                .authorizeRequests()
                .antMatchers(swaggerUrls)
                .permitAll() //swagger 不拦截
                .antMatchers(notFiltesUrls)
                .permitAll()//登录授权等 不拦截
                .anyRequest()
                .authenticated();

    }

}
