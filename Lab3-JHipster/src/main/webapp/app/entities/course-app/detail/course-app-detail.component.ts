import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ICourseApp } from '../course-app.model';

@Component({
  selector: 'jhi-course-app-detail',
  templateUrl: './course-app-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class CourseAppDetailComponent {
  course = input<ICourseApp | null>(null);

  previousState(): void {
    window.history.back();
  }
}
