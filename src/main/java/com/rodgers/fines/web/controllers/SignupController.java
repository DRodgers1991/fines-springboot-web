package com.rodgers.fines.web.controllers;

import com.rodgers.fines.web.vo.SignUpVo;
import com.rodgers.fines.web.webclient.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.http.HttpResponse;

@Controller
@Slf4j
public class SignupController {

    public static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signup", new SignUpVo());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute SignUpVo signup, Model model) {
        model.addAttribute("signup", signup);
        signup.setPassword(ENCODER.encode(signup.getPassword()));
        log.info("Got sign up request for username : {}", signup.getUserName());
        HttpResponse<String> response = HttpHelper.createUser(signup);
        log.info("Response from creation service | {}:{}",response.statusCode(),response.body());
        setResult(signup, response);
        return "signup";
    }

    private static void setResult(SignUpVo signup, HttpResponse<String> response) {
        if(response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.warn("Internal error requesting user creation");
            signup.setResult("Sorry something has gone wrong, please wait and try again later");
        } else if(response.statusCode() == HttpStatus.BAD_REQUEST.value()) {
            log.warn("Bad User Request: {}", response.body());
            signup.setResult(response.body().split(":")[1].replace("}",""));
        } else if(response.statusCode() == HttpStatus.OK.value()) {
            log.info("Successful User creation");
            signup.setResult("User created successfully, please proceed to Login");
        } else {
            signup.setResult(response.body());
        }
    }
}
