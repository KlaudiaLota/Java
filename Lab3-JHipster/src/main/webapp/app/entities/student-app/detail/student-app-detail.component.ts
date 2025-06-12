import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IStudentApp } from '../student-app.model';

@Component({
  selector: 'jhi-student-app-detail',
  templateUrl: './student-app-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class StudentAppDetailComponent {
  student = input<IStudentApp | null>(null);

  previousState(): void {
    window.history.back();
  }
}
