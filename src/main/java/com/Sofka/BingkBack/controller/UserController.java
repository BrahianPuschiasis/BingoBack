package com.Sofka.BingkBack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController
    public class UserController {

        @GetMapping("/user/name")
        public String getUserName(Authentication authentication) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String userName = token.getPrincipal().getAttribute("preferred_username");
            return "Usuario logueado: " + userName;
        }
    }


