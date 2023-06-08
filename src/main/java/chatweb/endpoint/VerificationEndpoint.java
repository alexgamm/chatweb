package chatweb.endpoint;

import chatweb.repository.VerificationRepository;
import chatweb.response.TemplateResponse;
import chatweb.service.VerificationService;
import com.github.jknack.handlebars.Handlebars;
import webserver.Endpoint;
import webserver.Request;
import webserver.RequestFailedException;
import webserver.Response;

import java.util.Collections;

public class VerificationEndpoint implements Endpoint {
    private final VerificationRepository verificationRepository;
    private final VerificationService verificationService;
    private final Handlebars handlebars;

    public VerificationEndpoint(VerificationRepository verificationRepository, VerificationService verificationService, Handlebars handlebars) {
        this.verificationRepository = verificationRepository;
        this.verificationService = verificationService;
        this.handlebars = handlebars;
    }

    public Object get(Request request) throws RequestFailedException {
        return new TemplateResponse.Builder(handlebars, "verification").build();
    }

    @Override
    public Object post(Request request) throws RequestFailedException {
        String code = request.getBody().get("otp");
        verificationRepository.findVerification()
        return Response.redirect(
                "/index"
                //  Collections.singletonMap("Set-Cookie", "sessionId=" + session.getId())
        );
    }
}
