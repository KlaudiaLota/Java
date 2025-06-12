import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IStudentApp } from '../student-app.model';
import { StudentAppService } from '../service/student-app.service';

@Component({
  templateUrl: './student-app-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class StudentAppDeleteDialogComponent {
  student?: IStudentApp;

  protected studentService = inject(StudentAppService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.studentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
