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
        <div class="page-stack">
          <div>
            <p>
              Para iniciar o jogo, você pode escolher um nível de dificuldade.
              São 4 níveis disponíveis:
              <strong>Fácil, Normal, Difícil e Mastermind</strong>.
            </p>

            <p>Ao começar, você verá o tabuleiro do jogo.</p>
			<br /><br />
			<img src="images/board_secret_example.png" alt="Exemplo do tabuleiro do jogo" />

            <p>
              No topo do tabuleiro está o segredo a ser descoberto. E não
              adianta tentar espiar — ele está bem protegido nos cofres do
              servidor. :)
            </p>
          </div>

          <div>
            <p>
              Logo abaixo, há 10 linhas com 4 ou 6 espaços para cores. À direita
              de cada linha, você verá pequenos quadrados que representam as
              dicas.
            </p>

            <p>
              Para tentar adivinhar o segredo, você deve selecionar 4 ou 6 cores
              (dependendo do nível). As cores podem ou não se repetir, conforme
              a dificuldade escolhida.
            </p>

            <p>
              Se quiser desfazer uma escolha, basta clicar na cor selecionada na
              linha. Depois, clique no botão <strong>Tentar</strong>.
            </p>
          </div>

          <div>
            <p class="eyebrow">Dicas</p>
            <ul>
              <li>
                <strong>Quadrado preto:</strong> indica que você acertou uma cor
                na posição correta.
              </li>
              <li>
                <strong>Quadrado rosa:</strong> indica que você acertou uma cor,
                mas na posição errada.
              </li>
            </ul>

            <p>
              <strong>Atenção:</strong> os quadrados de dica não mostram a
              posição exata dos acertos. Eles apenas indicam quantos acertos
              você teve. Ou seja, se aparecer um quadrado preto, isso não
              significa que a primeira cor está correta — apenas que alguma cor
              está na posição certa.
            </p>
          </div>

          <div>
            <p>
              Sua missão é usar lógica e dedução, com base nas tentativas e nas
              dicas, para descobrir o segredo. Mas seja rápido: você tem apenas
              <strong>10 tentativas</strong>!
            </p>
          </div>

          <div>
            <p class="eyebrow">Regras por nível</p>
            <ul>
              <li><strong>Fácil:</strong> 4 cores, sem repetição</li>
              <li><strong>Normal:</strong> 4 cores, com repetição</li>
              <li><strong>Difícil:</strong> 6 cores, sem repetição</li>
              <li><strong>Mastermind:</strong> 6 cores, com repetição</li>
            </ul>
          </div>
        </div>
      </section>
    </section>
  `,
})
export class RulesPageComponent {}
