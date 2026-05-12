import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { IftaLabel } from 'primeng/iftalabel';
import { Fluid } from 'primeng/fluid';
import { Card } from 'primeng/card';
import { LoginRequest, TokenResponse } from '../../modelos/auth';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, InputText, Password, Button, Message, IftaLabel, Fluid, Card],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  private http = inject(HttpClient);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);

  loginForm = inject(FormBuilder).group({
    username: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  isLoading = signal(false);
  result = signal('');

  formStatus = toSignal(this.loginForm.statusChanges, {
    initialValue: 'INVALID' as const
  });

  canSubmit = computed(() =>
    this.formStatus() === 'VALID' && !this.isLoading()
  );

  onSubmit(): void {
    if (!this.canSubmit()) return;

    this.isLoading.set(true);
    const credenciales = this.loginForm.value as LoginRequest;

    this.http.post<TokenResponse>('http://localhost:8080/api/auth/login', credenciales)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
  localStorage.setItem('token', response.token);
  localStorage.setItem('refreshToken', response.refreshToken);
  localStorage.setItem('roles', JSON.stringify(response.roles));
  this.authService.isAuthenticated.set(true);
  this.isLoading.set(false);

  const roles = response.roles;
  if (roles.includes('ADMIN') || roles.includes('ADMINISTRATIVO')) {
    this.router.navigate(['/dashboard']);
  } else {
    this.router.navigate(['/mis-solicitudes']);
  }
},
        error: () => {
          this.isLoading.set(false);
          this.result.set('Credenciales inválidas. Verifica tu correo y contraseña.');
        }
      });
  }
}