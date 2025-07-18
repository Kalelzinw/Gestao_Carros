import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';
import { switchMap, catchError, take } from 'rxjs/operators';

import { VehicleService, VehicleResponseDTO } from '../../../services/vehicle.service';
import { AuthService, UserProfile } from '../../../services/auth-service';

@Component({
  selector: 'app-vehicle-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './vehicle-detail.component.html',
  styleUrls: ['./vehicle-detail.component.scss']
})
export class VehicleDetailComponent implements OnInit {
  
  vehicle: VehicleResponseDTO | null = null;
  isLoading = true;
  isLoggedIn$: Observable<boolean>;
  isOwner = false;

  private route = inject(ActivatedRoute);
  private vehicleService = inject(VehicleService);
  private authService = inject(AuthService);
  private toastr = inject(ToastrService);
  private router = inject(Router);

  constructor() {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
  }

  ngOnInit(): void {
    const vehicleId = this.route.snapshot.paramMap.get('id');
    if (vehicleId) {
      this.loadVehicleDetails(vehicleId);
    } else {
      this.handleError("ID do veículo não encontrado na URL.");
    }
  }

 
loadVehicleDetails(id: string): void {
    this.isLoading = true;
    this.vehicleService.getVehicleById(id).subscribe({
      next: (data) => {
        this.vehicle = data;
        
        console.log('[FRONTEND] Dados recarregados do backend. O objeto recebido é:', this.vehicle);
        console.log('[FRONTEND] O valor de isFavorited recebido foi: ->', this.vehicle?.isFavorited);

        this.checkOwnership();
        this.isLoading = false;
      },
      error: () => this.handleError("Não foi possível carregar os detalhes deste veículo.")
    });
  }

  checkOwnership(): void {
    this.authService.currentUser$.pipe(take(1)).subscribe(currentUser => {
      this.isOwner = !!(currentUser && this.vehicle && currentUser.id === this.vehicle.user.id);
    });
  }

  toggleFavorite(): void {
    if (!this.vehicle) return;

    const vehicleId = this.vehicle.id;
    const currentlyFavorited = this.vehicle.isFavorited;

    const action = currentlyFavorited
      ? this.vehicleService.removeFavorite(vehicleId)
      : this.vehicleService.addFavorite(vehicleId);

    action.subscribe({
      next: () => {
        const message = currentlyFavorited ? 'Removido dos favoritos!' : 'Adicionado aos favoritos!';
        this.toastr.success(message);
   
        this.loadVehicleDetails(vehicleId.toString());
      },
      error: () => this.toastr.error("Ocorreu um erro. Por favor, faça login e tente novamente.")
    });
  }

  getVehicleImageUrl(imageName: string | null): string {
    if (!imageName) return '/assets/svg/foto.svg';
    return `http://localhost:8080/uploads/${imageName}`;
  }

  private handleError(message: string): void {
    this.toastr.error(message);
    this.router.navigate(['/app/home']);
  }
}
