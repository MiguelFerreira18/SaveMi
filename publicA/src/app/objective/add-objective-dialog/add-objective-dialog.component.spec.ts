import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddObjectiveDialogComponent } from './add-objective-dialog.component';

describe('AddObjectiveDialogComponent', () => {
  let component: AddObjectiveDialogComponent;
  let fixture: ComponentFixture<AddObjectiveDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddObjectiveDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddObjectiveDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
