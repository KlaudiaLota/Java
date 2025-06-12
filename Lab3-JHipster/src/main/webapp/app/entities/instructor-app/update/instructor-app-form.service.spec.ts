import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../instructor-app.test-samples';

import { InstructorAppFormService } from './instructor-app-form.service';

describe('InstructorApp Form Service', () => {
  let service: InstructorAppFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InstructorAppFormService);
  });

  describe('Service methods', () => {
    describe('createInstructorAppFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInstructorAppFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            email: expect.any(Object),
            bio: expect.any(Object),
            hireDate: expect.any(Object),
            rating: expect.any(Object),
          }),
        );
      });

      it('passing IInstructorApp should create a new form with FormGroup', () => {
        const formGroup = service.createInstructorAppFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            email: expect.any(Object),
            bio: expect.any(Object),
            hireDate: expect.any(Object),
            rating: expect.any(Object),
          }),
        );
      });
    });

    describe('getInstructorApp', () => {
      it('should return NewInstructorApp for default InstructorApp initial value', () => {
        const formGroup = service.createInstructorAppFormGroup(sampleWithNewData);

        const instructor = service.getInstructorApp(formGroup) as any;

        expect(instructor).toMatchObject(sampleWithNewData);
      });

      it('should return NewInstructorApp for empty InstructorApp initial value', () => {
        const formGroup = service.createInstructorAppFormGroup();

        const instructor = service.getInstructorApp(formGroup) as any;

        expect(instructor).toMatchObject({});
      });

      it('should return IInstructorApp', () => {
        const formGroup = service.createInstructorAppFormGroup(sampleWithRequiredData);

        const instructor = service.getInstructorApp(formGroup) as any;

        expect(instructor).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInstructorApp should not enable id FormControl', () => {
        const formGroup = service.createInstructorAppFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInstructorApp should disable id FormControl', () => {
        const formGroup = service.createInstructorAppFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
