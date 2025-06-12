import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { StudentProfileAppService } from '../service/student-profile-app.service';
import { IStudentProfileApp } from '../student-profile-app.model';
import { StudentProfileAppFormService } from './student-profile-app-form.service';

import { StudentProfileAppUpdateComponent } from './student-profile-app-update.component';

describe('StudentProfileApp Management Update Component', () => {
  let comp: StudentProfileAppUpdateComponent;
  let fixture: ComponentFixture<StudentProfileAppUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let studentProfileFormService: StudentProfileAppFormService;
  let studentProfileService: StudentProfileAppService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StudentProfileAppUpdateComponent],
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
      .overrideTemplate(StudentProfileAppUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StudentProfileAppUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    studentProfileFormService = TestBed.inject(StudentProfileAppFormService);
    studentProfileService = TestBed.inject(StudentProfileAppService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const studentProfile: IStudentProfileApp = { id: 14453 };

      activatedRoute.data = of({ studentProfile });
      comp.ngOnInit();

      expect(comp.studentProfile).toEqual(studentProfile);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudentProfileApp>>();
      const studentProfile = { id: 475 };
      jest.spyOn(studentProfileFormService, 'getStudentProfileApp').mockReturnValue(studentProfile);
      jest.spyOn(studentProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studentProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: studentProfile }));
      saveSubject.complete();

      // THEN
      expect(studentProfileFormService.getStudentProfileApp).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(studentProfileService.update).toHaveBeenCalledWith(expect.objectContaining(studentProfile));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudentProfileApp>>();
      const studentProfile = { id: 475 };
      jest.spyOn(studentProfileFormService, 'getStudentProfileApp').mockReturnValue({ id: null });
      jest.spyOn(studentProfileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studentProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: studentProfile }));
      saveSubject.complete();

      // THEN
      expect(studentProfileFormService.getStudentProfileApp).toHaveBeenCalled();
      expect(studentProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudentProfileApp>>();
      const studentProfile = { id: 475 };
      jest.spyOn(studentProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studentProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(studentProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
