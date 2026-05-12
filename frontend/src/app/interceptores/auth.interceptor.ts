import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../servicios/auth.service';

const RUTAS_SIN_INTERCEPTAR = [
  '/api/auth/login',
  '/api/auth/refresh'
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const esRutaPublica = RUTAS_SIN_INTERCEPTAR.some(r => req.url.includes(r));
  if (esRutaPublica) {
    return next(req);
  }

  if (!authService.isAuthenticated()) {
    return next(req);
  }

  const token = authService.getToken();

  if (!token || token === 'null' || token === 'undefined' || token.split('.').length !== 3) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });

  return next(authReq).pipe(
    catchError(error => {
      if (error.status === 401) {
        const refreshToken = localStorage.getItem('refreshToken');

        if (refreshToken && refreshToken !== 'null' && refreshToken.split('.').length === 3) {
          return authService.renovarToken(refreshToken).pipe(
            switchMap(response => {
              authService.guardarToken(response);
              const nuevoReq = req.clone({
                setHeaders: { Authorization: `Bearer ${response.token}` }
              });
              return next(nuevoReq);
            }),
            catchError(refreshError => {
              authService.logout();
              router.navigate(['/login']);
              return throwError(() => refreshError);
            })
          );
        }

        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};