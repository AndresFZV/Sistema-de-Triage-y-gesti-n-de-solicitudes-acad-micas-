import { Routes } from '@angular/router';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { Dashboard } from './componentes/dashboard/dashboard';
import { ListaSolicitudes } from './componentes/lista-solicitudes/lista-solicitudes';
import { NuevaSolicitud } from './componentes/nueva-solicitud/nueva-solicitud';
import { SolicitudDetalle } from './componentes/solicitud-detalle/solicitud-detalle';
import { Usuarios } from './componentes/usuarios/usuarios';
import { Unauthorized } from './componentes/unauthorized/unauthorized';
import { authGuard, adminGuard } from './guards/auth.guard';
import { publicGuard } from './guards/public-guard';
import { rolesGuard } from './guards/roles-guard';
export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login, canActivate: [publicGuard] },
  { path: 'registro', component: Registro, canActivate: [publicGuard] },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [rolesGuard],
    data: { expectedRoles: ['ADMIN', 'ADMINISTRATIVO'] }
  },
  {
    path: 'lista-solicitudes',
    component: ListaSolicitudes,
    canActivate: [rolesGuard],
    data: { expectedRoles: ['ADMIN', 'ADMINISTRATIVO'] }
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
    canActivate: [rolesGuard],
    data: { expectedRoles: ['ADMIN', 'ADMINISTRATIVO'] }
  },
  { path: 'unauthorized', component: Unauthorized },
  { path: '**', redirectTo: 'login' }
];