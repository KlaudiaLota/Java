import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICourseApp, NewCourseApp } from '../course-app.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICourseApp for edit and NewCourseAppFormGroupInput for create.
 */
type CourseAppFormGroupInput = ICourseApp | PartialWithRequiredKeyOf<NewCourseApp>;

type CourseAppFormDefaults = Pick<NewCourseApp, 'id' | 'students'>;

type CourseAppFormGroupContent = {
  id: FormControl<ICourseApp['id'] | NewCourseApp['id']>;
  title: FormControl<ICourseApp['title']>;
  description: FormControl<ICourseApp['description']>;
  startDate: FormControl<ICourseApp['startDate']>;
  endDate: FormControl<ICourseApp['endDate']>;
  level: FormControl<ICourseApp['level']>;
  price: FormControl<ICourseApp['price']>;
  students: FormControl<ICourseApp['students']>;
  instructor: FormControl<ICourseApp['instructor']>;
};

export type CourseAppFormGroup = FormGroup<CourseAppFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CourseAppFormService {
  createCourseAppFormGroup(course: CourseAppFormGroupInput = { id: null }): CourseAppFormGroup {
    const courseRawValue = {
      ...this.getFormDefaults(),
      ...course,
    };
    return new FormGroup<CourseAppFormGroupContent>({
      id: new FormControl(
        { value: courseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(courseRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(courseRawValue.description),
      startDate: new FormControl(courseRawValue.startDate),
      endDate: new FormControl(courseRawValue.endDate),
      level: new FormControl(courseRawValue.level),
      price: new FormControl(courseRawValue.price),
      students: new FormControl(courseRawValue.students ?? []),
      instructor: new FormControl(courseRawValue.instructor),
    });
  }

  getCourseApp(form: CourseAppFormGroup): ICourseApp | NewCourseApp {
    return form.getRawValue() as ICourseApp | NewCourseApp;
  }

  resetForm(form: CourseAppFormGroup, course: CourseAppFormGroupInput): void {
    const courseRawValue = { ...this.getFormDefaults(), ...course };
    form.reset(
      {
        ...courseRawValue,
        id: { value: courseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CourseAppFormDefaults {
    return {
      id: null,
      students: [],
    };
  }
}
