// Em src/test/java/com/example/gestao/carros/config/WithMockCustomUser.java

package com.example.gestao.carros.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "teste@email.com";
    int id() default 1;
}