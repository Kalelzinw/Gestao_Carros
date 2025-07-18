import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common'; 

import { DefaultLoginLayoutComponent } from '../../../components/default-login-layout/default-login-layout.component';
import { FormInputComponent } from "../../../components/form-input/form-input.component";

import { VehicleService, VehicleResponseDTO } from '../../../services/vehicle.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-vehicle-list',
  standalone: true,
  imports: [
    CommonModule,
    DefaultLoginLayoutComponent,
    FormInputComponent
  ],
  templateUrl: './vehicle-list.component.html', 
  styleUrl: './vehicle-list.component.scss' 
})
export class VehicleListComponent implements OnInit {
  vehicles: VehicleResponseDTO[] = [];

  constructor(
    private vehicleService: VehicleService, 
    private router: Router,
    private toastr: ToastrService 
  ) { }

  ngOnInit(): void {
    this.loadMyVehicles();
  }

  loadMyVehicles(): void {
    this.vehicleService.getMyVehicles().subscribe({
      next: (data) => {
        this.vehicles = data;
      },
      error: (err) => {
        console.error('Erro ao carregar seus veículos:', err);
        this.toastr.error('Não foi possível carregar seus veículos. Tente novamente mais tarde.');
      }
    });
  }

  navigateToCreate(): void {
    this.router.navigate(['/app/vehicles/new']); 
  }

  navigateToDetail(vehicleId: number): void {
    if (vehicleId) {
      this.router.navigate(['/app/vehicles/edit', vehicleId]); 
    }
  }

  getVehicleImageUrl(imageName: string): string {
    if (!imageName) {
      return '/assets/svg/foto.svg'; 
    }
    return `http://localhost:8080/uploads/${imageName}`;
  }
}
