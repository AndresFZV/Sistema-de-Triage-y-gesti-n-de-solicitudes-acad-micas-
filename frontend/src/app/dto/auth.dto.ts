export interface LoginRequest {
  username: string;
  password: string;
}

export interface TokenResponse {
  token: string;
  refreshToken: string;
  type: string;
  expireAt: string;
  roles: string[];
}