import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../student-app.test-samples';

import { StudentAppFormService } from './student-app-form.service';

describe('StudentApp Form Service', () => {
  let service: StudentAppFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudentAppFormService);
  });

  describe('Service methods', () => {
    describe('createStudentAppFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStudentAppFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            birthDate: expect.any(Object),
            gender: expect.any(Object),
            email: expect.any(Object),
            registrationDate: expect.any(Object),
            profile: expect.any(Object),
            courses: expect.any(Object),
          }),
        );
      });

      it('passing IStudentApp should create a new form with FormGroup', () => {
        const formGroup = service.createStudentAppFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            birthDate: expect.any(Object),
            gender: expect.any(Object),
            email: expect.any(Object),
            registrationDate: expect.any(Object),
            profile: expect.any(Object),
            courses: expect.any(Object),
          }),
        );
      });
    });

    describe('getStudentApp', () => {
      it('should return NewStudentApp for default StudentApp initial value', () => {
        const formGroup = service.createStudentAppFormGroup(sampleWithNewData);

        const student = service.getStudentApp(formGroup) as any;

        expect(student).toMatchObject(sampleWithNewData);
      });

      it('should return NewStudentApp for empty StudentApp initial value', () => {
        const formGroup = service.createStudentAppFormGroup();

        const student = service.getStudentApp(formGroup) as any;

        expect(student).toMatchObject({});
      });

      it('should return IStudentApp', () => {
        const formGroup = service.createStudentAppFormGroup(sampleWithRequiredData);

        const student = service.getStudentApp(formGroup) as any;

        expect(student).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStudentApp should not enable id FormControl', () => {
        const formGroup = service.createStudentAppFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStudentApp should disable id FormControl', () => {
        const formGroup = service.createStudentAppFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
