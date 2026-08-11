package com.auth.infrastructure.decorator;

import com.auth.ports.in.RegisterUserUseCase;
import org.springframework.transaction.annotation.Transactional;

import com.auth.domain.model.User;

public class TransactionalRegisterUserDecorator implements RegisterUserUseCase {

    private final RegisterUserUseCase delegate;

    public TransactionalRegisterUserDecorator(RegisterUserUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public void execute(User user) {
        this.delegate.execute(user);
    }
}
