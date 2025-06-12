import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICourseApp } from '../course-app.model';
import { CourseAppService } from '../service/course-app.service';

const courseResolve = (route: ActivatedRouteSnapshot): Observable<null | ICourseApp> => {
  const id = route.params.id;
  if (id) {
    return inject(CourseAppService)
      .find(id)
      .pipe(
        mergeMap((course: HttpResponse<ICourseApp>) => {
          if (course.body) {
            return of(course.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default courseResolve;
