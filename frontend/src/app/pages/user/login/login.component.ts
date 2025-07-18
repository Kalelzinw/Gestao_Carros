
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router'; 
import { CommonModule } from '@angular/common';

import { ToastrService } from 'ngx-toastr';
import { DefaultLoginLayoutComponent } from '../../../components/default-login-layout/default-login-layout.component';
import { PrimaryInputComponent } from '../../../components/primary-input/primary-input.component';
import { AuthService } from '../../../services/auth-service'; 

interface LoginForm {
  email: FormControl<string | null>;
  password: FormControl<string | null>;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    DefaultLoginLayoutComponent,
    ReactiveFormsModule,
    PrimaryInputComponent,
    CommonModule,
    RouterModule 
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup<LoginForm>;

  constructor(
    private router: Router,
    private authService: AuthService,
    private toastService: ToastrService
  ){
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)])
    });
  }

  submit(){
    if (this.loginForm.invalid) {
      this.toastService.error("Por favor, preencha seu email e senha.");
      return;
    }

    this.authService.login(this.loginForm.value.email!, this.loginForm.value.password!).subscribe({
      next: () => {
        this.toastService.success("Login feito com sucesso!");
        this.router.navigate(['/app/home']); 
      },
      error: (err) => {
        console.error("Erro no login:", err);
        this.toastService.error(err.error?.message || "Email ou senha inválidos. Tente novamente.");
      }
    });
  }

  navigateToSignup(){
    this.router.navigate(["signup"]);
  }
}