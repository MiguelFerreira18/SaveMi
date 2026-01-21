import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner, MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { CreateWishDto, Wish } from '../shared/models/wish.model';
import { WishService } from './wish.service';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { AddWishDialogComponent } from './add-wish-dialog/add-wish-dialog.component';

@Component({
  selector: 'app-wish',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinner,
    ErrorDisplayComponent,
  ],
  templateUrl: './wish.component.html',
  styleUrl: './wish.component.css',
})
export class WishComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  columns: TableColumn<Wish>[] = [
    { header: 'Symbol', field: 'symbol' },
    { header: 'Description', field: 'description' },
    { header: 'Amount', field: 'amount' },
    { header: 'Date', field: 'formattedDate' },
  ];

  constructor(
    private wishService: WishService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadWishes();
    this.setupSearchFilter();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  readonly allWishes = signal<Wish[]>([]);
  wishes = signal<Wish[]>([]);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.wishes.set(this.allWishes());
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddWishDialog() {
    const dialogRef = this.dialog.open(AddWishDialogComponent, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createWish(result);
      }
    });
  }

  private loadWishes(retryCount: number = 0, maxRetries: number = 3) {
    this.wishService.getWishes().subscribe({
      next: (data) => {
        const processedData = this.formatDateFromWish(data);
        this.allWishes.set(processedData);
        this.wishes.set(processedData);
        this.isLoading.set(false);
        this.toast.show(`All Wishes were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadWishes(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        }
      },
    });
  }
  private createWish(wish: CreateWishDto) {
    this.wishService.postWishes(wish).subscribe({
      next: (data) => {
        console.log(data);

        this.toast.show(`Income ${data.description} created successfully`, 'success', 3000);
        this.loadWishes();
      },
      error: (_) => {
        this.toast.show('Error creating wish', 'error', 5000);
      },
    });
  }

  private formatDateFromWish(wishes: Wish[]): Wish[] {
    return wishes.map((wish) => ({
      ...wish,
      formattedDate: this.formatDate(wish.date),
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
      .subscribe(() => this.filterWishes());
  }

  private filterWishes(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.wishes.set(this.allWishes());
      return;
    }

    const filtered = this.allWishes().filter(
      (wishes) =>
        wishes.description.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        wishes.symbol.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        wishes.amount.toString().toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        wishes.date.toString().toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.wishes.set(filtered);
  }
}
