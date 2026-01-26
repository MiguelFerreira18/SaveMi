import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericDropdownFilterComponent } from './generic-dropdown-filter.component';

describe('GenericDropdownFilterComponent', () => {
  let component: GenericDropdownFilterComponent;
  let fixture: ComponentFixture<GenericDropdownFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenericDropdownFilterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GenericDropdownFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
