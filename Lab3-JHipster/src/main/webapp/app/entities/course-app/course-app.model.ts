import dayjs from 'dayjs/esm';
import { IStudentApp } from 'app/entities/student-app/student-app.model';
import { IInstructorApp } from 'app/entities/instructor-app/instructor-app.model';
import { CourseLevel } from 'app/entities/enumerations/course-level.model';

export interface ICourseApp {
  id: number;
  title?: string | null;
  description?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  level?: keyof typeof CourseLevel | null;
  price?: number | null;
  students?: Pick<IStudentApp, 'id' | 'firstName'>[] | null;
  instructor?: Pick<IInstructorApp, 'id'> | null;
}

export type NewCourseApp = Omit<ICourseApp, 'id'> & { id: null };
