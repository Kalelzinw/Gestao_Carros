
import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

import { VehicleService, VehicleSummaryDTO } from '../../../services/vehicle.service';

@Component({
  selector: 'app-favorites-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './favorites-list.component.html',
  styleUrls: ['./favorites-list.component.scss']
})
export class FavoritesListComponent implements OnInit {

  favoriteVehicles: VehicleSummaryDTO[] = [];
  isLoading = true;

  private vehicleService = inject(VehicleService);
  private toastr = inject(ToastrService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadFavorites();
  }

  loadFavorites(): void {
    this.isLoading = true;
    this.vehicleService.getFavorites().subscribe({
      next: (data) => {
        this.favoriteVehicles = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erro ao buscar favoritos:", err);
        this.toastr.error("Não foi possível carregar seus veículos favoritos.");
        this.isLoading = false;
      }
    });
  }

  navigateToDetail(vehicleId: number): void {
    this.router.navigate(['/app/vehicles/view', vehicleId]);
  }

  getVehicleImageUrl(imageName: string): string {
    return `http://localhost:8080/uploads/${imageName}`;
  }
}