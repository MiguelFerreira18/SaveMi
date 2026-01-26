import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddStrategyTypeDialogComponent } from './add-strategy-type-dialog.component';

describe('AddStrategyTypeDialogComponent', () => {
  let component: AddStrategyTypeDialogComponent;
  let fixture: ComponentFixture<AddStrategyTypeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddStrategyTypeDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AddStrategyTypeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
