import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { VehicleService, VehicleSummaryDTO } from '../../../services/vehicle.service';
import { ToastrService } from 'ngx-toastr';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.scss']
})
export class SearchResultsComponent implements OnInit {

  vehicles: VehicleSummaryDTO[] = [];
  searchTerm: string | null = null;
  isLoading = true;

  private route = inject(ActivatedRoute);
  private vehicleService = inject(VehicleService);
  private toastr = inject(ToastrService);
  private router = inject(Router);

  ngOnInit(): void {
    
    this.route.queryParamMap.pipe(
      switchMap(params => {
        this.isLoading = true;
        const query = params.get('query');
        const marca = params.get('marca');
        
        this.searchTerm = query || marca; 

        return this.vehicleService.getVehicles({ query, marca });
      })
    ).subscribe({
      next: (data) => {
        this.vehicles = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error("Não foi possível realizar a busca.");
        this.isLoading = false;
      }
    });
  }
  
  getVehicleImageUrl(imageName: string): string {
    return `http://localhost:8080/uploads/${imageName}`;
  }

  navigateToDetail(vehicleId: number): void {
    this.router.navigate(['/app/vehicles/view', vehicleId]);
  }
}
