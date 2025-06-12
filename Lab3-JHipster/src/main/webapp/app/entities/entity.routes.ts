import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'simpleWebApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'instructor-app',
    data: { pageTitle: 'simpleWebApp.instructor.home.title' },
    loadChildren: () => import('./instructor-app/instructor-app.routes'),
  },
  {
    path: 'course-app',
    data: { pageTitle: 'simpleWebApp.course.home.title' },
    loadChildren: () => import('./course-app/course-app.routes'),
  },
  {
    path: 'student-app',
    data: { pageTitle: 'simpleWebApp.student.home.title' },
    loadChildren: () => import('./student-app/student-app.routes'),
  },
  {
    path: 'student-profile-app',
    data: { pageTitle: 'simpleWebApp.studentProfile.home.title' },
    loadChildren: () => import('./student-profile-app/student-profile-app.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
