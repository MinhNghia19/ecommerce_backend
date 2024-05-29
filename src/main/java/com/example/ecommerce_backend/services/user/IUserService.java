package com.example.ecommerce_backend.services.user;

import com.example.ecommerce_backend.dtos.UserDTO;
import com.example.ecommerce_backend.exception.DataNotFoundException;
import com.example.ecommerce_backend.models.User;
import com.example.ecommerce_backend.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    User createUser(UserDTO userDTO) throws DataNotFoundException;


    User getUserById(long id);


    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

}
