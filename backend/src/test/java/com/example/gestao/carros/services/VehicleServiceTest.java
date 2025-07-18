package com.example.gestao.carros.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;

import com.example.gestao.carros.dto.VehicleCreateDTO;
import com.example.gestao.carros.dto.VehicleResponseDTO;
import com.example.gestao.carros.entities.Document;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.entities.Vehicle;
import com.example.gestao.carros.repositories.DocumentRepository;
import com.example.gestao.carros.repositories.UserRepository;
import com.example.gestao.carros.repositories.VehicleRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

  
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private FileStorageService fileStorageService;

   
    @InjectMocks
    private VehicleService vehicleService;

   
    private User user;
    private VehicleCreateDTO createDTO;
    private MockMultipartFile imageFile;
    private MockMultipartFile pdfFile;

    @BeforeEach
    void setUp() {
       
        user = new User();
        user.setId(1);
        user.setName("Usuario Dono");

   
        createDTO = new VehicleCreateDTO();
        createDTO.setMarca("Ford");
        createDTO.setModelo("Ka");
        createDTO.setUserId(1);

     
        imageFile = new MockMultipartFile("imagem", "carro.jpg", "image/jpeg", "conteudo".getBytes());
        pdfFile = new MockMultipartFile("documento", "doc.pdf", "application/pdf", "conteudo".getBytes());
    }

    @Test
    @DisplayName("Deve criar um veículo com sucesso, salvando a imagem e o documento")
    void createVehicleWithFiles_deveFuncionar() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(fileStorageService.storeFile(imageFile)).thenReturn("imagem_salva.jpg");
        when(fileStorageService.storeFile(pdfFile)).thenReturn("documento_salvo.pdf");

        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

   
        VehicleResponseDTO result = vehicleService.createVehicleWithFiles(createDTO, pdfFile, imageFile);

      
        assertNotNull(result);
        assertEquals("Ford", result.getMarca());
        assertEquals("imagem_salva.jpg", result.getImageUrl());
     
        verify(documentRepository, times(1)).save(any(Document.class));

        verify(fileStorageService, times(2)).storeFile(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar veículo para um usuário inexistente")
    void createVehicleWithFiles_deveLancarExcecaoSeUsuarioNaoExiste() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

    
        assertThrows(EntityNotFoundException.class, () -> {
            vehicleService.createVehicleWithFiles(createDTO, pdfFile, imageFile);
        });
    }

    @Test
    @DisplayName("Deve deletar um veículo, seus documentos e seus arquivos físicos")
    void delete_deveDeletarTudoEmCascata() {
        // Arrange
        Vehicle vehicleToDelete = new Vehicle();
        vehicleToDelete.setId(1);
        
        Document documentToDelete = new Document();
        documentToDelete.setArquivo("arquivo_para_deletar.pdf");
        List<Document> documents = new ArrayList<>();
        documents.add(documentToDelete);
        
        when(vehicleRepository.existsById(1)).thenReturn(true);
        when(documentRepository.findByVehicleId(1)).thenReturn(documents);

   
        vehicleService.delete(1);

     
        verify(fileStorageService, times(1)).deleteFile("arquivo_para_deletar.pdf");

        verify(documentRepository, times(1)).deleteAll(documents);
   
        verify(vehicleRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve retornar uma lista de veículos filtrada por marca")
    void findAllWithFilters_deveFiltrarPorMarca() {
      
        when(vehicleRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

   
        vehicleService.findAllWithFilters(null, "Ford");

        
        verify(vehicleRepository, times(1)).findAll(any(Specification.class));
    }
}
