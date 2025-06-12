import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { InstructorAppService } from '../service/instructor-app.service';
import { IInstructorApp } from '../instructor-app.model';
import { InstructorAppFormService } from './instructor-app-form.service';

import { InstructorAppUpdateComponent } from './instructor-app-update.component';

describe('InstructorApp Management Update Component', () => {
  let comp: InstructorAppUpdateComponent;
  let fixture: ComponentFixture<InstructorAppUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let instructorFormService: InstructorAppFormService;
  let instructorService: InstructorAppService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InstructorAppUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(InstructorAppUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InstructorAppUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    instructorFormService = TestBed.inject(InstructorAppFormService);
    instructorService = TestBed.inject(InstructorAppService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const instructor: IInstructorApp = { id: 32448 };

      activatedRoute.data = of({ instructor });
      comp.ngOnInit();

      expect(comp.instructor).toEqual(instructor);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstructorApp>>();
      const instructor = { id: 14207 };
      jest.spyOn(instructorFormService, 'getInstructorApp').mockReturnValue(instructor);
      jest.spyOn(instructorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ instructor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: instructor }));
      saveSubject.complete();

      // THEN
      expect(instructorFormService.getInstructorApp).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(instructorService.update).toHaveBeenCalledWith(expect.objectContaining(instructor));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstructorApp>>();
      const instructor = { id: 14207 };
      jest.spyOn(instructorFormService, 'getInstructorApp').mockReturnValue({ id: null });
      jest.spyOn(instructorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ instructor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: instructor }));
      saveSubject.complete();

      // THEN
      expect(instructorFormService.getInstructorApp).toHaveBeenCalled();
      expect(instructorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInstructorApp>>();
      const instructor = { id: 14207 };
      jest.spyOn(instructorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ instructor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(instructorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
