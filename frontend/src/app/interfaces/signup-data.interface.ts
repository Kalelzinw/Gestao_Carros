// src/app/types/signup-data.type.ts

export interface SignupData {
  imagemBase64: string;
  name: string;
  cpf: string; // Já limpo, ou você pode limpar no serviço se preferir
  tel: string;
  nasc: string;
  email: string;
  password: string;
  passwordConfirm: string;
}