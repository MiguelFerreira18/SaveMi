import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardDynamicTableComponent } from './dashboard-dynamic-table.component';

describe('DashboardDynamicTableComponent', () => {
  let component: DashboardDynamicTableComponent;
  let fixture: ComponentFixture<DashboardDynamicTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardDynamicTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardDynamicTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
