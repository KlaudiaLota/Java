import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../course-app.test-samples';

import { CourseAppFormService } from './course-app-form.service';

describe('CourseApp Form Service', () => {
  let service: CourseAppFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CourseAppFormService);
  });

  describe('Service methods', () => {
    describe('createCourseAppFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCourseAppFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            level: expect.any(Object),
            price: expect.any(Object),
            students: expect.any(Object),
            instructor: expect.any(Object),
          }),
        );
      });

      it('passing ICourseApp should create a new form with FormGroup', () => {
        const formGroup = service.createCourseAppFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            level: expect.any(Object),
            price: expect.any(Object),
            students: expect.any(Object),
            instructor: expect.any(Object),
          }),
        );
      });
    });

    describe('getCourseApp', () => {
      it('should return NewCourseApp for default CourseApp initial value', () => {
        const formGroup = service.createCourseAppFormGroup(sampleWithNewData);

        const course = service.getCourseApp(formGroup) as any;

        expect(course).toMatchObject(sampleWithNewData);
      });

      it('should return NewCourseApp for empty CourseApp initial value', () => {
        const formGroup = service.createCourseAppFormGroup();

        const course = service.getCourseApp(formGroup) as any;

        expect(course).toMatchObject({});
      });

      it('should return ICourseApp', () => {
        const formGroup = service.createCourseAppFormGroup(sampleWithRequiredData);

        const course = service.getCourseApp(formGroup) as any;

        expect(course).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICourseApp should not enable id FormControl', () => {
        const formGroup = service.createCourseAppFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCourseApp should disable id FormControl', () => {
        const formGroup = service.createCourseAppFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
