import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Button } from 'primeng/button';
import { Message } from 'primeng/message';
import { IftaLabel } from 'primeng/iftalabel';
import { Fluid } from 'primeng/fluid';
import { Card } from 'primeng/card';
import { Select } from 'primeng/select';

@Component({
  selector: 'app-registro',
  imports: [FormsModule, RouterLink, InputText, Password, Button, Message, IftaLabel, Fluid, Card, Select],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {

  private http;
  private router;

  constructor(http: HttpClient, router: Router) {
    this.http = http;
    this.router = router;
  }

  request = {
    nombre: '',
    email: '',
    tipoUsuario: 'ESTUDIANTE',
    password: ''
  };

  error: string = '';
  exito: string = '';
  cargando: boolean = false;

  registrar(): void {
    this.error = '';
    this.exito = '';
    this.cargando = true;

    this.http.post<any>('http://localhost:8080/api/usuarios', this.request).subscribe({
      next: () => {
        this.exito = 'Usuario creado exitosamente. Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        if (err.status === 409) {
          this.error = 'Ya existe un usuario con ese email.';
        } else {
          this.error = 'Error al crear el usuario. Intenta de nuevo.';
        }
        this.cargando = false;
      }
    });
  }
}