import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { StudentProfileAppDetailComponent } from './student-profile-app-detail.component';

describe('StudentProfileApp Management Detail Component', () => {
  let comp: StudentProfileAppDetailComponent;
  let fixture: ComponentFixture<StudentProfileAppDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentProfileAppDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./student-profile-app-detail.component').then(m => m.StudentProfileAppDetailComponent),
              resolve: { studentProfile: () => of({ id: 475 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(StudentProfileAppDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentProfileAppDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load studentProfile on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', StudentProfileAppDetailComponent);

      // THEN
      expect(instance.studentProfile()).toEqual(expect.objectContaining({ id: 475 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
