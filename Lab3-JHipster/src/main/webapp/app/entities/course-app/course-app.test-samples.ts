import dayjs from 'dayjs/esm';

import { ICourseApp, NewCourseApp } from './course-app.model';

export const sampleWithRequiredData: ICourseApp = {
  id: 8824,
  title: 'radiant',
};

export const sampleWithPartialData: ICourseApp = {
  id: 1856,
  title: 'grandpa',
  description: 'to orientate for',
  startDate: dayjs('2025-06-03'),
};

export const sampleWithFullData: ICourseApp = {
  id: 30968,
  title: 'weakly',
  description: 'whenever pro',
  startDate: dayjs('2025-06-02'),
  endDate: dayjs('2025-06-02'),
  level: 'INTERMEDIATE',
  price: 13315.81,
};

export const sampleWithNewData: NewCourseApp = {
  title: 'unto',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
