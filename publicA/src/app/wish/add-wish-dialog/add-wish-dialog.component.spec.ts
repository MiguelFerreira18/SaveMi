import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddWishDialogComponent } from './add-wish-dialog.component';

describe('AddWishDialogComponent', () => {
  let component: AddWishDialogComponent;
  let fixture: ComponentFixture<AddWishDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddWishDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AddWishDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
