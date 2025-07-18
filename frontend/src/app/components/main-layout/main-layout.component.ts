

import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AuthService, UserProfile } from '../../services/auth-service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent {

 
  isLoggedIn$: Observable<boolean>;
  currentUser$: Observable<UserProfile | null>;

  
  isDropdownOpen = false;

  defaultAvatar = '/assets/svg/user-icon.svg'; 

  constructor(private authService: AuthService, private router: Router) {
    
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.currentUser$ = this.authService.currentUser$;
  }

 
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }


  logout(): void {
    this.isDropdownOpen = false; 
    this.authService.logout();
  }
  
  
  getImageUrl(imageName: string | null): string {
    if (!imageName) {
      return this.defaultAvatar;
    }
    return `http://localhost:8080/uploads/${imageName}`;
  }

 
  navigateTo(path: string): void {
    this.isDropdownOpen = false;
    this.router.navigate([path]);
  }
}