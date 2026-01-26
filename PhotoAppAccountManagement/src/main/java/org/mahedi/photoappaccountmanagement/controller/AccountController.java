package org.mahedi.photoappaccountmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @GetMapping("/status/check")
    public String checkStatus(){
        return "Account Management status working!";
    }

    // Reset Password
}
