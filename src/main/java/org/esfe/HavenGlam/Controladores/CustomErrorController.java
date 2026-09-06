package org.esfe.HavenGlam.Controladores;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String manejarError(HttpServletRequest request, Model model) {

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = (statusObj != null) ? Integer.parseInt(statusObj.toString()) : 500;

        switch (status) {
            case 400:
                return "error/400";
            case 401:
                return "error/401";
            case 403:
                return "error/403";
            case 404:
                return "error/404";
            default:
                return "error/500";
        }
    }
}