package xyz.vulquery;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles web requests
 */
@Controller
public class BaseController {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

}
