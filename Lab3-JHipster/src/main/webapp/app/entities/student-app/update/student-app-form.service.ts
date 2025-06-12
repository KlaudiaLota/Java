import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStudentApp, NewStudentApp } from '../student-app.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStudentApp for edit and NewStudentAppFormGroupInput for create.
 */
type StudentAppFormGroupInput = IStudentApp | PartialWithRequiredKeyOf<NewStudentApp>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStudentApp | NewStudentApp> = Omit<T, 'registrationDate'> & {
  registrationDate?: string | null;
};

type StudentAppFormRawValue = FormValueOf<IStudentApp>;

type NewStudentAppFormRawValue = FormValueOf<NewStudentApp>;

type StudentAppFormDefaults = Pick<NewStudentApp, 'id' | 'registrationDate' | 'courses'>;

type StudentAppFormGroupContent = {
  id: FormControl<StudentAppFormRawValue['id'] | NewStudentApp['id']>;
  firstName: FormControl<StudentAppFormRawValue['firstName']>;
  lastName: FormControl<StudentAppFormRawValue['lastName']>;
  birthDate: FormControl<StudentAppFormRawValue['birthDate']>;
  gender: FormControl<StudentAppFormRawValue['gender']>;
  email: FormControl<StudentAppFormRawValue['email']>;
  registrationDate: FormControl<StudentAppFormRawValue['registrationDate']>;
  profile: FormControl<StudentAppFormRawValue['profile']>;
  courses: FormControl<StudentAppFormRawValue['courses']>;
};

export type StudentAppFormGroup = FormGroup<StudentAppFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StudentAppFormService {
  createStudentAppFormGroup(student: StudentAppFormGroupInput = { id: null }): StudentAppFormGroup {
    const studentRawValue = this.convertStudentAppToStudentAppRawValue({
      ...this.getFormDefaults(),
      ...student,
    });
    return new FormGroup<StudentAppFormGroupContent>({
      id: new FormControl(
        { value: studentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(studentRawValue.firstName),
      lastName: new FormControl(studentRawValue.lastName),
      birthDate: new FormControl(studentRawValue.birthDate),
      gender: new FormControl(studentRawValue.gender),
      email: new FormControl(studentRawValue.email),
      registrationDate: new FormControl(studentRawValue.registrationDate),
      profile: new FormControl(studentRawValue.profile),
      courses: new FormControl(studentRawValue.courses ?? []),
    });
  }

  getStudentApp(form: StudentAppFormGroup): IStudentApp | NewStudentApp {
    return this.convertStudentAppRawValueToStudentApp(form.getRawValue() as StudentAppFormRawValue | NewStudentAppFormRawValue);
  }

  resetForm(form: StudentAppFormGroup, student: StudentAppFormGroupInput): void {
    const studentRawValue = this.convertStudentAppToStudentAppRawValue({ ...this.getFormDefaults(), ...student });
    form.reset(
      {
        ...studentRawValue,
        id: { value: studentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StudentAppFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      registrationDate: currentTime,
      courses: [],
    };
  }

  private convertStudentAppRawValueToStudentApp(
    rawStudentApp: StudentAppFormRawValue | NewStudentAppFormRawValue,
  ): IStudentApp | NewStudentApp {
    return {
      ...rawStudentApp,
      registrationDate: dayjs(rawStudentApp.registrationDate, DATE_TIME_FORMAT),
    };
  }

  private convertStudentAppToStudentAppRawValue(
    student: IStudentApp | (Partial<NewStudentApp> & StudentAppFormDefaults),
  ): StudentAppFormRawValue | PartialWithRequiredKeyOf<NewStudentAppFormRawValue> {
    return {
      ...student,
      registrationDate: student.registrationDate ? student.registrationDate.format(DATE_TIME_FORMAT) : undefined,
      courses: student.courses ?? [],
    };
  }
}
