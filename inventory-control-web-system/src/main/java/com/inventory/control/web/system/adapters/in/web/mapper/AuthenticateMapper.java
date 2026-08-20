package com.inventory.control.web.system.adapters.in.web.mapper;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.TokenUser;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthenticateMapper { 
    RegisterUser toRegisterUser(RegisterUserRequest request);
    LoginUser toLoginUser(LoginRequest request);
    TokenResponse toTokenResponse(TokenUser tokenUser);
}