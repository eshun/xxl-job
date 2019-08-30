package com.xxl.job.console.core.swagger2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-06-27
 */
@Data
@ConfigurationProperties(prefix = "app.swagger")
public class SwaggerProperties {
    public SwaggerProperties(){
        this.enable=false;
        basePackage="";
        title="";
        description="";
        termsOfServiceUrl="";
        contact="";
        version="";
        excludePath="";
    }

    private boolean enable;

    private String basePackage;

    private String title;

    private String description;

    private String termsOfServiceUrl;

    private String contact;

    private String version;

    private String excludePath;

    public String[] getExcludePaths() {
        return excludePath.split(",");
    }

}
