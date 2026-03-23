import { TestBed } from '@angular/core/testing';
import { ApiSettingsService } from '../../core/services/api-settings.service';
import { ApiSettingsPanelComponent } from './api-settings-panel.component';

describe('ApiSettingsPanelComponent', () => {
  let apiSettingsService: ApiSettingsService;

  beforeEach(async () => {
    localStorage.clear();

    await TestBed.configureTestingModule({
      imports: [ApiSettingsPanelComponent]
    }).compileComponents();

    apiSettingsService = TestBed.inject(ApiSettingsService);
  });

  it('should toggle edit mode and save the API base URL', () => {
    const fixture = TestBed.createComponent(ApiSettingsPanelComponent);
    fixture.componentRef.setInput('title', 'Endereco');
    fixture.detectChanges();

    const toggleButton = fixture.nativeElement.querySelector('.ghost-button') as HTMLButtonElement;
    toggleButton.click();
    fixture.detectChanges();

    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    input.value = 'http://localhost:9090/';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const saveButton = fixture.nativeElement.querySelector('.secondary-button') as HTMLButtonElement;
    saveButton.click();
    fixture.detectChanges();

    expect(apiSettingsService.baseUrl()).toBe('http://localhost:9090');
    expect(fixture.nativeElement.textContent).toContain('http://localhost:9090');
  });
});