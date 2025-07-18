package com.example.gestao.carros.repositories;

import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.entities.Vehicle;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("teste@email.com");
        testUser.setName("Usuario de Teste");
        testUser.setPassword("senha123");
        testUser.setCpf("12345678900");
        testUser.setFavorites(new ArrayList<>());

        testVehicle = new Vehicle();
        testVehicle.setMarca("Ford");
        testVehicle.setModelo("Teste");
        
        entityManager.persist(testVehicle);
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo email quando ele existe")
    void findByEmail_quandoUsuarioExiste_deveRetornarUsuario() {
        Optional<User> foundUser = userRepository.findByEmail("teste@email.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Usuario de Teste");
    }

    @Test
    @DisplayName("Não deve encontrar um usuário pelo email quando ele não existe")
    void findByEmail_quandoUsuarioNaoExiste_deveRetornarVazio() {
        Optional<User> foundUser = userRepository.findByEmail("naoexiste@email.com");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Deve retornar true quando um veículo está favoritado pelo usuário")
    void isVehicleFavoritedByUser_quandoFavoritado_deveRetornarTrue() {
        testUser.getFavorites().add(testVehicle);
        entityManager.persist(testUser);
        entityManager.flush();

        boolean isFavorited = userRepository.isVehicleFavoritedByUser(testUser.getId(), testVehicle.getId());
        assertThat(isFavorited).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando um veículo não está favoritado pelo usuário")
    void isVehicleFavoritedByUser_quandoNaoFavoritado_deveRetornarFalse() {
        boolean isFavorited = userRepository.isVehicleFavoritedByUser(testUser.getId(), testVehicle.getId());
        assertThat(isFavorited).isFalse();
    }
}
