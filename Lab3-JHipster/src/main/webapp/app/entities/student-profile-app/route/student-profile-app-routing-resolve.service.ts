import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStudentProfileApp } from '../student-profile-app.model';
import { StudentProfileAppService } from '../service/student-profile-app.service';

const studentProfileResolve = (route: ActivatedRouteSnapshot): Observable<null | IStudentProfileApp> => {
  const id = route.params.id;
  if (id) {
    return inject(StudentProfileAppService)
      .find(id)
      .pipe(
        mergeMap((studentProfile: HttpResponse<IStudentProfileApp>) => {
          if (studentProfile.body) {
            return of(studentProfile.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default studentProfileResolve;
