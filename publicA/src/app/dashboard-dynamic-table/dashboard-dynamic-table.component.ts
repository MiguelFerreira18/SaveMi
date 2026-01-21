import { CommonModule } from '@angular/common';
import { Component, Input, Signal, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';

export interface TableColumn {
  key: string;
  header: string;
  align?: 'left' | 'right' | 'center';
}

@Component({
  selector: 'app-dashboard-dynamic-table',
  imports: [CommonModule, MatTableModule, MatPaginatorModule],
  templateUrl: './dashboard-dynamic-table.component.html',
  styleUrl: './dashboard-dynamic-table.component.css',
})
export class DashboardDynamicTableComponent {
  @ViewChild('paginator') paginator!: MatPaginator;

  @Input({ required: true }) data: any[] = [];
  @Input() total!: Signal<number>;
  @Input() columns: TableColumn[] = [];
  @Input() minRows: number = 10;
  @Input() pageSize: number = 10;
  @Input({ required: true }) title!: string;

  displayedData: any[] = [];
  displayedColumns: string[] = [];

  ngOnInit() {
    this.displayedColumns = this.columns.map((col) => col.key);
    this.updateDisplayedData();
  }

  ngOnChanges() {
    this.updateDisplayedData();
  }

  onPageChange(event: any) {
    this.updateDisplayedData();
  }
  updateDisplayedData() {
    const pageIndex = this.paginator ? this.paginator.pageIndex : 0;
    const startIndex = pageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    const pageData = this.data.slice(startIndex, endIndex);

    // Fill with empty rows to meet minRows requirement
    const emptyRowsNeeded = Math.max(0, this.minRows - pageData.length);
    const emptyRows = Array(emptyRowsNeeded)
      .fill({})
      .map(() => {
        const emptyRow: any = {};
        this.columns.forEach((col) => (emptyRow[col.key] = ''));
        return emptyRow;
      });

    this.displayedData = [...pageData, ...emptyRows];
  }
}
