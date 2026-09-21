import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="hero">
      <div class="container">
        <span class="badge">Matching de vagas por IA</span>
        <h1>Seu currículo encontra<br />a vaga certa.</h1>
        <p class="lead">
          Suba seu currículo em PDF e veja as vagas do mercado ranqueadas por
          compatibilidade real — usando embeddings e busca vetorial, não palavra-chave.
        </p>
        <div class="cta">
          @if (auth.isLoggedIn()) {
            <a routerLink="/painel" class="btn btn-primary">Ir para o painel</a>
          } @else {
            <a routerLink="/login" class="btn btn-primary">Começar grátis</a>
          }
          <a href="#como" class="btn btn-ghost">Como funciona</a>
        </div>
      </div>
    </section>

    <section id="como" class="container section">
      <h2>Como funciona</h2>
      <div class="grid grid-3 steps">
        <div class="card">
          <div class="step-n">01</div>
          <h3>Suba seu currículo</h3>
          <p class="muted">Envie o PDF. Extraímos o texto e montamos o seu perfil automaticamente.</p>
        </div>
        <div class="card">
          <div class="step-n">02</div>
          <h3>Geramos seu vetor</h3>
          <p class="muted">Seu perfil vira um embedding que representa suas habilidades e experiência.</p>
        </div>
        <div class="card">
          <div class="step-n">03</div>
          <h3>Veja as vagas ranqueadas</h3>
          <p class="muted">Comparamos com as vagas do mercado por similaridade e mostramos o score.</p>
        </div>
      </div>
    </section>

    <section class="container section">
      <div class="card tech">
        <h3>Construído com</h3>
        <div class="tags">
          <span>Java 21</span><span>Spring Boot</span><span>PostgreSQL + pgvector</span>
          <span>Angular 19</span><span>Docker</span><span>Clean Architecture + DDD</span>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .hero { padding: clamp(56px, 10vw, 110px) 0 clamp(40px, 7vw, 72px); }
    .hero h1 { margin-top: 18px; }
    .lead { font-size: 1.15rem; color: var(--muted); max-width: 56ch; margin: 0 0 30px; }
    .cta { display: flex; gap: 12px; flex-wrap: wrap; }
    .section { padding-bottom: 56px; }
    .steps .card { display: flex; flex-direction: column; }
    .step-n { font-size: .78rem; font-weight: 700; color: var(--accent); margin-bottom: 10px; }
    .steps h3 { font-size: 1.1rem; }
    .steps p { margin: 0; font-size: .93rem; }
    .tech h3 { font-size: 1rem; margin-bottom: 14px; }
    .tags { display: flex; flex-wrap: wrap; gap: 8px; }
    .tags span {
      font-size: .8rem; color: var(--muted); border: 1px solid var(--line);
      padding: 6px 11px; border-radius: 8px; background: #fbfcfe;
    }
  `]
})
export class LandingComponent {
  auth = inject(AuthService);
}
