import dayjs from 'dayjs/esm';

export interface IInstructorApp {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  bio?: string | null;
  hireDate?: dayjs.Dayjs | null;
  rating?: number | null;
}

export type NewInstructorApp = Omit<IInstructorApp, 'id'> & { id: null };
