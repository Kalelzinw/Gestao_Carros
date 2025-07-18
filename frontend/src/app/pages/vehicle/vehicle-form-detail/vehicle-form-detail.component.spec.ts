// src/app/pages/vehicle/vehicle-form-detail/vehicle-form-detail.component.spec.ts

import { ComponentFixture, TestBed } from '@angular/core/testing';

// Importe o componente COM O NOVO NOME DA CLASSE E ARQUIVO
import { VehicleFormDetailComponent } from './vehicle-form-detail.component';

describe('VehicleFormDetailComponent', () => { // ATUALIZADO: Nome do grupo de testes
  let component: VehicleFormDetailComponent; // ATUALIZADO: Tipo do componente
  let fixture: ComponentFixture<VehicleFormDetailComponent>; // ATUALIZADO: Tipo do fixture

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // ATUALIZADO: Use o novo nome da classe do componente aqui
      imports: [VehicleFormDetailComponent]
    })
    .compileComponents();

    // ATUALIZADO: Crie o componente usando o novo nome da classe
    fixture = TestBed.createComponent(VehicleFormDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});