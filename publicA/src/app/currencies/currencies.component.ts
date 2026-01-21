import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CurrenciesService } from './currencies.service';
import { debounce, debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { CreateCurrencyDto, Currency } from '../shared/models/currency.model';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { AddCurrencyDialogComponent } from './add-currency-dialog/add-currency-dialog.component';

@Component({
  selector: 'app-currencies',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinner,
    ErrorDisplayComponent,
  ],
  templateUrl: './currencies.component.html',
  styleUrl: './currencies.component.css',
})
export class CurrenciesComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  columns: TableColumn<Currency>[] = [
    { header: 'Name', field: 'name' },
    { header: 'Symbol', field: 'symbol' },
  ];
  constructor(
    private currencyService: CurrenciesService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCurrencies();
    this.setupSearchFilter();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  readonly allCurrencies = signal<Currency[]>([]);
  currencies = signal<Currency[]>([]);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.currencies.set(this.allCurrencies());
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddCurrencyDialog() {
    const dialogRef = this.dialog.open(AddCurrencyDialogComponent, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createCurrency(result);
      }
    });
  }

  private loadCurrencies(retryCount: number = 0, maxRetries: number = 3) {
    this.currencyService.getCurrencies().subscribe({
      next: (data) => {
        this.allCurrencies.set(data);
        this.currencies.set(data);
        this.isLoading.set(false);
        this.toast.show(`All Currencies were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadCurrencies(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error ocurred ', 'error', 5000);
        }
      },
    });
  }

  private createCurrency(currency: CreateCurrencyDto) {
    this.currencyService.postCurrencies(currency).subscribe({
      next: (data) => {
        this.toast.show(`Currency ${data.symbol} created successfully`, 'success', 3000);
        this.loadCurrencies();
      },
      error: (_) => this.toast.show('Error creating currency', 'error', 5000),
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => this.filterCurrencies());
  }

  private filterCurrencies(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.currencies.set(this.allCurrencies());
      return;
    }

    const filtered = this.allCurrencies().filter(
      (currency) =>
        currency.name.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        currency.symbol.toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.currencies.set(filtered);
  }
}
