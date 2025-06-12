import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInstructorApp, NewInstructorApp } from '../instructor-app.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInstructorApp for edit and NewInstructorAppFormGroupInput for create.
 */
type InstructorAppFormGroupInput = IInstructorApp | PartialWithRequiredKeyOf<NewInstructorApp>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IInstructorApp | NewInstructorApp> = Omit<T, 'hireDate'> & {
  hireDate?: string | null;
};

type InstructorAppFormRawValue = FormValueOf<IInstructorApp>;

type NewInstructorAppFormRawValue = FormValueOf<NewInstructorApp>;

type InstructorAppFormDefaults = Pick<NewInstructorApp, 'id' | 'hireDate'>;

type InstructorAppFormGroupContent = {
  id: FormControl<InstructorAppFormRawValue['id'] | NewInstructorApp['id']>;
  firstName: FormControl<InstructorAppFormRawValue['firstName']>;
  lastName: FormControl<InstructorAppFormRawValue['lastName']>;
  email: FormControl<InstructorAppFormRawValue['email']>;
  bio: FormControl<InstructorAppFormRawValue['bio']>;
  hireDate: FormControl<InstructorAppFormRawValue['hireDate']>;
  rating: FormControl<InstructorAppFormRawValue['rating']>;
};

export type InstructorAppFormGroup = FormGroup<InstructorAppFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InstructorAppFormService {
  createInstructorAppFormGroup(instructor: InstructorAppFormGroupInput = { id: null }): InstructorAppFormGroup {
    const instructorRawValue = this.convertInstructorAppToInstructorAppRawValue({
      ...this.getFormDefaults(),
      ...instructor,
    });
    return new FormGroup<InstructorAppFormGroupContent>({
      id: new FormControl(
        { value: instructorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(instructorRawValue.firstName),
      lastName: new FormControl(instructorRawValue.lastName),
      email: new FormControl(instructorRawValue.email),
      bio: new FormControl(instructorRawValue.bio),
      hireDate: new FormControl(instructorRawValue.hireDate),
      rating: new FormControl(instructorRawValue.rating),
    });
  }

  getInstructorApp(form: InstructorAppFormGroup): IInstructorApp | NewInstructorApp {
    return this.convertInstructorAppRawValueToInstructorApp(form.getRawValue() as InstructorAppFormRawValue | NewInstructorAppFormRawValue);
  }

  resetForm(form: InstructorAppFormGroup, instructor: InstructorAppFormGroupInput): void {
    const instructorRawValue = this.convertInstructorAppToInstructorAppRawValue({ ...this.getFormDefaults(), ...instructor });
    form.reset(
      {
        ...instructorRawValue,
        id: { value: instructorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InstructorAppFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      hireDate: currentTime,
    };
  }

  private convertInstructorAppRawValueToInstructorApp(
    rawInstructorApp: InstructorAppFormRawValue | NewInstructorAppFormRawValue,
  ): IInstructorApp | NewInstructorApp {
    return {
      ...rawInstructorApp,
      hireDate: dayjs(rawInstructorApp.hireDate, DATE_TIME_FORMAT),
    };
  }

  private convertInstructorAppToInstructorAppRawValue(
    instructor: IInstructorApp | (Partial<NewInstructorApp> & InstructorAppFormDefaults),
  ): InstructorAppFormRawValue | PartialWithRequiredKeyOf<NewInstructorAppFormRawValue> {
    return {
      ...instructor,
      hireDate: instructor.hireDate ? instructor.hireDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
