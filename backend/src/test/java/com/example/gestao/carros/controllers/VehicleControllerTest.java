package com.example.gestao.carros.controllers;

import com.example.gestao.carros.dto.VehicleResponseDTO;
import com.example.gestao.carros.dto.VehicleSummaryDTO;
import com.example.gestao.carros.infra.security.CustomUserDetailsService;
import com.example.gestao.carros.infra.security.TokenService;
import com.example.gestao.carros.services.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // <-- IMPORT NECESSÁRIO
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de veículos ao chamar /list")
    @WithMockUser
    void listAll_deveRetornarListaDeVeiculos() throws Exception {
        VehicleSummaryDTO summaryDTO = new VehicleSummaryDTO();
        summaryDTO.setMarca("Ford");
        List<VehicleSummaryDTO> vehicleList = Collections.singletonList(summaryDTO);
        
        given(vehicleService.findAllWithFilters(any(), any())).willReturn(vehicleList);

        mockMvc.perform(get("/vehicles/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].marca").value("Ford"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e os detalhes de um veículo ao chamar /{id}")
    @WithMockUser
    void findById_quandoIdExiste_deveRetornarDetalhesDoVeiculo() throws Exception {
        VehicleResponseDTO responseDTO = new VehicleResponseDTO();
        responseDTO.setId(1);
        responseDTO.setMarca("Ferrari");
        
        given(vehicleService.findById(1)).willReturn(responseDTO);

        mockMvc.perform(get("/vehicles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.marca").value("Ferrari"));
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao criar um veículo com sucesso")
    @WithMockUser
    void create_comDadosValidos_deveRetornarCreated() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("vehicleImage", "imagem.jpg", "image/jpeg", "conteudo".getBytes());
        MockMultipartFile pdfFile = new MockMultipartFile("documentoPdf", "doc.pdf", "application/pdf", "conteudo".getBytes());
        
        VehicleResponseDTO responseDTO = new VehicleResponseDTO();
        responseDTO.setId(1);

        given(vehicleService.createVehicleWithFiles(any(), any(), any())).willReturn(responseDTO);

        mockMvc.perform(multipart(HttpMethod.POST, "/vehicles/create")
                .file(imageFile)
                .file(pdfFile)
                .param("marca", "Fiat")
                .param("modelo", "Uno")
                .param("ano", "1995")
                .param("cor", "Branco")
                .param("quilometragem", "150000")
                .param("localidade", "Rua A")
                .param("valorVenda", "5000.0")
                .param("valorCompra", "2000.0")
                .param("status", "DISPONIVEL")
                .param("visivel", "true")
                .param("placa", "ABC1234")
                .param("chassi", "12345678901234567")
                .param("userId", "1")
                .with(csrf()) 
        )
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao deletar um veículo")
    @WithMockUser
    void delete_deveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/vehicles/delete/{id}", 1)
                .with(csrf()) 
        )
        .andExpect(status().isNoContent());
        
        verify(vehicleService, times(1)).delete(1);
    }
}