import dayjs from 'dayjs/esm';
import { IStudentProfileApp } from 'app/entities/student-profile-app/student-profile-app.model';
import { ICourseApp } from 'app/entities/course-app/course-app.model';
import { Gender } from 'app/entities/enumerations/gender.model';

export interface IStudentApp {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  birthDate?: dayjs.Dayjs | null;
  gender?: keyof typeof Gender | null;
  email?: string | null;
  registrationDate?: dayjs.Dayjs | null;
  profile?: Pick<IStudentProfileApp, 'id'> | null;
  courses?: Pick<ICourseApp, 'id'>[] | null;
}

export type NewStudentApp = Omit<IStudentApp, 'id'> & { id: null };
