import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import {
  count,
  debounceTime,
  delay,
  distinctUntilChanged,
  retry,
  Subject,
  Subscription,
  takeUntil,
  timer,
} from 'rxjs';
import { CreateInvestmentDto, Investment } from '../shared/models/investment.model';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { AddInvestmentDialogComponent } from './add-investment-dialog/add-investment-dialog.component';
import { InvestmentService } from './investment.service';
import { emptyPage, Page } from '../shared/models/page';
import { formatDateFromType } from '../shared/utils/date.utils';
import { PageEvent } from '@angular/material/paginator';

@Component({
  standalone: true,
  selector: 'app-investment',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinner,
    ErrorDisplayComponent,
  ],
  templateUrl: './investment.component.html',
  styleUrl: './investment.component.css',
})
export class InvestmentComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private request?: Subscription;
  columns: TableColumn<Investment>[] = [
    { header: 'Strategy Type', field: 'strategyType' },
    { header: 'Symbol', field: 'symbol' },
    { header: 'Description', field: 'description' },
    { header: 'Amount', field: 'amount' },
    { header: 'Date', field: 'formattedDate' },
  ];

  constructor(
    private investmentService: InvestmentService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}
  ngOnInit(): void {
    this.loadInvestment();
    this.setupSearchFilter();
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  readonly pageInvestment = signal<Page<Investment>>(emptyPage());
  investments = signal<Investment[]>([]);
  selectedIds = signal<Set<number>>(new Set<number>());
  selectedPageSize = signal<number>(5);
  currentPageIndex = signal<number>(1);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.investments.set(this.pageInvestment().content);
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddInvestmentDialog() {
    const dialogRef = this.dialog.open(AddInvestmentDialogComponent, {
      width: '450px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createInvestment(result);
      }
    });
  }

  deleteInvestments() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.investmentService.deleteInvestments(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadInvestment();
      },
      error: (_) => {
        this.toast.show('Error deleting investments', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.selectedPageSize.set(event.pageSize);
    this.currentPageIndex.set(event.pageIndex);
    this.loadInvestment();
  }

  private loadInvestment(maxRetries: number = 3) {
    this.request?.unsubscribe;
    this.isLoading.set(true);

    this.request = this.investmentService
      .getInvestments(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          const processedData = formatDateFromType(data);
          this.pageInvestment.set(processedData);
          this.investments.set(processedData.content);
          this.isLoading.set(false);
          this.hasErrorLoading.set(false);
          this.toast.show(`All Investments were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        },
      });
  }

  private createInvestment(investment: CreateInvestmentDto) {
    this.investmentService.postInvestments(investment).subscribe({
      next: (data) => {
        this.toast.show(`Investment ${data.description} created successfully`, 'success', 3000);
        this.loadInvestment();
      },
      error: (_) => {
        this.toast.show('Error creating investment', 'error', 5000);
      },
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadInvestment();
      });
  }
}
