import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { API_BASE, AuthResponse } from './models';

const STORAGE_KEY = 'vagamatch.auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  readonly user = signal<AuthResponse | null>(this.readStored());
  readonly isLoggedIn = computed(() => this.user() !== null);
  readonly isAdmin = computed(() => this.user()?.role === 'ADMIN');

  login(email: string, password: string) {
    return this.http
      .post<AuthResponse>(`${API_BASE}/api/auth/login`, { email, password })
      .pipe(tap((res) => this.store(res)));
  }

  register(email: string, password: string) {
    return this.http
      .post<AuthResponse>(`${API_BASE}/api/auth/register`, { email, password })
      .pipe(tap((res) => this.store(res)));
  }

  logout(): void {
    try { localStorage.removeItem(STORAGE_KEY); } catch { /* ignore */ }
    this.user.set(null);
    this.router.navigateByUrl('/');
  }

  token(): string | null {
    return this.user()?.token ?? null;
  }

  private store(res: AuthResponse): void {
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify(res)); } catch { /* ignore */ }
    this.user.set(res);
  }

  private readStored(): AuthResponse | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? (JSON.parse(raw) as AuthResponse) : null;
    } catch {
      return null;
    }
  }
}
