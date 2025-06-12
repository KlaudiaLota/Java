import dayjs from 'dayjs/esm';

import { IStudentApp, NewStudentApp } from './student-app.model';

export const sampleWithRequiredData: IStudentApp = {
  id: 15380,
};

export const sampleWithPartialData: IStudentApp = {
  id: 3475,
  firstName: 'Madilyn',
  birthDate: dayjs('2025-06-02'),
  gender: 'FEMALE',
  registrationDate: dayjs('2025-06-02T18:37'),
};

export const sampleWithFullData: IStudentApp = {
  id: 6617,
  firstName: 'Mya',
  lastName: 'Jerde',
  birthDate: dayjs('2025-06-03'),
  gender: 'FEMALE',
  email: 'Price.Heaney26@yahoo.com',
  registrationDate: dayjs('2025-06-03T10:20'),
};

export const sampleWithNewData: NewStudentApp = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
