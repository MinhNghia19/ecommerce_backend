package com.example.ecommerce_backend.services.token;



public interface ITokenService {

    String refreshToken(String email, String password, Long roleId) throws Exception;
}
