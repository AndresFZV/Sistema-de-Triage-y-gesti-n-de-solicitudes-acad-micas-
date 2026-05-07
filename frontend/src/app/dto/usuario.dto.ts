export interface UsuarioResponse {
  id: string;
  nombre: string;
  email: string;
  tipoUsuario: string;
}

export interface CrearUsuarioRequest {
  nombre: string;
  email: string;
  tipoUsuario: string;
  password: string;
}