import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IStudentProfileApp } from '../student-profile-app.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../student-profile-app.test-samples';

import { StudentProfileAppService } from './student-profile-app.service';

const requireRestSample: IStudentProfileApp = {
  ...sampleWithRequiredData,
};

describe('StudentProfileApp Service', () => {
  let service: StudentProfileAppService;
  let httpMock: HttpTestingController;
  let expectedResult: IStudentProfileApp | IStudentProfileApp[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StudentProfileAppService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a StudentProfileApp', () => {
      const studentProfile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(studentProfile).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StudentProfileApp', () => {
      const studentProfile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(studentProfile).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StudentProfileApp', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StudentProfileApp', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StudentProfileApp', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStudentProfileAppToCollectionIfMissing', () => {
      it('should add a StudentProfileApp to an empty array', () => {
        const studentProfile: IStudentProfileApp = sampleWithRequiredData;
        expectedResult = service.addStudentProfileAppToCollectionIfMissing([], studentProfile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(studentProfile);
      });

      it('should not add a StudentProfileApp to an array that contains it', () => {
        const studentProfile: IStudentProfileApp = sampleWithRequiredData;
        const studentProfileCollection: IStudentProfileApp[] = [
          {
            ...studentProfile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStudentProfileAppToCollectionIfMissing(studentProfileCollection, studentProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StudentProfileApp to an array that doesn't contain it", () => {
        const studentProfile: IStudentProfileApp = sampleWithRequiredData;
        const studentProfileCollection: IStudentProfileApp[] = [sampleWithPartialData];
        expectedResult = service.addStudentProfileAppToCollectionIfMissing(studentProfileCollection, studentProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(studentProfile);
      });

      it('should add only unique StudentProfileApp to an array', () => {
        const studentProfileArray: IStudentProfileApp[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const studentProfileCollection: IStudentProfileApp[] = [sampleWithRequiredData];
        expectedResult = service.addStudentProfileAppToCollectionIfMissing(studentProfileCollection, ...studentProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const studentProfile: IStudentProfileApp = sampleWithRequiredData;
        const studentProfile2: IStudentProfileApp = sampleWithPartialData;
        expectedResult = service.addStudentProfileAppToCollectionIfMissing([], studentProfile, studentProfile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(studentProfile);
        expect(expectedResult).toContain(studentProfile2);
      });

      it('should accept null and undefined values', () => {
        const studentProfile: IStudentProfileApp = sampleWithRequiredData;
        expectedResult = service.addStudentProfileAppToCollectionIfMissing([], null, studentProfile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(studentProfile);
      });

      it('should return initial array if no StudentProfileApp is added', () => {
        const studentProfileCollection: IStudentProfileApp[] = [sampleWithRequiredData];
        expectedResult = service.addStudentProfileAppToCollectionIfMissing(studentProfileCollection, undefined, null);
        expect(expectedResult).toEqual(studentProfileCollection);
      });
    });

    describe('compareStudentProfileApp', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStudentProfileApp(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 475 };
        const entity2 = null;

        const compareResult1 = service.compareStudentProfileApp(entity1, entity2);
        const compareResult2 = service.compareStudentProfileApp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 475 };
        const entity2 = { id: 14453 };

        const compareResult1 = service.compareStudentProfileApp(entity1, entity2);
        const compareResult2 = service.compareStudentProfileApp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 475 };
        const entity2 = { id: 475 };

        const compareResult1 = service.compareStudentProfileApp(entity1, entity2);
        const compareResult2 = service.compareStudentProfileApp(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
