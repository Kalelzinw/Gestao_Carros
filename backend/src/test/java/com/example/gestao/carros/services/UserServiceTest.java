package com.example.gestao.carros.services;

import com.example.gestao.carros.dto.VehicleSummaryDTO;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.entities.Vehicle;
import com.example.gestao.carros.repositories.UserRepository;
import com.example.gestao.carros.repositories.VehicleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList; 
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // --- MOCKS (Dublês/Falsos) ---
    @Mock
    private UserRepository userRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FileStorageService fileStorageService;
    
    
    @InjectMocks
    private UserService userService;

   
    private User user;
    private Vehicle vehicle;
    private final Integer userId = 1;
    private final Integer vehicleId = 10;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("Usuario Teste");
        user.setEmail("teste@email.com");
        user.setFavorites(new ArrayList<>());

        vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setModelo("Carro Teste");
    }

    @Test
    @DisplayName("Deve atualizar apenas o telefone do usuário quando outros campos são nulos")
    void updateLoggedUser_deveAtualizarApenasTelefone() {
     
        String novoTelefone = "11987654321";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    
        User updatedUser = userService.updateLoggedUser(userId, null, novoTelefone, null, null);

     
        assertNotNull(updatedUser);
        assertEquals(novoTelefone, updatedUser.getTel());
        assertEquals("teste@email.com", updatedUser.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(fileStorageService, never()).storeFile(any());
    }
    
    @Test
    @DisplayName("Deve atualizar a foto do perfil e deletar a foto antiga, se existir")
    void updateLoggedUser_deveAtualizarFotoEDeletarAntiga() {
      
        user.setFotoUrl("caminho/foto_antiga.jpg");
        MockMultipartFile novaFoto = new MockMultipartFile("foto", "nova_foto.jpg", "image/jpeg", "conteudo_da_imagem".getBytes());
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileStorageService.storeFile(novaFoto)).thenReturn("caminho_salvo/nova_foto.jpg");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

   
        userService.updateLoggedUser(userId, null, null, null, novaFoto);


        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        assertEquals("caminho_salvo/nova_foto.jpg", userCaptor.getValue().getFotoUrl());
        verify(fileStorageService, times(1)).deleteFile("caminho/foto_antiga.jpg");
        verify(fileStorageService, times(1)).storeFile(novaFoto);
    }
    
    @Test
    @DisplayName("Deve deletar o usuário e também sua foto de perfil do disco")
    void deleteLoggedUser_deveDeletarUsuarioEFoto() {
       
        user.setFotoUrl("caminho/foto_para_deletar.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
      
        userService.deleteLoggedUser(userId);
        
   
        verify(fileStorageService, times(1)).deleteFile("caminho/foto_para_deletar.jpg");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Deve adicionar um veículo aos favoritos do usuário com sucesso")
    void addVehicleToFavorites_deveFuncionar() {
     
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

      
        userService.addVehicleToFavorites(userId, vehicleId);

      
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertTrue(savedUser.getFavorites().contains(vehicle));
    }
    
    
}
