import { Routes } from '@angular/router';
import { Login } from './componentes/login/login';
import { Registro } from './componentes/registro/registro';
import { Dashboard } from './componentes/dashboard/dashboard';
import { Solicitudes } from './componentes/solicitudes/solicitudes';
import { SolicitudDetalle } from './componentes/solicitud-detalle/solicitud-detalle';
import { NuevaSolicitud } from './componentes/nueva-solicitud/nueva-solicitud';
import { Usuarios } from './componentes/usuarios/usuarios';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'dashboard', component: Dashboard },
  { path: 'solicitudes', component: Solicitudes },
  { path: 'nueva-solicitud', component: NuevaSolicitud },
  { path: 'solicitudes/:codigo', component: SolicitudDetalle },
  { path: 'usuarios', component: Usuarios },
  { path: '**', redirectTo: 'login' }
];