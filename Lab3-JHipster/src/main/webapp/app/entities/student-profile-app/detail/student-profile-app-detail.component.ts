import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IStudentProfileApp } from '../student-profile-app.model';

@Component({
  selector: 'jhi-student-profile-app-detail',
  templateUrl: './student-profile-app-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class StudentProfileAppDetailComponent {
  studentProfile = input<IStudentProfileApp | null>(null);

  previousState(): void {
    window.history.back();
  }
}
