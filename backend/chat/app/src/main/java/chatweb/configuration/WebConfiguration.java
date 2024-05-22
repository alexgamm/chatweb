package chatweb.configuration;

import chatweb.interceptor.ServiceAuthInterceptor;
import chatweb.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration extends CommonWebMvcConfigurer {
    private final UserAuthInterceptor userAuthInterceptor;
    private final ServiceAuthInterceptor serviceAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/rooms")
                .excludePathPatterns("/api/service/**");
        registry.addInterceptor(serviceAuthInterceptor)
                .addPathPatterns("/api/rooms")
                .addPathPatterns("/api/service/**");
    }
}
