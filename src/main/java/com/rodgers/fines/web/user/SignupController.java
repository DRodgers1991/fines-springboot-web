package com.rodgers.fines.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        signup.setResult("Oh No");
        log.info("Got sign up request : {}", signup);
        return "signup";
    }

}
