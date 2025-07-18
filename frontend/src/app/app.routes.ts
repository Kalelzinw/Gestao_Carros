import { Routes } from '@angular/router';
import { LoginComponent } from './pages/user/login/login.component';
import { SignUpComponent } from './pages/user/signup/signup.component';
import { HomeComponent } from './pages/home/home.component';
import { UserComponent } from './pages/user/user-profile/user.component';

import { VehicleListComponent } from './pages/vehicle/vehicle-list/vehicle-list.component';
import { VehicleFormDetailComponent } from './pages/vehicle/vehicle-form-detail/vehicle-form-detail.component';
import { SearchResultsComponent } from './pages/vehicle/search-results/search-results.component'; 

import { AuthGuard } from './services/auth-guard.service';
import { MainLayoutComponent } from './components/main-layout/main-layout.component';
import { VehicleDetailComponent } from './pages/vehicle/vehicle-detail/vehicle-detail.component';
import { FavoritesListComponent } from './pages/user/favorites-list/favorites-list.component';


export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignUpComponent },

  {
    path: 'app',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'user', component: UserComponent, canActivate: [AuthGuard] },
      { path: 'vehicles', component: VehicleListComponent },
      { path: 'vehicles/new', component: VehicleFormDetailComponent, canActivate: [AuthGuard] },
      { path: 'vehicles/edit/:id', component: VehicleFormDetailComponent, canActivate: [AuthGuard] },
      { path: 'buscar', component: SearchResultsComponent }, 
      { path: 'vehicles/view/:id', component: VehicleDetailComponent },
      { path: 'favorites', component: FavoritesListComponent, canActivate: [AuthGuard] },
    ]
  },

  { path: '**', redirectTo: 'app/home' }
];