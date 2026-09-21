/** URL base da API (backend Spring Boot). */
export const API_BASE = 'http://localhost:8080';

export type Role = 'USER' | 'ADMIN';

export interface AuthResponse {
  token: string;
  email: string;
  role: Role;
}

export interface JobMatch {
  title: string;
  company: string;
  url: string;
  workModel: string;
  seniority: string;
  location: string | null;
  stack: string[];
  scorePercent: number;
}

export interface IngestionResult {
  created: number;
  skipped: number;
}

export interface AdminStats {
  totalJobs: number;
  jobsWithEmbedding: number;
  totalUsers: number;
}
