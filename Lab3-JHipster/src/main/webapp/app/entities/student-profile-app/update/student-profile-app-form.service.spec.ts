import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../student-profile-app.test-samples';

import { StudentProfileAppFormService } from './student-profile-app-form.service';

describe('StudentProfileApp Form Service', () => {
  let service: StudentProfileAppFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudentProfileAppFormService);
  });

  describe('Service methods', () => {
    describe('createStudentProfileAppFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStudentProfileAppFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            bio: expect.any(Object),
            linkedinUrl: expect.any(Object),
            githubUrl: expect.any(Object),
            profilePictureUrl: expect.any(Object),
          }),
        );
      });

      it('passing IStudentProfileApp should create a new form with FormGroup', () => {
        const formGroup = service.createStudentProfileAppFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            bio: expect.any(Object),
            linkedinUrl: expect.any(Object),
            githubUrl: expect.any(Object),
            profilePictureUrl: expect.any(Object),
          }),
        );
      });
    });

    describe('getStudentProfileApp', () => {
      it('should return NewStudentProfileApp for default StudentProfileApp initial value', () => {
        const formGroup = service.createStudentProfileAppFormGroup(sampleWithNewData);

        const studentProfile = service.getStudentProfileApp(formGroup) as any;

        expect(studentProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewStudentProfileApp for empty StudentProfileApp initial value', () => {
        const formGroup = service.createStudentProfileAppFormGroup();

        const studentProfile = service.getStudentProfileApp(formGroup) as any;

        expect(studentProfile).toMatchObject({});
      });

      it('should return IStudentProfileApp', () => {
        const formGroup = service.createStudentProfileAppFormGroup(sampleWithRequiredData);

        const studentProfile = service.getStudentProfileApp(formGroup) as any;

        expect(studentProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStudentProfileApp should not enable id FormControl', () => {
        const formGroup = service.createStudentProfileAppFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStudentProfileApp should disable id FormControl', () => {
        const formGroup = service.createStudentProfileAppFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
