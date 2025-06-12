import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { InstructorAppDetailComponent } from './instructor-app-detail.component';

describe('InstructorApp Management Detail Component', () => {
  let comp: InstructorAppDetailComponent;
  let fixture: ComponentFixture<InstructorAppDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InstructorAppDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./instructor-app-detail.component').then(m => m.InstructorAppDetailComponent),
              resolve: { instructor: () => of({ id: 14207 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(InstructorAppDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorAppDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load instructor on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', InstructorAppDetailComponent);

      // THEN
      expect(instance.instructor()).toEqual(expect.objectContaining({ id: 14207 }));
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
