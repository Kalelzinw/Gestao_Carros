import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { passwordValidator, telefoneValido } from '../../../validators';
import { provideNgxMask } from 'ngx-mask';
import { LoginService } from '../../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { DefaultLoginLayoutComponent } from '../../../components/default-login-layout/default-login-layout.component';
import { FormInputComponent } from '../../../components/form-input/form-input.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
  standalone: true,
  imports: [
    DefaultLoginLayoutComponent,
    ReactiveFormsModule,
    FormInputComponent,
    CommonModule,
  ],
  providers: [
    provideNgxMask()
  ],
})
export class UserComponent implements OnInit {

  imagemSelecionada: File | null = null;
  previewImagem: string | null = null;

  userForm = new FormGroup({
    imagem: new FormControl<string | null>(null), 
    tel: new FormControl('', [telefoneValido()]),
    email: new FormControl('', [Validators.email]),
    password: new FormControl(''),
    passwordConfirm: new FormControl(''),
  });

  constructor(
    private loginService: LoginService,
    private toastService: ToastrService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loginService.getUserProfile().subscribe((user: any) => {
      this.userForm.patchValue({
        email: user.email,
        tel: user.tel,
      });
      if (user.fotoUrl) {
        this.previewImagem = 'http://localhost:8080/uploads/' + user.fotoUrl;
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.type !== 'image/png' && file.type !== 'image/jpeg') {
        this.toastService.error('Apenas arquivos PNG ou JPEG são permitidos.');
        return;
      }
      this.imagemSelecionada = file; 
      this.userForm.get('imagem')?.setValue(file.name); 

      const reader = new FileReader();
      reader.onload = () => {
        this.previewImagem = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  submit() {
    if (this.userForm.value.password && this.userForm.value.password !== this.userForm.value.passwordConfirm) {
      this.toastService.error('As senhas não coincidem.');
      return;
    }

    const formValue = this.userForm.getRawValue();

    this.loginService.updateUserProfile(formValue, this.imagemSelecionada).subscribe({
      next: (updatedUser) => {
        this.toastService.success('Perfil atualizado com sucesso!');
        if (updatedUser.fotoUrl) {
          this.previewImagem = 'http://localhost:8080/uploads/' + updatedUser.fotoUrl;
          this.imagemSelecionada = null; 
          this.userForm.get('imagem')?.reset();
        }
      },
      error: (error: any) => {
        console.error('Erro ao atualizar perfil:', error);
        this.toastService.error(error.error?.message || 'Erro ao atualizar o perfil.');
      },
    });
  }
  
  onDeleteAccount() {
    if (confirm('Tem certeza que deseja deletar sua conta permanentemente? Esta ação é irreversível.')) {
      this.loginService.deleteUserProfile().subscribe({
        next: () => {
          this.toastService.success('Sua conta foi deletada com sucesso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.toastService.error(err.message || 'Erro ao deletar a conta.');
        }
      });
    }
  }
}
