package com.example.demo.Controller.Web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {
    /**
     * Страница аутентификации.
     * Отдаёт шаблон src/main/resources/templates/auth.html
     * В нём подключён auth.js, который отправит fetch-запросы к /api/auth/register и /api/auth/login.
     */
    @GetMapping("/auth")
    public String authPage() {
        return "auth";
    }

    /**
     * Главная страница после логина.
     * Отдаёт шаблон src/main/resources/templates/home.html.
     * Предполагаем, что на ней серверным Thymeleaf’ом уже заполнен список книг.
     */
    @GetMapping("/home")
    public String home(@RequestParam(name = "token", required = false) String token,
                       HttpServletResponse response) {
        return "home";
    }

    /*(опционально) Страница приветствия для гостей*/
    @GetMapping({"/", "/hello", "/welcome"})
    public String welcomePage() {
        return "hello";
    }

    @GetMapping("/history")
    public String history() {
        return "history";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }
}

