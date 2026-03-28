import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

export interface PieChartData {
  labels: string[];
  data: number[];
}

@Component({
  selector: 'app-pie-chart',
  imports: [BaseChartDirective],
  templateUrl: './pie-chart.component.html',
  styleUrl: './pie-chart.component.css',
})
export class PieChartComponent implements OnChanges {
  @Input({ required: true }) pieChartData!: PieChartData;

  public pieChartType: ChartType = 'pie';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['pieChartData']) {
      this.pieChartDataConfig = {
        labels: this.pieChartData.labels,
        datasets: [{ data: this.pieChartData.data }],
      };
    }
  }

  public pieChartDataConfig: ChartData<'pie'> = {
    labels: [],
    datasets: [
      {
        data: [],
      },
    ],
  };

  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
      tooltip: {
        enabled: true,
        callbacks: {
          label: function (tooltipItem) {
            const data = tooltipItem.chart.data.datasets[tooltipItem.datasetIndex].data as number[];
            const total = data.reduce((acc, value) => acc + value, 0);
            const currentValue = data[tooltipItem.dataIndex];
            const percentage = ((currentValue / total) * 100).toFixed(2);
            const label = tooltipItem.chart.data.labels?.[tooltipItem.dataIndex] || '';
            return `${label}: ${percentage}%`;
          },
        },
      },
    },
  };
}
