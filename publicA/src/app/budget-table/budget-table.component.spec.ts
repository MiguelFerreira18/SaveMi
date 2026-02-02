import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BudgetTableComponent } from './budget-table.component';

describe('BudgetTableComponent', () => {
  let component: BudgetTableComponent;
  let fixture: ComponentFixture<BudgetTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BudgetTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BudgetTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
