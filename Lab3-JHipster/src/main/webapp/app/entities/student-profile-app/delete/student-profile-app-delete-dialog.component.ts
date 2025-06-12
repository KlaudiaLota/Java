import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IStudentProfileApp } from '../student-profile-app.model';
import { StudentProfileAppService } from '../service/student-profile-app.service';

@Component({
  templateUrl: './student-profile-app-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class StudentProfileAppDeleteDialogComponent {
  studentProfile?: IStudentProfileApp;

  protected studentProfileService = inject(StudentProfileAppService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.studentProfileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
