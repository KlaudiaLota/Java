import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IInstructorApp } from '../instructor-app.model';

@Component({
  selector: 'jhi-instructor-app-detail',
  templateUrl: './instructor-app-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class InstructorAppDetailComponent {
  instructor = input<IInstructorApp | null>(null);

  previousState(): void {
    window.history.back();
  }
}
