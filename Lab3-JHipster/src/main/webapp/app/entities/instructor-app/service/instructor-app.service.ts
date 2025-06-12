import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInstructorApp, NewInstructorApp } from '../instructor-app.model';

export type PartialUpdateInstructorApp = Partial<IInstructorApp> & Pick<IInstructorApp, 'id'>;

type RestOf<T extends IInstructorApp | NewInstructorApp> = Omit<T, 'hireDate'> & {
  hireDate?: string | null;
};

export type RestInstructorApp = RestOf<IInstructorApp>;

export type NewRestInstructorApp = RestOf<NewInstructorApp>;

export type PartialUpdateRestInstructorApp = RestOf<PartialUpdateInstructorApp>;

export type EntityResponseType = HttpResponse<IInstructorApp>;
export type EntityArrayResponseType = HttpResponse<IInstructorApp[]>;

@Injectable({ providedIn: 'root' })
export class InstructorAppService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/instructors');

  create(instructor: NewInstructorApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(instructor);
    return this.http
      .post<RestInstructorApp>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(instructor: IInstructorApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(instructor);
    return this.http
      .put<RestInstructorApp>(`${this.resourceUrl}/${this.getInstructorAppIdentifier(instructor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(instructor: PartialUpdateInstructorApp): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(instructor);
    return this.http
      .patch<RestInstructorApp>(`${this.resourceUrl}/${this.getInstructorAppIdentifier(instructor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestInstructorApp>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInstructorApp[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getInstructorAppIdentifier(instructor: Pick<IInstructorApp, 'id'>): number {
    return instructor.id;
  }

  compareInstructorApp(o1: Pick<IInstructorApp, 'id'> | null, o2: Pick<IInstructorApp, 'id'> | null): boolean {
    return o1 && o2 ? this.getInstructorAppIdentifier(o1) === this.getInstructorAppIdentifier(o2) : o1 === o2;
  }

  addInstructorAppToCollectionIfMissing<Type extends Pick<IInstructorApp, 'id'>>(
    instructorCollection: Type[],
    ...instructorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const instructors: Type[] = instructorsToCheck.filter(isPresent);
    if (instructors.length > 0) {
      const instructorCollectionIdentifiers = instructorCollection.map(instructorItem => this.getInstructorAppIdentifier(instructorItem));
      const instructorsToAdd = instructors.filter(instructorItem => {
        const instructorIdentifier = this.getInstructorAppIdentifier(instructorItem);
        if (instructorCollectionIdentifiers.includes(instructorIdentifier)) {
          return false;
        }
        instructorCollectionIdentifiers.push(instructorIdentifier);
        return true;
      });
      return [...instructorsToAdd, ...instructorCollection];
    }
    return instructorCollection;
  }

  protected convertDateFromClient<T extends IInstructorApp | NewInstructorApp | PartialUpdateInstructorApp>(instructor: T): RestOf<T> {
    return {
      ...instructor,
      hireDate: instructor.hireDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restInstructorApp: RestInstructorApp): IInstructorApp {
    return {
      ...restInstructorApp,
      hireDate: restInstructorApp.hireDate ? dayjs(restInstructorApp.hireDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestInstructorApp>): HttpResponse<IInstructorApp> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestInstructorApp[]>): HttpResponse<IInstructorApp[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
