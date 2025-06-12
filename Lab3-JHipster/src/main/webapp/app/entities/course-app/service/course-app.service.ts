import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICourseApp, NewCourseApp } from '../course-app.model';

export type PartialUpdateCourseApp = Partial<ICourseApp> & Pick<ICourseApp, 'id'>;

type RestOf<T extends ICourseApp | NewCourseApp> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestCourseApp = RestOf<ICourseApp>;

export type NewRestCourseApp = RestOf<NewCourseApp>;

export type PartialUpdateRestCourseApp = RestOf<PartialUpdateCourseApp>;

export type EntityResponseType = HttpResponse<ICourseApp>;
export type EntityArrayResponseType = HttpResponse<ICourseApp[]>;

@Injectable({ providedIn: 'root' })
export class CourseAppService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/courses');

  create(course: NewCourseApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(course);
    return this.http
      .post<RestCourseApp>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(course: ICourseApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(course);
    return this.http
      .put<RestCourseApp>(`${this.resourceUrl}/${this.getCourseAppIdentifier(course)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(course: PartialUpdateCourseApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(course);
    return this.http
      .patch<RestCourseApp>(`${this.resourceUrl}/${this.getCourseAppIdentifier(course)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCourseApp>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCourseApp[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCourseAppIdentifier(course: Pick<ICourseApp, 'id'>): number {
    return course.id;
  }

  compareCourseApp(o1: Pick<ICourseApp, 'id'> | null, o2: Pick<ICourseApp, 'id'> | null): boolean {
    return o1 && o2 ? this.getCourseAppIdentifier(o1) === this.getCourseAppIdentifier(o2) : o1 === o2;
  }

  addCourseAppToCollectionIfMissing<Type extends Pick<ICourseApp, 'id'>>(
    courseCollection: Type[],
    ...coursesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const courses: Type[] = coursesToCheck.filter(isPresent);
    if (courses.length > 0) {
      const courseCollectionIdentifiers = courseCollection.map(courseItem => this.getCourseAppIdentifier(courseItem));
      const coursesToAdd = courses.filter(courseItem => {
        const courseIdentifier = this.getCourseAppIdentifier(courseItem);
        if (courseCollectionIdentifiers.includes(courseIdentifier)) {
          return false;
        }
        courseCollectionIdentifiers.push(courseIdentifier);
        return true;
      });
      return [...coursesToAdd, ...courseCollection];
    }
    return courseCollection;
  }

  protected convertDateFromClient<T extends ICourseApp | NewCourseApp | PartialUpdateCourseApp>(course: T): RestOf<T> {
    return {
      ...course,
      startDate: course.startDate?.format(DATE_FORMAT) ?? null,
      endDate: course.endDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restCourseApp: RestCourseApp): ICourseApp {
    return {
      ...restCourseApp,
      startDate: restCourseApp.startDate ? dayjs(restCourseApp.startDate) : undefined,
      endDate: restCourseApp.endDate ? dayjs(restCourseApp.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCourseApp>): HttpResponse<ICourseApp> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCourseApp[]>): HttpResponse<ICourseApp[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
