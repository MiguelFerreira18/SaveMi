import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonthlyLineGraphComponent } from './monthly-line-graph.component';

describe('MonthlyLineGraphComponent', () => {
  let component: MonthlyLineGraphComponent;
  let fixture: ComponentFixture<MonthlyLineGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonthlyLineGraphComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonthlyLineGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
