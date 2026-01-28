import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddInvestmentDialogComponent } from './add-investment-dialog.component';

describe('AddInvestmentDialogComponent', () => {
  let component: AddInvestmentDialogComponent;
  let fixture: ComponentFixture<AddInvestmentDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddInvestmentDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AddInvestmentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
