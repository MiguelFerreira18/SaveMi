import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AddIncomeDialogComponent } from './add-income-dialog/add-income-dialog.component';
import {
  debounceTime,
  delay,
  distinctUntilChanged,
  retry,
  Subject,
  Subscription,
  takeUntil,
  timer,
} from 'rxjs';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { CreateIncomeDto, Income } from '../shared/models/income.model';
import { ToastService } from '../shared/toast.service';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { IncomeService } from './income.service';
import { emptyPage, Page } from '../shared/models/page';
import { PageEvent } from '@angular/material/paginator';
import { formatDateFromType } from '../shared/utils/date.utils';

@Component({
  standalone: true,
  selector: 'app-income',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinner,
    ErrorDisplayComponent,
  ],
  templateUrl: './income.component.html',
  styleUrl: './income.component.css',
})
export class IncomeComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private request?: Subscription;
  columns: TableColumn<Income>[] = [
    { header: 'Symbol', field: 'symbol' },
    { header: 'Description', field: 'description' },
    { header: 'Amount', field: 'amount' },
    { header: 'Date', field: 'formattedDate' },
  ];

  constructor(
    private incomeService: IncomeService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadIncomes();
    this.setupSearchFilter();
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  readonly pageIncomes = signal<Page<Income>>(emptyPage());
  incomes = signal<Income[]>([]);
  currentPageIndex = signal<number>(0);
  selectedPageSize = signal<number>(5);
  selectedIds = signal<Set<number>>(new Set<number>());
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.incomes.set(this.pageIncomes().content);
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddIncomeDialog() {
    const dialogRef = this.dialog.open(AddIncomeDialogComponent, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createIncome(result);
      }
    });
  }

  deleteIncomes() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.incomeService.deleteIncome(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadIncomes();
      },
      error: (_) => {
        this.toast.show('Error deleting incomes', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.currentPageIndex.set(event.pageIndex);
    this.selectedPageSize.set(event.pageSize);
    this.loadIncomes();
  }

  private loadIncomes(maxRetries: number = 3) {
    this.request?.unsubscribe();
    this.isLoading.set(true);

    this.incomeService
      .getIncome(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          const processedData = formatDateFromType(data);
          this.pageIncomes.set(processedData);
          this.incomes.set(processedData.content);
          this.isLoading.set(false);
          this.hasErrorLoading.set(false);
          this.toast.show(`All Incomes were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        },
      });
  }

  private createIncome(income: CreateIncomeDto) {
    this.incomeService.postIncome(income).subscribe({
      next: (data) => {
        this.toast.show(`Income ${data.description} created successfully`, 'success', 3000);
        this.loadIncomes();
      },
      error: (_) => {
        this.toast.show('Error creating Income', 'error', 5000);
      },
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadIncomes();
      });
  }
}
