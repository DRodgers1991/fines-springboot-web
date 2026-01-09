package com.rodgers.fines.web.user;

import com.rodgers.fines.web.webclient.HttpHelper;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Got sign up request : {}", signup);
        HttpResponse<String> response = HttpHelper.createUser(signup);
        log.info("Response from creation service | {}:{}",response.statusCode(),response.body());
        signup.setResult(response.body());
        return "signup";
    }

}
