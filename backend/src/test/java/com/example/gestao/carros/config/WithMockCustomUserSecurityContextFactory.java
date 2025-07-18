// Em src/test/java/com/example/gestao/carros/config/WithMockCustomUserSecurityContextFactory.java

package com.example.gestao.carros.config;

import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.infra.security.UserSpringDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User();
        user.setId(customUser.id());
        user.setEmail(customUser.username());

        UserSpringDetails principal = new UserSpringDetails(user);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            principal, null, principal.getAuthorities()
        );
        context.setAuthentication(token);
        return context;
    }
}