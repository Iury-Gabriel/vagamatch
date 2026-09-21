import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="wrap container">
      <div class="card box">
        <h2>{{ mode() === 'login' ? 'Entrar' : 'Criar conta' }}</h2>
        <p class="muted sub">
          {{ mode() === 'login'
            ? 'Acesse seu painel e veja suas vagas compatíveis.'
            : 'Leva 10 segundos. Depois é só subir seu currículo.' }}
        </p>

        @if (error()) { <div class="alert-error">{{ error() }}</div> }

        <form (ngSubmit)="submit()">
          <div class="field">
            <label class="label" for="email">E-mail</label>
            <input id="email" class="input" type="email" name="email"
                   [(ngModel)]="email" required autocomplete="email" />
          </div>
          <div class="field">
            <label class="label" for="password">Senha</label>
            <input id="password" class="input" type="password" name="password"
                   [(ngModel)]="password" required autocomplete="current-password" />
          </div>
          <button class="btn btn-primary btn-block" type="submit" [disabled]="loading()">
            {{ loading() ? 'Aguarde...' : (mode() === 'login' ? 'Entrar' : 'Criar conta') }}
          </button>
        </form>

        <p class="toggle muted">
          {{ mode() === 'login' ? 'Ainda não tem conta?' : 'Já tem conta?' }}
          <a href="#" (click)="toggle($event)">
            {{ mode() === 'login' ? 'Criar conta' : 'Entrar' }}
          </a>
        </p>

        <div class="hint">
          <strong>Admin de demonstração:</strong> admin&#64;vagamatch.dev / admin123
        </div>
      </div>
    </div>
  `,
  styles: [`
    .wrap { display: flex; justify-content: center; padding: 56px 20px; }
    .box { width: 100%; max-width: 420px; }
    .sub { margin: 0 0 22px; font-size: .92rem; }
    .toggle { margin: 18px 0 0; font-size: .9rem; text-align: center; }
    .toggle a { color: var(--accent); font-weight: 600; }
    .hint {
      margin-top: 18px; padding: 10px 12px; border-radius: 10px;
      background: var(--accent-soft); font-size: .8rem; color: var(--muted);
    }
  `]
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  mode = signal<'login' | 'register'>('login');
  loading = signal(false);
  error = signal<string | null>(null);

  toggle(event: Event): void {
    event.preventDefault();
    this.mode.set(this.mode() === 'login' ? 'register' : 'login');
    this.error.set(null);
  }

  submit(): void {
    if (!this.email || !this.password) {
      this.error.set('Preencha e-mail e senha.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);

    const request$ = this.mode() === 'login'
      ? this.auth.login(this.email, this.password)
      : this.auth.register(this.email, this.password);

    request$.subscribe({
      next: (res) => {
        this.loading.set(false);
        this.router.navigateByUrl(res.role === 'ADMIN' ? '/admin' : '/painel');
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.detail ?? 'Não foi possível concluir. Tente novamente.');
      }
    });
  }
}
