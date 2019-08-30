package com.xxl.job.console.core.swagger2;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger接口api
 * @author esun
 * @date 2019/6/5
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties({SwaggerProperties.class})
@ConditionalOnProperty(prefix = "app.swagger", name = "enable", havingValue = "true")
@Import({Swagger2DocumentationConfiguration.class})
public class SwaggerAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);
    private SwaggerProperties swaggerProperties;

    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
        logger.info("swagger2 [{}]", swaggerProperties);
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(setToken())
                .apiInfo(apiInfo())
                .enable(swaggerProperties.isEnable())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(paths())
                .build();
    }


    private Predicate<String> paths() {

        List<Predicate<String>> basePaths = new ArrayList<>();
        basePaths.add(PathSelectors.any());

        List<Predicate<String>> excludePaths = new ArrayList<>();
        for (String excludePath : swaggerProperties.getExcludePaths()) {
            excludePaths.add(PathSelectors.ant(excludePath));
        }

        return Predicates.and(
                Predicates.not(
                        Predicates.or(excludePaths)
                ),
                Predicates.or(basePaths)
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .contact(new Contact(swaggerProperties.getContact(), "", ""))
                .version(swaggerProperties.getVersion())
                .build();
    }

    private List<Parameter> setToken() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("Authorization")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .description("登录令牌")
                .defaultValue("Bearer ")
                .required(false)
                .build();
        List<Parameter> pars = new ArrayList<>();
        pars.add(tokenPar.build());
        return pars;
    }
}
