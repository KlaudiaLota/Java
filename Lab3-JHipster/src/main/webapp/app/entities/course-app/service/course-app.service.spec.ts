import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ICourseApp } from '../course-app.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../course-app.test-samples';

import { CourseAppService, RestCourseApp } from './course-app.service';

const requireRestSample: RestCourseApp = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.format(DATE_FORMAT),
  endDate: sampleWithRequiredData.endDate?.format(DATE_FORMAT),
};

describe('CourseApp Service', () => {
  let service: CourseAppService;
  let httpMock: HttpTestingController;
  let expectedResult: ICourseApp | ICourseApp[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CourseAppService);
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

    it('should create a CourseApp', () => {
      const course = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(course).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CourseApp', () => {
      const course = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(course).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CourseApp', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CourseApp', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CourseApp', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCourseAppToCollectionIfMissing', () => {
      it('should add a CourseApp to an empty array', () => {
        const course: ICourseApp = sampleWithRequiredData;
        expectedResult = service.addCourseAppToCollectionIfMissing([], course);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(course);
      });

      it('should not add a CourseApp to an array that contains it', () => {
        const course: ICourseApp = sampleWithRequiredData;
        const courseCollection: ICourseApp[] = [
          {
            ...course,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCourseAppToCollectionIfMissing(courseCollection, course);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CourseApp to an array that doesn't contain it", () => {
        const course: ICourseApp = sampleWithRequiredData;
        const courseCollection: ICourseApp[] = [sampleWithPartialData];
        expectedResult = service.addCourseAppToCollectionIfMissing(courseCollection, course);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(course);
      });

      it('should add only unique CourseApp to an array', () => {
        const courseArray: ICourseApp[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const courseCollection: ICourseApp[] = [sampleWithRequiredData];
        expectedResult = service.addCourseAppToCollectionIfMissing(courseCollection, ...courseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const course: ICourseApp = sampleWithRequiredData;
        const course2: ICourseApp = sampleWithPartialData;
        expectedResult = service.addCourseAppToCollectionIfMissing([], course, course2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(course);
        expect(expectedResult).toContain(course2);
      });

      it('should accept null and undefined values', () => {
        const course: ICourseApp = sampleWithRequiredData;
        expectedResult = service.addCourseAppToCollectionIfMissing([], null, course, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(course);
      });

      it('should return initial array if no CourseApp is added', () => {
        const courseCollection: ICourseApp[] = [sampleWithRequiredData];
        expectedResult = service.addCourseAppToCollectionIfMissing(courseCollection, undefined, null);
        expect(expectedResult).toEqual(courseCollection);
      });
    });

    describe('compareCourseApp', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCourseApp(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2858 };
        const entity2 = null;

        const compareResult1 = service.compareCourseApp(entity1, entity2);
        const compareResult2 = service.compareCourseApp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2858 };
        const entity2 = { id: 3722 };

        const compareResult1 = service.compareCourseApp(entity1, entity2);
        const compareResult2 = service.compareCourseApp(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 2858 };
        const entity2 = { id: 2858 };

        const compareResult1 = service.compareCourseApp(entity1, entity2);
        const compareResult2 = service.compareCourseApp(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
