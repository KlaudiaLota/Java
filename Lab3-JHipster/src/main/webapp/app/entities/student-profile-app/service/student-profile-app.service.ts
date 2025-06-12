import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStudentProfileApp, NewStudentProfileApp } from '../student-profile-app.model';

export type PartialUpdateStudentProfileApp = Partial<IStudentProfileApp> & Pick<IStudentProfileApp, 'id'>;

export type EntityResponseType = HttpResponse<IStudentProfileApp>;
export type EntityArrayResponseType = HttpResponse<IStudentProfileApp[]>;

@Injectable({ providedIn: 'root' })
export class StudentProfileAppService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/student-profiles');

  create(studentProfile: NewStudentProfileApp): Observable<EntityResponseType> {
    return this.http.post<IStudentProfileApp>(this.resourceUrl, studentProfile, { observe: 'response' });
  }

  update(studentProfile: IStudentProfileApp): Observable<EntityResponseType> {
    return this.http.put<IStudentProfileApp>(`${this.resourceUrl}/${this.getStudentProfileAppIdentifier(studentProfile)}`, studentProfile, {
      observe: 'response',
    });
  }

  partialUpdate(studentProfile: PartialUpdateStudentProfileApp): Observable<EntityResponseType> {
    return this.http.patch<IStudentProfileApp>(
      `${this.resourceUrl}/${this.getStudentProfileAppIdentifier(studentProfile)}`,
      studentProfile,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStudentProfileApp>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStudentProfileApp[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStudentProfileAppIdentifier(studentProfile: Pick<IStudentProfileApp, 'id'>): number {
    return studentProfile.id;
  }

  compareStudentProfileApp(o1: Pick<IStudentProfileApp, 'id'> | null, o2: Pick<IStudentProfileApp, 'id'> | null): boolean {
    return o1 && o2 ? this.getStudentProfileAppIdentifier(o1) === this.getStudentProfileAppIdentifier(o2) : o1 === o2;
  }

  addStudentProfileAppToCollectionIfMissing<Type extends Pick<IStudentProfileApp, 'id'>>(
    studentProfileCollection: Type[],
    ...studentProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const studentProfiles: Type[] = studentProfilesToCheck.filter(isPresent);
    if (studentProfiles.length > 0) {
      const studentProfileCollectionIdentifiers = studentProfileCollection.map(studentProfileItem =>
        this.getStudentProfileAppIdentifier(studentProfileItem),
      );
      const studentProfilesToAdd = studentProfiles.filter(studentProfileItem => {
        const studentProfileIdentifier = this.getStudentProfileAppIdentifier(studentProfileItem);
        if (studentProfileCollectionIdentifiers.includes(studentProfileIdentifier)) {
          return false;
        }
        studentProfileCollectionIdentifiers.push(studentProfileIdentifier);
        return true;
      });
      return [...studentProfilesToAdd, ...studentProfileCollection];
    }
    return studentProfileCollection;
  }
}
