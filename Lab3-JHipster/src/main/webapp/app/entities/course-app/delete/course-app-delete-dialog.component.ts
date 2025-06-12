import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICourseApp } from '../course-app.model';
import { CourseAppService } from '../service/course-app.service';

@Component({
  templateUrl: './course-app-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CourseAppDeleteDialogComponent {
  course?: ICourseApp;

  protected courseService = inject(CourseAppService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.courseService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
