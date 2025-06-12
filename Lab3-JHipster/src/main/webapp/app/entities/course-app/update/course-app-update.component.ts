import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudentApp } from 'app/entities/student-app/student-app.model';
import { StudentAppService } from 'app/entities/student-app/service/student-app.service';
import { IInstructorApp } from 'app/entities/instructor-app/instructor-app.model';
import { InstructorAppService } from 'app/entities/instructor-app/service/instructor-app.service';
import { CourseLevel } from 'app/entities/enumerations/course-level.model';
import { CourseAppService } from '../service/course-app.service';
import { ICourseApp } from '../course-app.model';
import { CourseAppFormGroup, CourseAppFormService } from './course-app-form.service';

@Component({
  selector: 'jhi-course-app-update',
  templateUrl: './course-app-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CourseAppUpdateComponent implements OnInit {
  isSaving = false;
  course: ICourseApp | null = null;
  courseLevelValues = Object.keys(CourseLevel);

  studentsSharedCollection: IStudentApp[] = [];
  instructorsSharedCollection: IInstructorApp[] = [];

  protected courseService = inject(CourseAppService);
  protected courseFormService = inject(CourseAppFormService);
  protected studentService = inject(StudentAppService);
  protected instructorService = inject(InstructorAppService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CourseAppFormGroup = this.courseFormService.createCourseAppFormGroup();

  compareStudentApp = (o1: IStudentApp | null, o2: IStudentApp | null): boolean => this.studentService.compareStudentApp(o1, o2);

  compareInstructorApp = (o1: IInstructorApp | null, o2: IInstructorApp | null): boolean =>
    this.instructorService.compareInstructorApp(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ course }) => {
      this.course = course;
      if (course) {
        this.updateForm(course);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const course = this.courseFormService.getCourseApp(this.editForm);
    if (course.id !== null) {
      this.subscribeToSaveResponse(this.courseService.update(course));
    } else {
      this.subscribeToSaveResponse(this.courseService.create(course));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICourseApp>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(course: ICourseApp): void {
    this.course = course;
    this.courseFormService.resetForm(this.editForm, course);

    this.studentsSharedCollection = this.studentService.addStudentAppToCollectionIfMissing<IStudentApp>(
      this.studentsSharedCollection,
      ...(course.students ?? []),
    );
    this.instructorsSharedCollection = this.instructorService.addInstructorAppToCollectionIfMissing<IInstructorApp>(
      this.instructorsSharedCollection,
      course.instructor,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudentApp[]>) => res.body ?? []))
      .pipe(
        map((students: IStudentApp[]) =>
          this.studentService.addStudentAppToCollectionIfMissing<IStudentApp>(students, ...(this.course?.students ?? [])),
        ),
      )
      .subscribe((students: IStudentApp[]) => (this.studentsSharedCollection = students));

    this.instructorService
      .query()
      .pipe(map((res: HttpResponse<IInstructorApp[]>) => res.body ?? []))
      .pipe(
        map((instructors: IInstructorApp[]) =>
          this.instructorService.addInstructorAppToCollectionIfMissing<IInstructorApp>(instructors, this.course?.instructor),
        ),
      )
      .subscribe((instructors: IInstructorApp[]) => (this.instructorsSharedCollection = instructors));
  }
}
