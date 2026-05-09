import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // No agregar token en las rutas de autenticación
  const esRutaPublica = req.url.includes('/api/auth/login') ||
                        req.url.includes('/api/auth/refresh') ||
                        req.url.includes('/api/auth/registro');

  if (token && !esRutaPublica) {
    const peticionAutenticada = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(peticionAutenticada);
  }

  return next(req);
};