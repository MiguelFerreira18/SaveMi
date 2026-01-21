import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Currency } from '../shared/models/currency.model';
import { Category } from '../shared/models/category.model';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AddIncomeDialogComponent } from './add-income-dialog/add-income-dialog.component';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { CreateIncomeDto, Income } from '../shared/models/income.model';
import { ToastService } from '../shared/toast.service';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { IncomeService } from './income.service';

@Component({
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

  readonly allIncomes = signal<Income[]>([]);
  incomes = signal<Income[]>([]);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.incomes.set(this.allIncomes());
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
  private loadIncomes(retryCount: number = 0, maxRetries: number = 3) {
    this.incomeService.getIncome().subscribe({
      next: (data) => {
        const processedData = this.formatDateFromIncome(data);
        this.allIncomes.set(processedData);
        this.incomes.set(processedData);
        this.isLoading.set(false);
        this.toast.show(`All Incomes were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadIncomes(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        }
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

  private formatDateFromIncome(incomes: Income[]): Income[] {
    return incomes.map((income) => ({
      ...income,
      formattedDate: this.formatDate(income.date),
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
  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => this.filterIncomes());
  }

  private filterIncomes(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.incomes.set(this.allIncomes());
      return;
    }

    const filtered = this.allIncomes().filter(
      (incomes) =>
        incomes.description.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        incomes.symbol.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        incomes.amount.toString().toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        incomes.date.toString().toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.incomes.set(filtered);
  }
}
