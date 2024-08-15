package chatweb;

import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = GrpcServerFactoryAutoConfiguration.class)
public class ChatGPTApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGPTApplication.class, args);
    }
}
