import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatCheckboxChange, MatCheckboxModule } from '@angular/material/checkbox';
import { Page } from '../models/page';
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
export class DataTableComponent<T extends { id: number }> implements OnChanges {
  @Input() columns!: TableColumn<T>[];
  @Input() page!: Page<T> | null;
  @Input() pageIndex = 0;
  @Input() pageSize = 5;
  @Input() pageSizeOption: number[] = [2, 5, 10, 20, 50];
  @Input() selectedIds!: Set<number>;
  @Input() updateSelectedIds!: (fn: (s: Set<number>) => Set<number>) => void;
  @Output() onPageChange = new EventEmitter<PageEvent>();

  datasource = new MatTableDataSource<T>();

  ngOnChanges(changes: SimpleChanges) {
    if (changes['page'] && this.page) {
      this.datasource.data = this.page.content;
    }
  }

  handlePage(event: PageEvent): void {
    this.onPageChange.emit(event);
  }

  toggleAll(event: MatCheckboxChange): void {
    if (event.checked) {
      this.updateSelectedIds((prev) => {
        const next = new Set(prev);
        this.datasource.filteredData.map((element) => {
          next.add(element.id);
        });
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
}
