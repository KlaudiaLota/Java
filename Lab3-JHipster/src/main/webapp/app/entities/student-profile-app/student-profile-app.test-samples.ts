import { IStudentProfileApp, NewStudentProfileApp } from './student-profile-app.model';

export const sampleWithRequiredData: IStudentProfileApp = {
  id: 23850,
};

export const sampleWithPartialData: IStudentProfileApp = {
  id: 11370,
  bio: 'frail demonstrate',
  linkedinUrl: 'yuppify hornet traditionalism',
  profilePictureUrl: 'frightfully truthfully quaintly',
};

export const sampleWithFullData: IStudentProfileApp = {
  id: 21433,
  bio: 'beyond um down',
  linkedinUrl: 'gift',
  githubUrl: 'pitiful',
  profilePictureUrl: 'designation separately blaspheme',
};

export const sampleWithNewData: NewStudentProfileApp = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
