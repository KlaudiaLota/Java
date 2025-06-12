import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import StudentAppResolve from './route/student-app-routing-resolve.service';

const studentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/student-app.component').then(m => m.StudentAppComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/student-app-detail.component').then(m => m.StudentAppDetailComponent),
    resolve: {
      student: StudentAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/student-app-update.component').then(m => m.StudentAppUpdateComponent),
    resolve: {
      student: StudentAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/student-app-update.component').then(m => m.StudentAppUpdateComponent),
    resolve: {
      student: StudentAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default studentRoute;
