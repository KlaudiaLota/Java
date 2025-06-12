import dayjs from 'dayjs/esm';

import { IInstructorApp, NewInstructorApp } from './instructor-app.model';

export const sampleWithRequiredData: IInstructorApp = {
  id: 8088,
};

export const sampleWithPartialData: IInstructorApp = {
  id: 25950,
  firstName: 'Kyle',
  email: 'Dana54@hotmail.com',
};

export const sampleWithFullData: IInstructorApp = {
  id: 11749,
  firstName: 'Jeremy',
  lastName: 'Lind',
  email: 'Roosevelt_Schowalter@gmail.com',
  bio: 'eek how',
  hireDate: dayjs('2025-06-02T19:09'),
  rating: 16854.44,
};

export const sampleWithNewData: NewInstructorApp = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
