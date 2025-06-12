import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import StudentProfileAppResolve from './route/student-profile-app-routing-resolve.service';

const studentProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/student-profile-app.component').then(m => m.StudentProfileAppComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/student-profile-app-detail.component').then(m => m.StudentProfileAppDetailComponent),
    resolve: {
      studentProfile: StudentProfileAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/student-profile-app-update.component').then(m => m.StudentProfileAppUpdateComponent),
    resolve: {
      studentProfile: StudentProfileAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/student-profile-app-update.component').then(m => m.StudentProfileAppUpdateComponent),
    resolve: {
      studentProfile: StudentProfileAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default studentProfileRoute;
