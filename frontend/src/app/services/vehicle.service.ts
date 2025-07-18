import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- Interfaces para tipar os dados (é uma boa prática mantê-las em um arquivo separado) ---

export interface VehicleSummaryDTO {
  id: number;
  marca: string;
  modelo: string;
  ano: number;
  valorVenda: number;
  imageUrl: string;
  localidade: string;
  placa: string;
}

// Em src/app/services/vehicle.service.ts

// ...

// Para os detalhes completos de um veículo (findById)
export interface VehicleResponseDTO {
  id: number;
  marca: string;
  modelo: string;
  ano: number;
  cor: string;
  quilometragem: number;
  localidade: string;
  valorVenda: number;
  valorCompra: number;
  imageUrl: string;
  status: string;
  visivel: boolean;
  placa: string;
  chassi: string;
  // MUDANÇA: Atualize a interface do usuário para incluir os novos campos
  user: { 
    id: number; 
    name: string; 
    fotoUrl: string | null; 
    email: string; // <-- Adicionado
    tel: string;   // <-- Adicionado
  };
  documents: { id: number; tipo: string; arquivo: string; }[];
  isFavorited: boolean;
}

// ... o resto do seu serviço ...


@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private apiUrl = 'http://localhost:8080/vehicles';
  private userApiUrl = 'http://localhost:8080/user';

  private http = inject(HttpClient);

  // --- MÉTODOS DE GERENCIAMENTO DE VEÍCULOS ---

  getVehicles(filters?: { query?: string | null, marca?: string | null }): Observable<VehicleSummaryDTO[]> {
    let params = new HttpParams();
    let endpoint = `${this.apiUrl}/list`; // Endpoint padrão é a lista com filtros

    if (filters) {
      if (filters.query) {
        // Se a busca for por texto, usamos o endpoint de busca
        endpoint = `${this.apiUrl}/search`;
        params = params.set('query', filters.query);
      }
      if (filters.marca) {
        params = params.set('marca', filters.marca);
      }
      // No futuro, outros filtros (ano, preço) seriam adicionados aqui
    }

    return this.http.get<VehicleSummaryDTO[]>(endpoint, { params });
  }

  getMyVehicles(): Observable<VehicleResponseDTO[]> {
    return this.http.get<VehicleResponseDTO[]>(`${this.apiUrl}/my-vehicles`);
  }

  getVehicleById(id: string): Observable<VehicleResponseDTO> {
    return this.http.get<VehicleResponseDTO>(`${this.apiUrl}/${id}`);
  }

  createVehicle(formValues: any, vehicleImage: File, documentPdf: File): Observable<VehicleResponseDTO> {
    const formData = new FormData();

    Object.keys(formValues).forEach(key => {
      formData.append(key, formValues[key]);
    });
    
    formData.append('vehicleImage', vehicleImage);
    formData.append('documentoPdf', documentPdf);
    
    return this.http.post<VehicleResponseDTO>(`${this.apiUrl}/create`, formData);
  }

  updateVehicle(id: string, formValues: any, vehicleImage?: File | null, documentPdf?: File | null): Observable<VehicleResponseDTO> {
    const formData = new FormData();
    
    // Adiciona os campos de texto do formulário ao FormData
    Object.keys(formValues).forEach(key => {
        // Apenas adiciona se o valor não for nulo ou indefinido
        if (formValues[key] !== null && formValues[key] !== undefined) {
            formData.append(key, formValues[key]);
        }
    });

    // Adiciona os arquivos se eles existirem
    if (vehicleImage) {
      formData.append('vehicleImage', vehicleImage);
    }
    if (documentPdf) {
      formData.append('documentoPdf', documentPdf);
    }
    
    // =================================================================
    //  DEBUG: VAMOS VER O QUE ESTÁ DENTRO DO FORMDATA DO UPDATE
    // =================================================================
    console.log(`--- Conteúdo do FormData para UPDATE do veículo ${id} ---`);
    formData.forEach((value, key) => {
        console.log(`Chave: '${key}', Valor:`, value);
    });
    console.log("---------------------------------------------------------");

    return this.http.put<VehicleResponseDTO>(`${this.apiUrl}/update/${id}`, formData);
}

  deleteVehicle(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  // --- MÉTODOS PARA FAVORITOS ---

  addFavorite(vehicleId: number): Observable<void> {
    return this.http.post<void>(`${this.userApiUrl}/favorites/${vehicleId}`, {});
  }

  removeFavorite(vehicleId: number): Observable<void> {
    return this.http.delete<void>(`${this.userApiUrl}/favorites/${vehicleId}`);
  }

  getFavorites(): Observable<VehicleSummaryDTO[]> {
    return this.http.get<VehicleSummaryDTO[]>(`${this.userApiUrl}/favorites`);
  }
}
