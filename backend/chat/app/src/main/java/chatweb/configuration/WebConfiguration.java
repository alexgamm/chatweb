package chatweb.configuration;

import chatweb.interceptor.ServiceAuthInterceptor;
import chatweb.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final UserAuthInterceptor userAuthInterceptor;
    private final ServiceAuthInterceptor serviceAuthInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/**")
                        .excludePathPatterns("/api/rooms");
        registry.addInterceptor(serviceAuthInterceptor)
                .addPathPatterns("/api/rooms");
    }
}
