import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IInstructorApp } from '../instructor-app.model';
import { InstructorAppService } from '../service/instructor-app.service';
import { InstructorAppFormGroup, InstructorAppFormService } from './instructor-app-form.service';

@Component({
  selector: 'jhi-instructor-app-update',
  templateUrl: './instructor-app-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InstructorAppUpdateComponent implements OnInit {
  isSaving = false;
  instructor: IInstructorApp | null = null;

  protected instructorService = inject(InstructorAppService);
  protected instructorFormService = inject(InstructorAppFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InstructorAppFormGroup = this.instructorFormService.createInstructorAppFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ instructor }) => {
      this.instructor = instructor;
      if (instructor) {
        this.updateForm(instructor);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const instructor = this.instructorFormService.getInstructorApp(this.editForm);
    if (instructor.id !== null) {
      this.subscribeToSaveResponse(this.instructorService.update(instructor));
    } else {
      this.subscribeToSaveResponse(this.instructorService.create(instructor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInstructorApp>>): void {
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

  protected updateForm(instructor: IInstructorApp): void {
    this.instructor = instructor;
    this.instructorFormService.resetForm(this.editForm, instructor);
  }
}
