package chatweb.response;

import com.github.jknack.handlebars.Handlebars;
import webserver.RequestFailedException;
import webserver.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TemplateResponse extends Response {

    public static class Builder {
        private final Handlebars handlebars;
        private final String templateName;
        private final Map<String, Object> context = new HashMap<>();
        private int statusCode = 200;

        public Builder(Handlebars handlebars, String templateName) {
            this.handlebars = handlebars;
            this.templateName = templateName;
        }

        public Builder addToContext(String key, Object value) {
            context.put(key, value);
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public TemplateResponse build() throws RequestFailedException {
            try {
                return new TemplateResponse(
                        Collections.emptyMap(),
                        statusCode,
                        handlebars.compile(templateName).apply(context)
                );
            } catch (IOException e) {
                throw new RequestFailedException(500, e.getMessage());
            }
        }
    }

    public TemplateResponse(Map<String, String> headers, int statusCode, String body) {
        super(headers, statusCode, body);
    }
}
