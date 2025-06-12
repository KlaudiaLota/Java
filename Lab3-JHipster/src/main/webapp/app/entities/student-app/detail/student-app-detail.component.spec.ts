import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { StudentAppDetailComponent } from './student-app-detail.component';

describe('StudentApp Management Detail Component', () => {
  let comp: StudentAppDetailComponent;
  let fixture: ComponentFixture<StudentAppDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentAppDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./student-app-detail.component').then(m => m.StudentAppDetailComponent),
              resolve: { student: () => of({ id: 9978 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(StudentAppDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentAppDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load student on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', StudentAppDetailComponent);

      // THEN
      expect(instance.student()).toEqual(expect.objectContaining({ id: 9978 }));
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
