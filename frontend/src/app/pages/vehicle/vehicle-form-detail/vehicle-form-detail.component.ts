import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { provideNgxMask } from 'ngx-mask';
import { ToastrService } from 'ngx-toastr';

import { DefaultLoginLayoutComponent } from '../../../components/default-login-layout/default-login-layout.component';
import { FormInputComponent } from "../../../components/form-input/form-input.component";
import { VehicleService, VehicleResponseDTO } from '../../../services/vehicle.service';
import { LoginService } from '../../../services/user.service'; 

interface VehicleForm {
  marca: FormControl<string | null>;
  placa: FormControl<string | null>;
  chassi: FormControl<string | null>;
  ano: FormControl<number | null>;
  cor: FormControl<string | null>;
  modelo: FormControl<string | null>;
  quilometragem: FormControl<number | null>;
  localidade: FormControl<string | null>;
  valorVenda: FormControl<number | null>;
  valorCompra: FormControl<number | null>;
  status: FormControl<string | null>;
  visivel: FormControl<boolean | null>;
}

@Component({
  selector: 'app-vehicle-form-detail',
  standalone: true,
  imports: [
    DefaultLoginLayoutComponent,
    ReactiveFormsModule,
    FormInputComponent,
    CommonModule,
  ],
  providers: [provideNgxMask()],
  templateUrl: './vehicle-form-detail.component.html',
  styleUrl: './vehicle-form-detail.component.scss'
})
export class VehicleFormDetailComponent implements OnInit {
  imagemSelecionada: File | null = null;
  documentoSelecionado: File | null = null;
  previewImagem: string | null = null;
  nomeArquivoDocumento: string | null = null;
  userId: number | null = null;

  vehicleId: string | null = null;
  isEditMode: boolean = false;
  vehicleForm: FormGroup<VehicleForm>;

  @ViewChild('imagemInput') imagemInputRef!: ElementRef<HTMLInputElement>;

  constructor(
    private vehicleService: VehicleService,
    private loginService: LoginService, 
    private toastService: ToastrService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.vehicleForm = new FormGroup<VehicleForm>({
        marca: new FormControl('', [Validators.required]),
        placa: new FormControl('', [Validators.required]),
        chassi: new FormControl('', [Validators.required, Validators.minLength(17), Validators.maxLength(17)]),
        ano: new FormControl(null, [Validators.required, Validators.min(1900)]),
        cor: new FormControl('', [Validators.required]),
        modelo: new FormControl('', [Validators.required]),
        quilometragem: new FormControl(null, [Validators.required, Validators.min(0)]),
        localidade: new FormControl('', [Validators.required]),
        valorVenda: new FormControl(null, [Validators.required, Validators.min(0)]),
        valorCompra: new FormControl(null, [Validators.required, Validators.min(0)]),
        status: new FormControl('DISPONIVEL', [Validators.required]),
        visivel: new FormControl(true, [Validators.required]),
    });
  }

  ngOnInit(): void {
    this.loginService.getUserProfile().subscribe(user => {
      this.userId = user.id;
    });

    this.route.paramMap.subscribe(params => {
      this.vehicleId = params.get('id');
      this.isEditMode = !!this.vehicleId;

      if (this.isEditMode && this.vehicleId) {
        this.loadVehicleDetails(this.vehicleId);
      }
    });
  }

  loadVehicleDetails(id: string): void {
    this.vehicleService.getVehicleById(id).subscribe({
      next: (vehicle: VehicleResponseDTO) => {
        this.vehicleForm.patchValue(vehicle);
        if (vehicle.imageUrl) {
          this.previewImagem = `http://localhost:8080/uploads/${vehicle.imageUrl}`;
        }
        if (vehicle.documents && vehicle.documents.length > 0) {
          this.nomeArquivoDocumento = vehicle.documents[0].arquivo;
        }
      },
      error: (err) => {
        this.toastService.error('Erro ao carregar detalhes do veículo.');
        this.router.navigate(['/app/home']);
      }
    });
  }

  onImagemSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      if (!file.type.startsWith('image/')) {
        this.toastService.error('Apenas arquivos de imagem são permitidos.');
        return;
      }
      this.imagemSelecionada = file;
      const reader = new FileReader();
      reader.onload = () => { this.previewImagem = reader.result as string; };
      reader.readAsDataURL(file);
    }
  }

  onDocumentoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      if (file.type !== 'application/pdf') {
        this.toastService.error('Apenas arquivos PDF são permitidos.');
        return;
      }
      this.documentoSelecionado = file;
      this.nomeArquivoDocumento = file.name;
    }
  }

  submit(): void {
    if (this.vehicleForm.invalid) {
      this.toastService.error('Por favor, preencha todos os campos obrigatórios.');
      return;
    }
    if (!this.userId) {
      this.toastService.error('Não foi possível identificar o usuário. Por favor, faça login novamente.');
      return;
    }
    if (!this.isEditMode && (!this.imagemSelecionada || !this.documentoSelecionado)) {
      this.toastService.error('A imagem e o documento são obrigatórios para o cadastro.');
      return;
    }

    const formValues = this.vehicleForm.getRawValue();

     console.log('DADOS DO FORMULÁRIO ANTES DE ENVIAR:', formValues);
    const vehicleDataWithUser = { ...formValues, userId: this.userId };

    const operation = this.isEditMode && this.vehicleId
      ? this.vehicleService.updateVehicle(this.vehicleId, vehicleDataWithUser, this.imagemSelecionada!, this.documentoSelecionado!)
      : this.vehicleService.createVehicle(vehicleDataWithUser, this.imagemSelecionada!, this.documentoSelecionado!);
    
    operation.subscribe({
      next: () => {
        const successMessage = this.isEditMode ? 'Veículo atualizado!' : 'Veículo cadastrado!';
        this.toastService.success(successMessage);
        this.router.navigate(['/app/home']);
      },
      error: (error: HttpErrorResponse) => {
        this.toastService.error(error.error?.message || 'Ocorreu um erro.');
      }
    });
  }

  onDeleteVehicle(): void {
    if (!this.vehicleId) {
      this.toastService.error('ID do veículo não encontrado para exclusão.');
      return;
    }

    if (confirm('Tem certeza que deseja deletar este veículo permanentemente?')) {
      this.vehicleService.deleteVehicle(this.vehicleId).subscribe({
        next: () => {
          this.toastService.success('Veículo deletado com sucesso!');
          this.router.navigate(['/app/home']);
        },
        error: (err) => {
          this.toastService.error(err.error?.message || 'Erro ao deletar o veículo.');
        }
      });
    }
  }
}
