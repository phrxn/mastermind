import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-rules-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Regras</p>
          <h2>Como funciona o Mastermind</h2>
        </div>
      </div>

      <section class="card-surface page-stack">
        <p>
          Esta pagina foi preparada para reunir a explicacao completa do jogo. Voce pode editar este conteudo depois com as
          regras, exemplos e observacoes que quiser destacar.
        </p>

        <div class="page-stack">
          <div>
            <p class="eyebrow">Objetivo</p>
            <p>Descobrir a combinacao secreta de cores antes de acabar o numero maximo de tentativas.</p>
          </div>

          <div>
            <p class="eyebrow">Leitura das dicas</p>
            <p>As dicas mostram quantas cores estao na posicao correta e quantas existem na combinacao, mas em outra posicao.</p>
          </div>

          <div>
            <p class="eyebrow">Dificuldades</p>
            <p>Cada nivel altera quantidade de casas e permissao de repeticao de cores.</p>
          </div>
        </div>
      </section>
    </section>
  `
})
export class RulesPageComponent {}