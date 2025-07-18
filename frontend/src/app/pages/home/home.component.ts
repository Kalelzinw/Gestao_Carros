import { Component, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { VehicleService, VehicleSummaryDTO } from '../../services/vehicle.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  vehicles: VehicleSummaryDTO[] = [];
  

  searchForm: FormGroup;

  private vehicleService = inject(VehicleService);
  private router = inject(Router);
  private toastr = inject(ToastrService);

  constructor() {
   
    this.searchForm = new FormGroup({
      searchInput: new FormControl('')
    });
  }

  ngOnInit(): void {
    this.loadInitialVehicles();
  }

  loadInitialVehicles(): void {
    this.vehicleService.getVehicles().subscribe({
      next: (data) => this.vehicles = data.slice(0, 6),
      error: () => this.toastr.error('Erro ao carregar veículos em destaque.')
    });
  }

  submitSearch(): void {
    const term = this.searchForm.value.searchInput;

    if (term && term.trim() !== '') {
      this.router.navigate(['/app/buscar'], { queryParams: { query: term } });
    }
  }

 searchByBrand(brandName: string): void {
  console.log(`[FRONTEND] Clique na marca: '${brandName}'. Navegando para /app/buscar com o filtro '${brandName}' .`);
  
  this.router.navigate(['/app/buscar'], { queryParams: { marca: brandName } });
}
  navigateToDetail(vehicleId: number): void {
    this.router.navigate(['/app/vehicles/view', vehicleId]);
  }

  getVehicleImageUrl(imageName: string): string {
    return `http://localhost:8080/uploads/${imageName}`;
  }
}