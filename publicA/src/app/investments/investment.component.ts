import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { CreateInvestmentDto, Investment } from '../shared/models/investment.model';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { AddInvestmentDialogComponent } from './add-investment-dialog/add-investment-dialog.component';
import { InvestmentService } from './investment.service';

@Component({
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
  readonly allInvestments = signal<Investment[]>([]);
  investments = signal<Investment[]>([]);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.investments.set(this.allInvestments());
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
  private loadInvestment(retryCount: number = 0, maxRetries: number = 3) {
    this.investmentService.getInvestments().subscribe({
      next: (data) => {
        const processedData = this.formatDateFromInvestment(data);
        this.allInvestments.set(processedData);
        this.investments.set(processedData);
        this.isLoading.set(false);
        this.toast.show(`All Investments were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadInvestment(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        }
      },
    });
  }
  private formatDateFromInvestment(investments: Investment[]): Investment[] {
    return investments.map((investment) => ({
      ...investment,
      formattedDate: this.formatDate(investment.date),
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
      .subscribe(() => this.filterInvestments());
  }

  private filterInvestments(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.investments.set(this.allInvestments());
      return;
    }

    const filtered = this.allInvestments().filter(
      (investment) =>
        investment.description.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        investment.symbol.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        investment.strategyType.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        investment.amount.toString().toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        investment.date.toString().toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.investments.set(filtered);
  }
}
