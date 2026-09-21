import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_BASE, AdminStats, IngestionResult, JobMatch } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  /** Envia o currículo em PDF e recebe as vagas ranqueadas. */
  matchResume(file: File, limit = 10) {
    const form = new FormData();
    form.append('resume', file);
    return this.http.post<JobMatch[]>(`${API_BASE}/api/match?limit=${limit}`, form);
  }

  /** Dispara a ingestão de vagas (ADMIN). */
  ingest() {
    return this.http.post<IngestionResult>(`${API_BASE}/api/admin/ingest`, {});
  }

  /** Números do painel administrativo (ADMIN). */
  stats() {
    return this.http.get<AdminStats>(`${API_BASE}/api/admin/stats`);
  }
}
