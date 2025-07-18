import { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from '@angular/forms';
import { cpf } from 'cpf-cnpj-validator';

export function cpfValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const cpf = (control.value || '').replace(/\D/g, '');

    if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) {
      return { cpfInvalido: true };
    }

    const calcVerificador = (base: string, fator: number): number => {
      let total = 0;
      for (let i = 0; i < base.length; i++) {
        total += parseInt(base[i]) * fator--;
      }
      const resto = total % 11;
      return resto < 2 ? 0 : 11 - resto;
    };

    const digito1 = calcVerificador(cpf.substring(0, 9), 10);
    const digito2 = calcVerificador(cpf.substring(0, 10), 11);

    if (digito1 !== parseInt(cpf[9]) || digito2 !== parseInt(cpf[10])) {
      return { cpfInvalido: true };
    }

    return null;
  };
}
export function telefoneValido(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const valor = control.value;
    const regexTelefone = /^\(\d{2}\) \d{4,5}-\d{4}$/;
    if (valor && !regexTelefone.test(valor)) {
      return { telefoneInvalido: { value: control.value } };
    }
    return null;
  };
}


export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const senha = control.value;

    if (!senha) return null;

    const temLetraMaiuscula = /[A-Z]/.test(senha);
    const temLetraMinuscula = /[a-z]/.test(senha);
    const temNumero = /\d/.test(senha);
    const temEspecial = /[!@#$%^&*(),.?":{}|<>]/.test(senha);
    const tamanhoMinimo = senha.length >= 8;
    const tamanhoMaximo = senha.length <=12;

    const valido =
      temLetraMaiuscula &&
      temLetraMinuscula &&
      temNumero &&
      temEspecial &&
      tamanhoMaximo &&
      tamanhoMinimo;

    return valido
      ? null
      : {
          senhaInvalida: {
            requisitos: {
              letraMaiuscula: temLetraMaiuscula,
              letraMinuscula: temLetraMinuscula,
              numero: temNumero,
              especial: temEspecial,
              tamanhoMinimo: tamanhoMinimo,
            },
          },
        };
  };
}

export function senhasIguaisValidator(
  campoSenha: string,
  campoConfirmacao: string
): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    const senha = formGroup.get(campoSenha)?.value;
    const confirmar = formGroup.get(campoConfirmacao)?.value;

    return senha === confirmar ? null : { senhasDiferentes: true };
  };
}
