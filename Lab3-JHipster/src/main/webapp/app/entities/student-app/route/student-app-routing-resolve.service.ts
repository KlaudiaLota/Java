import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStudentApp } from '../student-app.model';
import { StudentAppService } from '../service/student-app.service';

const studentResolve = (route: ActivatedRouteSnapshot): Observable<null | IStudentApp> => {
  const id = route.params.id;
  if (id) {
    return inject(StudentAppService)
      .find(id)
      .pipe(
        mergeMap((student: HttpResponse<IStudentApp>) => {
          if (student.body) {
            return of(student.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default studentResolve;
