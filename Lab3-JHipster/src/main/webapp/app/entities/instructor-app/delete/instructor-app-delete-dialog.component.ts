import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IInstructorApp } from '../instructor-app.model';
import { InstructorAppService } from '../service/instructor-app.service';

@Component({
  templateUrl: './instructor-app-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class InstructorAppDeleteDialogComponent {
  instructor?: IInstructorApp;

  protected instructorService = inject(InstructorAppService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.instructorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
