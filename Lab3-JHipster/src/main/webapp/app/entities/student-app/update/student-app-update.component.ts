import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudentProfileApp } from 'app/entities/student-profile-app/student-profile-app.model';
import { StudentProfileAppService } from 'app/entities/student-profile-app/service/student-profile-app.service';
import { ICourseApp } from 'app/entities/course-app/course-app.model';
import { CourseAppService } from 'app/entities/course-app/service/course-app.service';
import { Gender } from 'app/entities/enumerations/gender.model';
import { StudentAppService } from '../service/student-app.service';
import { IStudentApp } from '../student-app.model';
import { StudentAppFormGroup, StudentAppFormService } from './student-app-form.service';

@Component({
  selector: 'jhi-student-app-update',
  templateUrl: './student-app-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StudentAppUpdateComponent implements OnInit {
  isSaving = false;
  student: IStudentApp | null = null;
  genderValues = Object.keys(Gender);

  profilesCollection: IStudentProfileApp[] = [];
  coursesSharedCollection: ICourseApp[] = [];

  protected studentService = inject(StudentAppService);
  protected studentFormService = inject(StudentAppFormService);
  protected studentProfileService = inject(StudentProfileAppService);
  protected courseService = inject(CourseAppService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StudentAppFormGroup = this.studentFormService.createStudentAppFormGroup();

  compareStudentProfileApp = (o1: IStudentProfileApp | null, o2: IStudentProfileApp | null): boolean =>
    this.studentProfileService.compareStudentProfileApp(o1, o2);

  compareCourseApp = (o1: ICourseApp | null, o2: ICourseApp | null): boolean => this.courseService.compareCourseApp(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ student }) => {
      this.student = student;
      if (student) {
        this.updateForm(student);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const student = this.studentFormService.getStudentApp(this.editForm);
    if (student.id !== null) {
      this.subscribeToSaveResponse(this.studentService.update(student));
    } else {
      this.subscribeToSaveResponse(this.studentService.create(student));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStudentApp>>): void {
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

  protected updateForm(student: IStudentApp): void {
    this.student = student;
    this.studentFormService.resetForm(this.editForm, student);

    this.profilesCollection = this.studentProfileService.addStudentProfileAppToCollectionIfMissing<IStudentProfileApp>(
      this.profilesCollection,
      student.profile,
    );
    this.coursesSharedCollection = this.courseService.addCourseAppToCollectionIfMissing<ICourseApp>(
      this.coursesSharedCollection,
      ...(student.courses ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentProfileService
      .query({ filter: 'student-is-null' })
      .pipe(map((res: HttpResponse<IStudentProfileApp[]>) => res.body ?? []))
      .pipe(
        map((studentProfiles: IStudentProfileApp[]) =>
          this.studentProfileService.addStudentProfileAppToCollectionIfMissing<IStudentProfileApp>(studentProfiles, this.student?.profile),
        ),
      )
      .subscribe((studentProfiles: IStudentProfileApp[]) => (this.profilesCollection = studentProfiles));

    this.courseService
      .query()
      .pipe(map((res: HttpResponse<ICourseApp[]>) => res.body ?? []))
      .pipe(
        map((courses: ICourseApp[]) =>
          this.courseService.addCourseAppToCollectionIfMissing<ICourseApp>(courses, ...(this.student?.courses ?? [])),
        ),
      )
      .subscribe((courses: ICourseApp[]) => (this.coursesSharedCollection = courses));
  }
}
