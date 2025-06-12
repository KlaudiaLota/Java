import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import InstructorAppResolve from './route/instructor-app-routing-resolve.service';

const instructorRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/instructor-app.component').then(m => m.InstructorAppComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/instructor-app-detail.component').then(m => m.InstructorAppDetailComponent),
    resolve: {
      instructor: InstructorAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/instructor-app-update.component').then(m => m.InstructorAppUpdateComponent),
    resolve: {
      instructor: InstructorAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/instructor-app-update.component').then(m => m.InstructorAppUpdateComponent),
    resolve: {
      instructor: InstructorAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default instructorRoute;
