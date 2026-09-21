import { Component, inject, signal } from '@angular/core';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { JobMatch } from '../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="container page">
      <header class="head">
        <div>
          <h2>Suas vagas compatíveis</h2>
          <p class="muted">Suba seu currículo em PDF e veja o ranking de compatibilidade.</p>
        </div>
      </header>

      <div class="card upload">
        <div class="file-row">
          <label class="file-btn">
            <input type="file" accept="application/pdf" (change)="onFile($event)" hidden />
            Escolher PDF
          </label>
          <span class="filename muted">{{ fileName() ?? 'Nenhum arquivo selecionado' }}</span>
          <button class="btn btn-primary" (click)="analyze()" [disabled]="!file || loading()">
            {{ loading() ? 'Analisando...' : 'Encontrar vagas' }}
          </button>
        </div>
      </div>

      @if (error()) { <div class="alert-error">{{ error() }}</div> }

      @if (matches().length > 0) {
        <div class="results">
          @for (m of matches(); track m.url) {
            <article class="card match">
              <div class="score">
                <div class="score-n">{{ m.scorePercent }}%</div>
                <div class="score-bar"><span [style.width.%]="m.scorePercent"></span></div>
              </div>
              <div class="info">
                <h3>{{ m.title }}</h3>
                <p class="company muted">{{ m.company }} · {{ m.location || 'Remoto' }}</p>
                <div class="chips">
                  <span class="badge">{{ m.workModel }}</span>
                  <span class="badge">{{ m.seniority }}</span>
                  @for (t of m.stack.slice(0, 6); track t) { <span class="chip">{{ t }}</span> }
                </div>
              </div>
              <a class="btn btn-ghost apply" [href]="m.url" target="_blank" rel="noopener">Ver vaga</a>
            </article>
          }
        </div>
      } @else if (searched() && !loading()) {
        <div class="card empty muted">
          Nenhuma vaga encontrada. Peça ao admin para rodar a ingestão de vagas.
        </div>
      }
    </div>
  `,
  styles: [`
    .page { padding: 40px 20px 64px; }
    .head { margin-bottom: 22px; }
    .head p { margin: 0; font-size: .93rem; }
    .upload { margin-bottom: 20px; }
    .file-row { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
    .file-btn {
      display: inline-flex; align-items: center; padding: 10px 16px; border-radius: 10px;
      border: 1px dashed var(--accent); color: var(--accent); background: var(--accent-soft);
      font-weight: 600; font-size: .9rem; cursor: pointer;
    }
    .filename { flex: 1; font-size: .88rem; min-width: 160px; }
    .results { display: flex; flex-direction: column; gap: 12px; }
    .match { display: grid; grid-template-columns: 92px 1fr auto; gap: 18px; align-items: center; }
    @media (max-width: 640px) { .match { grid-template-columns: 1fr; } }
    .score-n { font-size: 1.35rem; font-weight: 700; color: var(--accent); line-height: 1; }
    .score-bar { height: 6px; background: var(--line); border-radius: 99px; margin-top: 8px; overflow: hidden; }
    .score-bar span { display: block; height: 100%; background: var(--accent); border-radius: 99px; }
    .info h3 { font-size: 1.05rem; margin: 0 0 3px; }
    .company { margin: 0 0 9px; font-size: .87rem; }
    .chips { display: flex; flex-wrap: wrap; gap: 6px; }
    .chip {
      font-size: .72rem; color: var(--muted); border: 1px solid var(--line);
      padding: 3px 8px; border-radius: 6px;
    }
    .apply { white-space: nowrap; }
    .empty { text-align: center; padding: 34px; }
  `]
})
export class DashboardComponent {
  private api = inject(ApiService);
  auth = inject(AuthService);

  file: File | null = null;
  fileName = signal<string | null>(null);
  matches = signal<JobMatch[]>([]);
  loading = signal(false);
  searched = signal(false);
  error = signal<string | null>(null);

  onFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.file = input.files?.[0] ?? null;
    this.fileName.set(this.file?.name ?? null);
    this.error.set(null);
  }

  analyze(): void {
    if (!this.file) return;
    this.loading.set(true);
    this.error.set(null);

    this.api.matchResume(this.file, 10).subscribe({
      next: (result) => {
        this.matches.set(result);
        this.loading.set(false);
        this.searched.set(true);
      },
      error: (err) => {
        this.loading.set(false);
        this.searched.set(true);
        this.error.set(err?.error?.detail ?? 'Falha ao analisar o currículo.');
      }
    });
  }
}
