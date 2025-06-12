import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStudentProfileApp, NewStudentProfileApp } from '../student-profile-app.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStudentProfileApp for edit and NewStudentProfileAppFormGroupInput for create.
 */
type StudentProfileAppFormGroupInput = IStudentProfileApp | PartialWithRequiredKeyOf<NewStudentProfileApp>;

type StudentProfileAppFormDefaults = Pick<NewStudentProfileApp, 'id'>;

type StudentProfileAppFormGroupContent = {
  id: FormControl<IStudentProfileApp['id'] | NewStudentProfileApp['id']>;
  bio: FormControl<IStudentProfileApp['bio']>;
  linkedinUrl: FormControl<IStudentProfileApp['linkedinUrl']>;
  githubUrl: FormControl<IStudentProfileApp['githubUrl']>;
  profilePictureUrl: FormControl<IStudentProfileApp['profilePictureUrl']>;
};

export type StudentProfileAppFormGroup = FormGroup<StudentProfileAppFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StudentProfileAppFormService {
  createStudentProfileAppFormGroup(studentProfile: StudentProfileAppFormGroupInput = { id: null }): StudentProfileAppFormGroup {
    const studentProfileRawValue = {
      ...this.getFormDefaults(),
      ...studentProfile,
    };
    return new FormGroup<StudentProfileAppFormGroupContent>({
      id: new FormControl(
        { value: studentProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      bio: new FormControl(studentProfileRawValue.bio),
      linkedinUrl: new FormControl(studentProfileRawValue.linkedinUrl),
      githubUrl: new FormControl(studentProfileRawValue.githubUrl),
      profilePictureUrl: new FormControl(studentProfileRawValue.profilePictureUrl),
    });
  }

  getStudentProfileApp(form: StudentProfileAppFormGroup): IStudentProfileApp | NewStudentProfileApp {
    return form.getRawValue() as IStudentProfileApp | NewStudentProfileApp;
  }

  resetForm(form: StudentProfileAppFormGroup, studentProfile: StudentProfileAppFormGroupInput): void {
    const studentProfileRawValue = { ...this.getFormDefaults(), ...studentProfile };
    form.reset(
      {
        ...studentProfileRawValue,
        id: { value: studentProfileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StudentProfileAppFormDefaults {
    return {
      id: null,
    };
  }
}
