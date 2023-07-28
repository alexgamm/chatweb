package chatweb.configuration;

import chatweb.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/registration")
                .excludePathPatterns("/api/verification")
                .excludePathPatterns("/registration")
                .excludePathPatterns("/google/oauth/**")
                .excludePathPatterns("/api/google/oauth/**")
                .excludePathPatterns("/verification");
    }
}
