import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStudentApp, NewStudentApp } from '../student-app.model';

export type PartialUpdateStudentApp = Partial<IStudentApp> & Pick<IStudentApp, 'id'>;

type RestOf<T extends IStudentApp | NewStudentApp> = Omit<T, 'birthDate' | 'registrationDate'> & {
  birthDate?: string | null;
  registrationDate?: string | null;
};

export type RestStudentApp = RestOf<IStudentApp>;

export type NewRestStudentApp = RestOf<NewStudentApp>;

export type PartialUpdateRestStudentApp = RestOf<PartialUpdateStudentApp>;

export type EntityResponseType = HttpResponse<IStudentApp>;
export type EntityArrayResponseType = HttpResponse<IStudentApp[]>;

@Injectable({ providedIn: 'root' })
export class StudentAppService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/students');

  create(student: NewStudentApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(student);
    return this.http
      .post<RestStudentApp>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(student: IStudentApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(student);
    return this.http
      .put<RestStudentApp>(`${this.resourceUrl}/${this.getStudentAppIdentifier(student)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(student: PartialUpdateStudentApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(student);
    return this.http
      .patch<RestStudentApp>(`${this.resourceUrl}/${this.getStudentAppIdentifier(student)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStudentApp>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStudentApp[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStudentAppIdentifier(student: Pick<IStudentApp, 'id'>): number {
    return student.id;
  }

  compareStudentApp(o1: Pick<IStudentApp, 'id'> | null, o2: Pick<IStudentApp, 'id'> | null): boolean {
    return o1 && o2 ? this.getStudentAppIdentifier(o1) === this.getStudentAppIdentifier(o2) : o1 === o2;
  }

  addStudentAppToCollectionIfMissing<Type extends Pick<IStudentApp, 'id'>>(
    studentCollection: Type[],
    ...studentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const students: Type[] = studentsToCheck.filter(isPresent);
    if (students.length > 0) {
      const studentCollectionIdentifiers = studentCollection.map(studentItem => this.getStudentAppIdentifier(studentItem));
      const studentsToAdd = students.filter(studentItem => {
        const studentIdentifier = this.getStudentAppIdentifier(studentItem);
        if (studentCollectionIdentifiers.includes(studentIdentifier)) {
          return false;
        }
        studentCollectionIdentifiers.push(studentIdentifier);
        return true;
      });
      return [...studentsToAdd, ...studentCollection];
    }
    return studentCollection;
  }

  protected convertDateFromClient<T extends IStudentApp | NewStudentApp | PartialUpdateStudentApp>(student: T): RestOf<T> {
    return {
      ...student,
      birthDate: student.birthDate?.format(DATE_FORMAT) ?? null,
      registrationDate: student.registrationDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restStudentApp: RestStudentApp): IStudentApp {
    return {
      ...restStudentApp,
      birthDate: restStudentApp.birthDate ? dayjs(restStudentApp.birthDate) : undefined,
      registrationDate: restStudentApp.registrationDate ? dayjs(restStudentApp.registrationDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStudentApp>): HttpResponse<IStudentApp> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStudentApp[]>): HttpResponse<IStudentApp[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
