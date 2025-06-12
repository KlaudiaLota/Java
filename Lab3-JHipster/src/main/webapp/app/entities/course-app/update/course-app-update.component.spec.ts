import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudentApp } from 'app/entities/student-app/student-app.model';
import { StudentAppService } from 'app/entities/student-app/service/student-app.service';
import { IInstructorApp } from 'app/entities/instructor-app/instructor-app.model';
import { InstructorAppService } from 'app/entities/instructor-app/service/instructor-app.service';
import { ICourseApp } from '../course-app.model';
import { CourseAppService } from '../service/course-app.service';
import { CourseAppFormService } from './course-app-form.service';

import { CourseAppUpdateComponent } from './course-app-update.component';

describe('CourseApp Management Update Component', () => {
  let comp: CourseAppUpdateComponent;
  let fixture: ComponentFixture<CourseAppUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let courseFormService: CourseAppFormService;
  let courseService: CourseAppService;
  let studentService: StudentAppService;
  let instructorService: InstructorAppService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CourseAppUpdateComponent],
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
      .overrideTemplate(CourseAppUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CourseAppUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    courseFormService = TestBed.inject(CourseAppFormService);
    courseService = TestBed.inject(CourseAppService);
    studentService = TestBed.inject(StudentAppService);
    instructorService = TestBed.inject(InstructorAppService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call StudentApp query and add missing value', () => {
      const course: ICourseApp = { id: 3722 };
      const students: IStudentApp[] = [{ id: 9978 }];
      course.students = students;

      const studentCollection: IStudentApp[] = [{ id: 9978 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudentApps = [...students];
      const expectedCollection: IStudentApp[] = [...additionalStudentApps, ...studentCollection];
      jest.spyOn(studentService, 'addStudentAppToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ course });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentAppToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudentApps.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('should call InstructorApp query and add missing value', () => {
      const course: ICourseApp = { id: 3722 };
      const instructor: IInstructorApp = { id: 14207 };
      course.instructor = instructor;

      const instructorCollection: IInstructorApp[] = [{ id: 14207 }];
      jest.spyOn(instructorService, 'query').mockReturnValue(of(new HttpResponse({ body: instructorCollection })));
      const additionalInstructorApps = [instructor];
      const expectedCollection: IInstructorApp[] = [...additionalInstructorApps, ...instructorCollection];
      jest.spyOn(instructorService, 'addInstructorAppToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ course });
      comp.ngOnInit();

      expect(instructorService.query).toHaveBeenCalled();
      expect(instructorService.addInstructorAppToCollectionIfMissing).toHaveBeenCalledWith(
        instructorCollection,
        ...additionalInstructorApps.map(expect.objectContaining),
      );
      expect(comp.instructorsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const course: ICourseApp = { id: 3722 };
      const student: IStudentApp = { id: 9978 };
      course.students = [student];
      const instructor: IInstructorApp = { id: 14207 };
      course.instructor = instructor;

      activatedRoute.data = of({ course });
      comp.ngOnInit();

      expect(comp.studentsSharedCollection).toContainEqual(student);
      expect(comp.instructorsSharedCollection).toContainEqual(instructor);
      expect(comp.course).toEqual(course);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICourseApp>>();
      const course = { id: 2858 };
      jest.spyOn(courseFormService, 'getCourseApp').mockReturnValue(course);
      jest.spyOn(courseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ course });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: course }));
      saveSubject.complete();

      // THEN
      expect(courseFormService.getCourseApp).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(courseService.update).toHaveBeenCalledWith(expect.objectContaining(course));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICourseApp>>();
      const course = { id: 2858 };
      jest.spyOn(courseFormService, 'getCourseApp').mockReturnValue({ id: null });
      jest.spyOn(courseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ course: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: course }));
      saveSubject.complete();

      // THEN
      expect(courseFormService.getCourseApp).toHaveBeenCalled();
      expect(courseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICourseApp>>();
      const course = { id: 2858 };
      jest.spyOn(courseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ course });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(courseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStudentApp', () => {
      it('should forward to studentService', () => {
        const entity = { id: 9978 };
        const entity2 = { id: 22718 };
        jest.spyOn(studentService, 'compareStudentApp');
        comp.compareStudentApp(entity, entity2);
        expect(studentService.compareStudentApp).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareInstructorApp', () => {
      it('should forward to instructorService', () => {
        const entity = { id: 14207 };
        const entity2 = { id: 32448 };
        jest.spyOn(instructorService, 'compareInstructorApp');
        comp.compareInstructorApp(entity, entity2);
        expect(instructorService.compareInstructorApp).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
