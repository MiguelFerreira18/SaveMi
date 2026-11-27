import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CurrenciesService } from './currencies.service';
import {
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  Subscription,
  takeUntil,
  timer,
} from 'rxjs';
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
import { PageEvent } from '@angular/material/paginator';
import { emptyPage, Page } from '../shared/models/page';

@Component({
  standalone: true,
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
  private request?: Subscription;

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

  readonly pageCurrencies = signal<Page<Currency>>(emptyPage());
  currencies = signal<Currency[]>([]);
  currentPageIndex = signal<number>(0);
  selectedPageSize = signal<number>(5);
  selectedIds = signal<Set<number>>(new Set<number>());
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.currencies.set(this.pageCurrencies().content);
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

  deleteCurrencies() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.currencyService.deleteCurrencies(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadCurrencies();
      },
      error: (_) => {
        this.toast.show('Error deleting currencies', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.currentPageIndex.set(event.pageIndex);
    this.selectedPageSize.set(event.pageSize);
    this.loadCurrencies();
  }

  private loadCurrencies(maxRetries: number = 3) {
    this.currencyService
      .getCurrencies(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          this.pageCurrencies.set(data);
          this.currencies.set(data.content);
          this.isLoading.set(false);
          this.hasErrorLoading.set(false);
          this.toast.show(`All Currencies were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error ocurred ', 'error', 5000);
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
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadCurrencies();
      });
  }
}
