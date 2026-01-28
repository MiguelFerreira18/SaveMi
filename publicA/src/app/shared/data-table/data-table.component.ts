import {
  AfterViewInit,
  Component,
  Input,
  OnChanges,
  Signal,
  SimpleChanges,
  ViewChild,
  WritableSignal,
} from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatCheckboxChange, MatCheckboxModule } from '@angular/material/checkbox';
export interface TableColumn<T> {
  header: string;
  field: keyof T & string;
}

@Component({
  selector: 'app-data-table',
  imports: [MatTableModule, MatPaginatorModule, MatCheckboxModule],
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.css',
})
export class DataTableComponent<T extends { id: number }> implements AfterViewInit, OnChanges {
  @Input() columns!: TableColumn<T>[];
  @Input() data!: T[];
  @Input() minRows: number = 10;
  @Input() pageSizeOption?: number[];
  @Input() selectedIds!: Set<number>;
  @Input() updateSelectedIds!: (fn: (s: Set<number>) => Set<number>) => void;
  datasource = new MatTableDataSource<T>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly defaultPageSizeOption: number[] = [5, 10, 20, 50];

  ngAfterViewInit(): void {
    this.datasource.paginator = this.paginator;
    this.datasource.data = this.data;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['data']) {
      this.datasource.data = this.data;
    }
  }

  toggleAll(event: MatCheckboxChange): void {
    if (event.checked) {
      this.updateSelectedIds((prev) => {
        const next = new Set(prev);
        this.datasource.filteredData.map((element) => {
          next.add(element.id);
        });
        console.log(next);

        return next;
      });
    } else {
      this.updateSelectedIds((_) => {
        return new Set();
      });
    }
  }
  toggleSelection(id: number, event: MatCheckboxChange): void {
    if (event.checked) {
      this.updateSelectedIds((prev) => {
        const next = new Set(prev);
        next.add(id);
        return next;
      });
    } else {
      this.updateSelectedIds((prev) => {
        const next = new Set(prev);
        next.delete(id);
        return next;
      });
    }
  }

  areAllSelected(): boolean {
    const numRows = this.datasource?.filteredData?.length || 0;
    const numSelected = this.selectedIds.size;
    return numRows > 0 && numSelected == numRows;
  }

  isSelected(id: number): boolean {
    return this.selectedIds.has(id);
  }

  get displayedColumns(): string[] {
    return ['actions', ...this.columns.map((c) => c.field as string)];
  }
  get paginatorPageSizeOption(): number[] {
    return this.pageSizeOption || this.defaultPageSizeOption;
  }
}
