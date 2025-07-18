import { Component, ViewChild, ElementRef } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { cpfValidator, passwordValidator, senhasIguaisValidator, telefoneValido } from '../../../validators';
import { CommonModule } from '@angular/common';
import { DefaultLoginLayoutComponent } from '../../../components/default-login-layout/default-login-layout.component';
import { PrimaryInputComponent } from '../../../components/primary-input/primary-input.component';
import { provideNgxMask } from 'ngx-mask';
import { LoginService} from '../../../services/user.service';

interface SignupForm {
  name: FormControl<string | null>;
  email: FormControl<string | null>;
  cpf: FormControl<string | null>;
  tel: FormControl<string | null>;
  nasc: FormControl<string | null>;
  password: FormControl<string | null>;
  passwordConfirm: FormControl<string | null>;
  imagem: FormControl<string | null>; 
}

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    DefaultLoginLayoutComponent,
    ReactiveFormsModule,
    PrimaryInputComponent,
    CommonModule,
  ],
  providers: [
    provideNgxMask()
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignUpComponent {
  signupForm: FormGroup<SignupForm>;
  previewImagem: string | null = null;
  imagemSelecionada: File | null = null; 

  @ViewChild('imagemInput') imagemInputRef!: ElementRef<HTMLInputElement>;

  constructor(
    private router: Router,
    private toastService: ToastrService,
    private loginService: LoginService
  ) {
    this.signupForm = new FormGroup<SignupForm>({
      name: new FormControl('', [Validators.required, Validators.minLength(3)]),
      email: new FormControl('', [Validators.required, Validators.email]),
      cpf: new FormControl('', [Validators.required, cpfValidator()]),
      tel: new FormControl('', [Validators.required, telefoneValido()]),
      nasc: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required, passwordValidator()]),
      passwordConfirm: new FormControl('', [Validators.required, Validators.minLength(6)]),
      imagem: new FormControl(null, [Validators.required]), 
    }, {
      validators: senhasIguaisValidator('password', 'passwordConfirm')
    });
  }

  selecionarImagem(): void {
    this.imagemInputRef.nativeElement.click();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) {
      this.imagemSelecionada = null;
      this.previewImagem = null;
      this.signupForm.get('imagem')?.setValue(null);
      return;
    }

    const file = input.files[0];
    if (file.type !== 'image/png' && file.type !== 'image/jpeg') {
      this.toastService.error('Apenas arquivos PNG ou JPEG são permitidos.');
      return;
    }

    this.imagemSelecionada = file; 
    this.signupForm.get('imagem')?.setValue(file.name); 

   
    const reader = new FileReader();
    reader.onload = () => {
      this.previewImagem = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  submit() {
    this.signupForm.markAllAsTouched();

    if (this.signupForm.valid && this.imagemSelecionada) {
      
      const formValues = this.signupForm.getRawValue();

      this.loginService.signup(formValues, this.imagemSelecionada).subscribe({
        next: () => {
          this.toastService.success("Cadastro feito com sucesso!");
          this.navigate();
        },
        error: (error) => {
          console.error("Erro ao registrar:", error);
          this.toastService.error(error.error?.message || "Erro ao registrar, tente novamente.");
        }
      });
    } else {
      this.toastService.error("Por favor, preencha todos os campos corretamente.");
    }
  }

  navigate() {
    this.router.navigate(["login"]);
  }
}
