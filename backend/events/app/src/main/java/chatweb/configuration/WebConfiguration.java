package chatweb.configuration;

import chatweb.interceptor.ServiceAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration extends CommonWebMvcConfigurer {
    private final ServiceAuthInterceptor serviceAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serviceAuthInterceptor)
                .addPathPatterns("/api/events")
                .addPathPatterns("/api/events/online");
    }
}
