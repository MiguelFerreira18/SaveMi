import { Component, Input } from '@angular/core';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

export interface Dataset {
  label: string;
  data: number[];
  borderColor: string;
}
export interface LineChartData {
  labels: number[];
  datasets: Dataset[];
}

@Component({
  selector: 'app-monthly-line-graph',
  imports: [BaseChartDirective],
  templateUrl: './monthly-line-graph.component.html',
  styleUrl: './monthly-line-graph.component.css',
})
export class MonthlyLineGraphComponent {
  @Input({ required: true }) lineChartData!: LineChartData;
  public lineChartType: ChartType = 'line';

  get pieChartDataConfig(): ChartData<'line'> {
    return {
      labels: this.lineChartData.labels,
      datasets: this.lineChartData.datasets,
    };
  }
  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
      tooltip: {
        enabled: true,
      },
    },
  };
}
