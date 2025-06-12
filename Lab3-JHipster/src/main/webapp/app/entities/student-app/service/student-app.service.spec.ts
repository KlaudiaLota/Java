import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IStudentApp } from '../student-app.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../student-app.test-samples';

import { RestStudentApp, StudentAppService } from './student-app.service';

const requireRestSample: RestStudentApp = {
  ...sampleWithRequiredData,
  birthDate: sampleWithRequiredData.birthDate?.format(DATE_FORMAT),
  registrationDate: sampleWithRequiredData.registrationDate?.toJSON(),
};

describe('StudentApp Service', () => {
  let service: StudentAppService;
  let httpMock: HttpTestingController;
  let expectedResult: IStudentApp | IStudentApp[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StudentAppService);
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

    it('should create a StudentApp', () => {
      const student = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(student).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StudentApp', () => {
      const student = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(student).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StudentApp', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StudentApp', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StudentApp', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStudentAppToCollectionIfMissing', () => {
      it('should add a StudentApp to an empty array', () => {
        const student: IStudentApp = sampleWithRequiredData;
        expectedResult = service.addStudentAppToCollectionIfMissing([], student);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(student);
      });

      it('should not add a StudentApp to an array that contains it', () => {
        const student: IStudentApp = sampleWithRequiredData;
        const studentCollection: IStudentApp[] = [
          {
            ...student,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStudentAppToCollectionIfMissing(studentCollection, student);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StudentApp to an array that doesn't contain it", () => {
        const student: IStudentApp = sampleWithRequiredData;
        const studentCollection: IStudentApp[] = [sampleWithPartialData];
        expectedResult = service.addStudentAppToCollectionIfMissing(studentCollection, student);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(student);
      });

      it('should add only unique StudentApp to an array', () => {
        const studentArray: IStudentApp[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const studentCollection: IStudentApp[] = [sampleWithRequiredData];
        expectedResult = service.addStudentAppToCollectionIfMissing(studentCollection, ...studentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const student: IStudentApp = sampleWithRequiredData;
        const student2: IStudentApp = sampleWithPartialData;
        expectedResult = service.addStudentAppToCollectionIfMissing([], student, student2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(student);
        expect(expectedResult).toContain(student2);
      });

      it('should accept null and undefined values', () => {
        const student: IStudentApp = sampleWithRequiredData;
        expectedResult = service.addStudentAppToCollectionIfMissing([], null, student, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(student);
      });

      it('should return initial array if no StudentApp is added', () => {
        const studentCollection: IStudentApp[] = [sampleWithRequiredData];
        expectedResult = service.addStudentAppToCollectionIfMissing(studentCollection, undefined, null);
        expect(expectedResult).toEqual(studentCollection);
      });
    });

    describe('compareStudentApp', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStudentApp(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9978 };
        const entity2 = null;

        const compareResult1 = service.compareStudentApp(entity1, entity2);
        const compareResult2 = service.compareStudentApp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9978 };
        const entity2 = { id: 22718 };

        const compareResult1 = service.compareStudentApp(entity1, entity2);
        const compareResult2 = service.compareStudentApp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9978 };
        const entity2 = { id: 9978 };

        const compareResult1 = service.compareStudentApp(entity1, entity2);
        const compareResult2 = service.compareStudentApp(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
