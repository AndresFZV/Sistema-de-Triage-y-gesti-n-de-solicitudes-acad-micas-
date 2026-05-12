import { Routes } from '@angular/router';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { Dashboard } from './componentes/dashboard/dashboard';
import { ListaSolicitudes } from './componentes/lista-solicitudes/lista-solicitudes';
import { NuevaSolicitud } from './componentes/nueva-solicitud/nueva-solicitud';
import { SolicitudDetalle } from './componentes/solicitud-detalle/solicitud-detalle';
import { Usuarios } from './componentes/usuarios/usuarios';
import { authGuard, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'lista-solicitudes',
    component: ListaSolicitudes,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'mis-solicitudes',
    component: ListaSolicitudes,
    canActivate: [authGuard]
  },
  {
    path: 'nueva-solicitud',
    component: NuevaSolicitud,
    canActivate: [authGuard]
  },
  {
    path: 'solicitudes/:codigo',
    component: SolicitudDetalle,
    canActivate: [authGuard]
  },
  {
    path: 'usuarios',
    component: Usuarios,
    canActivate: [authGuard, adminGuard]
  },
  { path: '**', redirectTo: 'login' }
];