import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StrategyTypeComponent } from './strategy-type.component';

describe('StrategyTypeComponent', () => {
  let component: StrategyTypeComponent;
  let fixture: ComponentFixture<StrategyTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StrategyTypeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(StrategyTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
