import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCurrencyDialogComponent } from './add-currency-dialog.component';

describe('AddCurrencyDialogComponent', () => {
  let component: AddCurrencyDialogComponent;
  let fixture: ComponentFixture<AddCurrencyDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddCurrencyDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AddCurrencyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
