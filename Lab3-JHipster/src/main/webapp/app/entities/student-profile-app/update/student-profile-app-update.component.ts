import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudentProfileApp } from '../student-profile-app.model';
import { StudentProfileAppService } from '../service/student-profile-app.service';
import { StudentProfileAppFormGroup, StudentProfileAppFormService } from './student-profile-app-form.service';

@Component({
  selector: 'jhi-student-profile-app-update',
  templateUrl: './student-profile-app-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StudentProfileAppUpdateComponent implements OnInit {
  isSaving = false;
  studentProfile: IStudentProfileApp | null = null;

  protected studentProfileService = inject(StudentProfileAppService);
  protected studentProfileFormService = inject(StudentProfileAppFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StudentProfileAppFormGroup = this.studentProfileFormService.createStudentProfileAppFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ studentProfile }) => {
      this.studentProfile = studentProfile;
      if (studentProfile) {
        this.updateForm(studentProfile);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const studentProfile = this.studentProfileFormService.getStudentProfileApp(this.editForm);
    if (studentProfile.id !== null) {
      this.subscribeToSaveResponse(this.studentProfileService.update(studentProfile));
    } else {
      this.subscribeToSaveResponse(this.studentProfileService.create(studentProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStudentProfileApp>>): void {
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

  protected updateForm(studentProfile: IStudentProfileApp): void {
    this.studentProfile = studentProfile;
    this.studentProfileFormService.resetForm(this.editForm, studentProfile);
  }
}
