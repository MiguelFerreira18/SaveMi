import { Component, OnDestroy, OnInit, signal } from '@angular/core';
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
import { CreateExpenseDto, Expense } from '../shared/models/expense.model';
import { ExpensesService } from './expenses.service';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { AddExpenseDialogComponent } from './add-expense-dialog/add-expense-dialog.component';
import { emptyPage, Page } from '../shared/models/page';
import { formatDateFromType } from '../shared/utils/date.utils';
import { PageEvent } from '@angular/material/paginator';

@Component({
  standalone: true,
  selector: 'app-expenses',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinner,
    ErrorDisplayComponent,
  ],
  templateUrl: './expenses.component.html',
  styleUrl: './expenses.component.css',
})
export class ExpensesComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private request?: Subscription;
  columns: TableColumn<Expense>[] = [
    { header: 'Category', field: 'category' },
    { header: 'Symbol', field: 'symbol' },
    { header: 'Description', field: 'description' },
    { header: 'Amount', field: 'amount' },
    { header: 'Date', field: 'formattedDate' },
  ];

  constructor(
    private expenseService: ExpensesService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}
  ngOnInit(): void {
    this.loadExpenses();
    this.setupSearchFilter();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  readonly pageExpenses = signal<Page<Expense>>(emptyPage());
  expenses = signal<Expense[]>([]);
  currentPageIndex = signal<number>(0);
  selectedPageSize = signal<number>(5);
  selectedIds = signal<Set<number>>(new Set<number>());
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.expenses.set(this.pageExpenses().content);
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddExpenseDialog() {
    const dialogRef = this.dialog.open(AddExpenseDialogComponent, {
      width: '450px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createExpense(result);
      }
    });
  }

  deleteExpenses() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.expenseService.deleteExpenses(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadExpenses();
      },
      error: (_) => {
        this.toast.show('Error deleting expenses', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.currentPageIndex.set(event.pageIndex);
    this.selectedPageSize.set(event.pageSize);
    this.loadExpenses();
  }

  private loadExpenses(retryCount: number = 0, maxRetries: number = 3) {
    this.request?.unsubscribe;
    this.isLoading.set(true);

    this.expenseService
      .getExpenses(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          const processedData = formatDateFromType(data);
          this.pageExpenses.set(processedData);
          this.expenses.set(processedData.content);
          this.isLoading.set(false);
          this.toast.show(`All Expenses were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          if (retryCount < maxRetries) {
            const delay = 5000 * Math.pow(2, retryCount);
            setTimeout(() => {
              this.loadExpenses(retryCount + 1, maxRetries);
            }, delay);
          } else {
            this.isLoading.set(false);
            this.toast.show('An Error occurred ', 'error', 5000);
          }
        },
      });
  }

  private createExpense(expense: CreateExpenseDto) {
    this.expenseService.postExpense(expense).subscribe({
      next: (data) => {
        this.toast.show(`Expense ${data.description} created successfully`, 'success', 3000);
        this.loadExpenses();
      },
      error: (_) => {
        this.toast.show('Error creating expense', 'error', 5000);
      },
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadExpenses();
      });
  }
}
