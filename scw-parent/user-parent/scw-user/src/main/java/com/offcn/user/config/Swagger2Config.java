package com.offcn.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean("用户模块")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())  /*添加基本信息*/
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.offcn.user.controller"))
                .paths(PathSelectors.any())  /*对controller中的每一个访问路径都会生成一个说明文档*/
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("七易众筹-系统平台接口文档")
                .description("提供用户模块的文档")
                .termsOfServiceUrl("http://www.ujiuye.com/")
                .version("1.0")
                .build();
    }

}
