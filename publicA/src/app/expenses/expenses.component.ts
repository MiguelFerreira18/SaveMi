import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { debounceTime, distinctUntilChanged, max, Subject, takeUntil } from 'rxjs';
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

@Component({
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

  readonly allExpenses = signal<Expense[]>([]);
  expenses = signal<Expense[]>([]);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.expenses.set(this.allExpenses());
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

  private loadExpenses(retryCount: number = 0, maxRetries: number = 3) {
    this.expenseService.getExpenses().subscribe({
      next: (data) => {
        const processedData = this.formatDateFromExpenses(data);
        this.allExpenses.set(processedData);
        this.expenses.set(processedData);
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
  private formatDateFromExpenses(expenses: Expense[]): Expense[] {
    return expenses.map((expense) => ({
      ...expense,
      formattedDate: this.formatDate(expense.date),
    }));
  }
  private formatDate(date: Date): string {
    if (!date) return '';

    if (Array.isArray(date)) {
      const [year, month, day] = date;
      return `${day.toString().padStart(2, '0')}-${(month + 1).toString().padStart(2, '0')}-${year}`;
    }

    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
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
      .subscribe(() => this.filterExpenses());
  }

  private filterExpenses(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.expenses.set(this.allExpenses());
      return;
    }

    const filtered = this.allExpenses().filter(
      (expenses) =>
        expenses.description.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        expenses.symbol.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        expenses.category.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        expenses.amount.toString().toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        expenses.date.toString().toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.expenses.set(filtered);
  }
}
