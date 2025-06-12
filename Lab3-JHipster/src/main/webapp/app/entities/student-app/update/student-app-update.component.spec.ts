import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudentProfileApp } from 'app/entities/student-profile-app/student-profile-app.model';
import { StudentProfileAppService } from 'app/entities/student-profile-app/service/student-profile-app.service';
import { ICourseApp } from 'app/entities/course-app/course-app.model';
import { CourseAppService } from 'app/entities/course-app/service/course-app.service';
import { IStudentApp } from '../student-app.model';
import { StudentAppService } from '../service/student-app.service';
import { StudentAppFormService } from './student-app-form.service';

import { StudentAppUpdateComponent } from './student-app-update.component';

describe('StudentApp Management Update Component', () => {
  let comp: StudentAppUpdateComponent;
  let fixture: ComponentFixture<StudentAppUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let studentFormService: StudentAppFormService;
  let studentService: StudentAppService;
  let studentProfileService: StudentProfileAppService;
  let courseService: CourseAppService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StudentAppUpdateComponent],
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
      .overrideTemplate(StudentAppUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StudentAppUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    studentFormService = TestBed.inject(StudentAppFormService);
    studentService = TestBed.inject(StudentAppService);
    studentProfileService = TestBed.inject(StudentProfileAppService);
    courseService = TestBed.inject(CourseAppService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call profile query and add missing value', () => {
      const student: IStudentApp = { id: 22718 };
      const profile: IStudentProfileApp = { id: 475 };
      student.profile = profile;

      const profileCollection: IStudentProfileApp[] = [{ id: 475 }];
      jest.spyOn(studentProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const expectedCollection: IStudentProfileApp[] = [profile, ...profileCollection];
      jest.spyOn(studentProfileService, 'addStudentProfileAppToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ student });
      comp.ngOnInit();

      expect(studentProfileService.query).toHaveBeenCalled();
      expect(studentProfileService.addStudentProfileAppToCollectionIfMissing).toHaveBeenCalledWith(profileCollection, profile);
      expect(comp.profilesCollection).toEqual(expectedCollection);
    });

    it('should call CourseApp query and add missing value', () => {
      const student: IStudentApp = { id: 22718 };
      const courses: ICourseApp[] = [{ id: 2858 }];
      student.courses = courses;

      const courseCollection: ICourseApp[] = [{ id: 2858 }];
      jest.spyOn(courseService, 'query').mockReturnValue(of(new HttpResponse({ body: courseCollection })));
      const additionalCourseApps = [...courses];
      const expectedCollection: ICourseApp[] = [...additionalCourseApps, ...courseCollection];
      jest.spyOn(courseService, 'addCourseAppToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ student });
      comp.ngOnInit();

      expect(courseService.query).toHaveBeenCalled();
      expect(courseService.addCourseAppToCollectionIfMissing).toHaveBeenCalledWith(
        courseCollection,
        ...additionalCourseApps.map(expect.objectContaining),
      );
      expect(comp.coursesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const student: IStudentApp = { id: 22718 };
      const profile: IStudentProfileApp = { id: 475 };
      student.profile = profile;
      const course: ICourseApp = { id: 2858 };
      student.courses = [course];

      activatedRoute.data = of({ student });
      comp.ngOnInit();

      expect(comp.profilesCollection).toContainEqual(profile);
      expect(comp.coursesSharedCollection).toContainEqual(course);
      expect(comp.student).toEqual(student);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudentApp>>();
      const student = { id: 9978 };
      jest.spyOn(studentFormService, 'getStudentApp').mockReturnValue(student);
      jest.spyOn(studentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ student });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: student }));
      saveSubject.complete();

      // THEN
      expect(studentFormService.getStudentApp).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(studentService.update).toHaveBeenCalledWith(expect.objectContaining(student));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudentApp>>();
      const student = { id: 9978 };
      jest.spyOn(studentFormService, 'getStudentApp').mockReturnValue({ id: null });
      jest.spyOn(studentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ student: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: student }));
      saveSubject.complete();

      // THEN
      expect(studentFormService.getStudentApp).toHaveBeenCalled();
      expect(studentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudentApp>>();
      const student = { id: 9978 };
      jest.spyOn(studentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ student });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(studentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStudentProfileApp', () => {
      it('should forward to studentProfileService', () => {
        const entity = { id: 475 };
        const entity2 = { id: 14453 };
        jest.spyOn(studentProfileService, 'compareStudentProfileApp');
        comp.compareStudentProfileApp(entity, entity2);
        expect(studentProfileService.compareStudentProfileApp).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCourseApp', () => {
      it('should forward to courseService', () => {
        const entity = { id: 2858 };
        const entity2 = { id: 3722 };
        jest.spyOn(courseService, 'compareCourseApp');
        comp.compareCourseApp(entity, entity2);
        expect(courseService.compareCourseApp).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
