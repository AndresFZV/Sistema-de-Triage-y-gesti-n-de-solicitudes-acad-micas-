import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../servicios/auth.service';
import { LoginRequest } from '../../dto/auth.dto';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  request: LoginRequest = {
    username: '',
    password: ''
  };

  error: string = '';
  cargando: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.error = '';
    this.cargando = true;

    this.authService.login(this.request).subscribe({
      next: (response) => {
        this.authService.guardarToken(response);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.error = 'Credenciales incorrectas. Verifica tu email y contraseña.';
        this.cargando = false;
      }
    });
  }
}