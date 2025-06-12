import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInstructorApp } from '../instructor-app.model';
import { InstructorAppService } from '../service/instructor-app.service';

const instructorResolve = (route: ActivatedRouteSnapshot): Observable<null | IInstructorApp> => {
  const id = route.params.id;
  if (id) {
    return inject(InstructorAppService)
      .find(id)
      .pipe(
        mergeMap((instructor: HttpResponse<IInstructorApp>) => {
          if (instructor.body) {
            return of(instructor.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default instructorResolve;
