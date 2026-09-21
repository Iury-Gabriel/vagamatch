import { Component, OnInit, inject, signal } from '@angular/core';
import { ApiService } from '../core/api.service';
import { AdminStats } from '../core/models';

@Component({
  selector: 'app-admin',
  standalone: true,
  template: `
    <div class="container page">
      <header class="head">
        <div>
          <h2>Painel administrativo</h2>
          <p class="muted">Gerencie o catálogo de vagas da plataforma.</p>
        </div>
        <button class="btn btn-primary" (click)="runIngest()" [disabled]="ingesting()">
          {{ ingesting() ? 'Ingerindo...' : 'Rodar ingestão' }}
        </button>
      </header>

      @if (error()) { <div class="alert-error">{{ error() }}</div> }
      @if (message()) { <div class="alert-ok">{{ message() }}</div> }

      <div class="grid grid-3">
        <div class="card stat">
          <div class="stat-n">{{ stats()?.totalJobs ?? '—' }}</div>
          <div class="stat-l">Vagas no catálogo</div>
        </div>
        <div class="card stat">
          <div class="stat-n">{{ stats()?.jobsWithEmbedding ?? '—' }}</div>
          <div class="stat-l">Prontas para matching</div>
        </div>
        <div class="card stat">
          <div class="stat-n">{{ stats()?.totalUsers ?? '—' }}</div>
          <div class="stat-l">Usuários cadastrados</div>
        </div>
      </div>

      <div class="card info">
        <h3>Sobre a ingestão</h3>
        <p class="muted">
          A ingestão busca vagas nas fontes públicas configuradas (Remotive), gera o
          embedding de cada uma e salva no PostgreSQL com pgvector. Vagas já existentes
          (mesma fonte + id externo) são ignoradas.
        </p>
      </div>
    </div>
  `,
  styles: [`
    .page { padding: 40px 20px 64px; }
    .head { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; flex-wrap: wrap; margin-bottom: 22px; }
    .head p { margin: 0; font-size: .93rem; }
    .stat { text-align: left; }
    .stat-n { font-size: 2rem; font-weight: 700; letter-spacing: -.02em; line-height: 1; }
    .stat-l { font-size: .82rem; color: var(--muted); margin-top: 8px; }
    .info { margin-top: 18px; }
    .info h3 { font-size: 1rem; }
    .info p { margin: 0; font-size: .9rem; }
  `]
})
export class AdminComponent implements OnInit {
  private api = inject(ApiService);

  stats = signal<AdminStats | null>(null);
  ingesting = signal(false);
  error = signal<string | null>(null);
  message = signal<string | null>(null);

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.api.stats().subscribe({
      next: (s) => this.stats.set(s),
      error: () => this.error.set('Não foi possível carregar as estatísticas.')
    });
  }

  runIngest(): void {
    this.ingesting.set(true);
    this.error.set(null);
    this.message.set(null);

    this.api.ingest().subscribe({
      next: (result) => {
        this.ingesting.set(false);
        this.message.set(`Ingestão concluída: ${result.created} novas vagas, ${result.skipped} já existentes.`);
        this.loadStats();
      },
      error: (err) => {
        this.ingesting.set(false);
        this.error.set(err?.error?.detail ?? 'Falha ao rodar a ingestão.');
      }
    });
  }
}
