package com.example.gestao.carros.controllers;

import com.example.gestao.carros.config.WithMockCustomUser; // <-- IMPORTE A NOVA ANOTAÇÃO
import com.example.gestao.carros.dto.UserResponseDTO;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.infra.security.CustomUserDetailsService;
import com.example.gestao.carros.infra.security.TokenService;
import com.example.gestao.carros.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Usuario Teste");
        testUser.setEmail("teste@email.com");
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o perfil do usuário logado")
    @WithMockCustomUser // <-- MUDANÇA AQUI
    void getUserProfile_deveRetornarPerfil() throws Exception {
        given(userService.getLoggedUserProfile()).willReturn(testUser);
       
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk()) 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1)) 
                .andExpect(jsonPath("$.name").value("Usuario Teste")); 
    }
    
    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar os dados do usuário")
    @WithMockCustomUser // <-- MUDANÇA AQUI
    void updateUser_comDadosValidos_deveRetornarOk() throws Exception {
        given(userService.updateLoggedUser(anyInt(), any(), any(), any(), any())).willReturn(testUser);

        mockMvc.perform(patch("/user/update")
                        .param("tel", "11888887777") 
                        .with(csrf())) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuario Teste"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar a foto do perfil do usuário")
    @WithMockCustomUser // <-- MUDANÇA AQUI
    void updateProfilePicture_comArquivoValido_deveRetornarOk() throws Exception {
        MockMultipartFile pictureFile = new MockMultipartFile("foto", "novafoto.jpg", "image/jpeg", "conteudo".getBytes());
        given(userService.updateLoggedUser(anyInt(), any(), any(), any(), any())).willReturn(testUser);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/user/update")
                        .file(pictureFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar o usuário logado")
    @WithMockCustomUser // <-- MUDANÇA AQUI
    void deleteUser_deveRetornarNoContent() throws Exception {
        doNothing().when(userService).deleteLoggedUser(anyInt());

        mockMvc.perform(delete("/user/delete")
                        .with(csrf())) 
                .andExpect(status().isNoContent()); 
        
        verify(userService, times(1)).deleteLoggedUser(anyInt());
    }
}