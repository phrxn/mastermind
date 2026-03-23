import { CommonModule } from '@angular/common';
import { Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiSettingsService } from '../../core/services/api-settings.service';

@Component({
  selector: 'app-api-settings-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="api-panel card-surface">
      <div class="section-heading compact">
        <div>
          <p class="eyebrow">Conexao</p>
          <h3>{{ title() }}</h3>
        </div>
        <button type="button" class="ghost-button" (click)="toggle()">
          {{ expanded() ? 'Ocultar' : 'Editar' }}
        </button>
      </div>

      @if (expanded()) {
        <label class="form-field">
          <span>Endereco base da API</span>
          <input
            type="url"
            [(ngModel)]="draftBaseUrl"
            placeholder="http://localhost:8080"
            autocomplete="off"
          />
        </label>

        <div class="inline-actions">
          <button type="button" class="secondary-button" (click)="save()">Salvar endereco</button>
        </div>
      } @else {
        <p class="api-url">{{ apiSettingsService.baseUrl() }}</p>
      }
    </section>
  `
})
export class ApiSettingsPanelComponent {
  readonly title = input('API do jogo');
  readonly apiSettingsService = inject(ApiSettingsService);
  readonly expanded = signal(false);
  draftBaseUrl = this.apiSettingsService.baseUrl();

  toggle(): void {
    this.expanded.update((value) => !value);
    this.draftBaseUrl = this.apiSettingsService.baseUrl();
  }

  save(): void {
    this.apiSettingsService.updateBaseUrl(this.draftBaseUrl);
    this.expanded.set(false);
  }
}