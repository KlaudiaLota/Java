export interface IStudentProfileApp {
  id: number;
  bio?: string | null;
  linkedinUrl?: string | null;
  githubUrl?: string | null;
  profilePictureUrl?: string | null;
}

export type NewStudentProfileApp = Omit<IStudentProfileApp, 'id'> & { id: null };
