package com.hisaige.core.config;

import com.hisaige.core.config.properties.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Api：修饰整个类，描述Controller的作用
 * @ApiOperation：描述一个类的一个方法，或者说一个接口
 * @ApiParam：单个参数描述
 * @ApiModel：用对象来接收参数
 * @ApiProperty：用对象接收参数时，描述对象的一个字段
 * @ApiResponse：HTTP响应其中1个描述
 * @ApiResponses：HTTP响应整体描述
 * @ApiIgnore：使用该注解忽略这个API
 * @ApiError ：发生错误返回的信息
 * @ApiParamImplicitL：一个请求参数
 * @ApiParamsImplicit 多个请求参数
 * @author chenyj
 * 2019/9/21 - 15:33.
 **/
@EnableConfigurationProperties({SystemProperties.class})
public class SwaggerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfiguration.class);

    private SystemProperties systemProperties;

    public SwaggerConfiguration(ObjectProvider<SystemProperties> systemProperties){

        SystemProperties ifAvailable = systemProperties.getIfAvailable();
        if(null == ifAvailable){
            //使用默认配置
            logger.info("use default swagger configuration...");
            this.systemProperties = new SystemProperties();
        } else {
            this.systemProperties = ifAvailable;
        }
    }
    /**
     * 创建API
     */
    @Bean
    public Docket createRestApi(Environment environment) {

        //Profiles profile = Profiles.of("dev", "test");
        //判断当前运行环境是否允许配置swagger
        //boolean isAccess = environment.acceptsProfiles(profile);

        /**
         * 通过自定义的@EnableSwagger注解装载配置，一旦配置装载说明启用swagger
         * @deprecated
         */
        boolean isAccess = systemProperties.getEnableSwagger2();

        return new Docket(DocumentationType.SWAGGER_2)
                // 详细定制
                .apiInfo(apiInfo())
                .enable(isAccess) //通过自定义的@EnableSwagger注解装载配置，也可以在配置文件中配置是否启用swagger
                .groupName("默认分组")
                .select()
                // 指定当前包路径
                .apis(RequestHandlerSelectors.basePackage("com.hisaige"))
                // 扫描所有 .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 添加摘要信息
     * 默认访问首页 /swagger-ui.html
     */
    private ApiInfo apiInfo()
    {
        // 用ApiInfoBuilder进行定制
        return new ApiInfoBuilder()
                .title("标题：测试系统接口文档")
                .description("描述：用于测试接口")
                .contact(new Contact("hisaige", "https://hisaige.github.io", "hisaige@163.com"))
                .version("1.0")
                .build();
    }
}
