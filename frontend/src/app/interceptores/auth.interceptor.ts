import { HttpInterceptorFn } from '@angular/common/http';

const RUTAS_PUBLICAS = [
  '/api/auth/login',
  '/api/auth/refresh',
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  const tokenValido = token !== null && token !== 'null' && token !== 'undefined' && token.split('.').length === 3;

  const esRutaPublica = RUTAS_PUBLICAS.some(ruta => req.url.includes(ruta));

  if (esRutaPublica) {
    // Eliminar cualquier header de autorización en rutas públicas
    const reqLimpio = req.clone({
      headers: req.headers.delete('Authorization')
    });
    return next(reqLimpio);
  }

  if (tokenValido) {
    const peticionAutenticada = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(peticionAutenticada);
  }

  // Sin token válido — eliminar cualquier header de autorización
  const reqLimpio = req.clone({
    headers: req.headers.delete('Authorization')
  });
  return next(reqLimpio);
};