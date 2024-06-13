package chatweb;

import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = GrpcServerFactoryAutoConfiguration.class)
public class CodenamesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodenamesApplication.class, args);
    }
}
