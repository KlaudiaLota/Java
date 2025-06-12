import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CourseAppDetailComponent } from './course-app-detail.component';

describe('CourseApp Management Detail Component', () => {
  let comp: CourseAppDetailComponent;
  let fixture: ComponentFixture<CourseAppDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseAppDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./course-app-detail.component').then(m => m.CourseAppDetailComponent),
              resolve: { course: () => of({ id: 2858 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CourseAppDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseAppDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load course on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CourseAppDetailComponent);

      // THEN
      expect(instance.course()).toEqual(expect.objectContaining({ id: 2858 }));
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
