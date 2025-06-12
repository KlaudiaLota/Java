import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CourseAppResolve from './route/course-app-routing-resolve.service';

const courseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/course-app.component').then(m => m.CourseAppComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/course-app-detail.component').then(m => m.CourseAppDetailComponent),
    resolve: {
      course: CourseAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/course-app-update.component').then(m => m.CourseAppUpdateComponent),
    resolve: {
      course: CourseAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/course-app-update.component').then(m => m.CourseAppUpdateComponent),
    resolve: {
      course: CourseAppResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default courseRoute;
