package chatweb.controller;

import chatweb.exception.UnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    @GetMapping
    public String index() {
        return "index";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized() {
        return "redirect:/login";
    }

}
