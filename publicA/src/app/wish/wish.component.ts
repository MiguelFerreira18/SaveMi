import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
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
import { CreateWishDto, Wish } from '../shared/models/wish.model';
import { WishService } from './wish.service';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { AddWishDialogComponent } from './add-wish-dialog/add-wish-dialog.component';
import { formatDateFromType } from '../shared/utils/date.utils';
import { emptyPage, Page } from '../shared/models/page';
import { PageEvent } from '@angular/material/paginator';

@Component({
  standalone: true,
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
  private request?: Subscription;
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

  readonly pageWishes = signal<Page<Wish>>(emptyPage());
  wishes = signal<Wish[]>([]);
  selectedIds = signal<Set<number>>(new Set<number>());
  selectedPageSize = signal<number>(5);
  currentPageIndex = signal<number>(0);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.wishes.set(this.pageWishes().content);
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

  deleteWishes() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.wishService.deleteWishes(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadWishes();
      },
      error: (_) => {
        this.toast.show('Error deleting wishes', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.selectedPageSize.set(event.pageSize);
    this.currentPageIndex.set(event.pageIndex);
    this.loadWishes();
  }

  private loadWishes(maxRetries: number = 3) {
    this.request?.unsubscribe;
    this.isLoading.set(true);

    this.request = this.wishService
      .getWishes(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          const processedData = formatDateFromType(data);
          this.pageWishes.set(processedData);
          this.wishes.set(processedData.content);
          this.isLoading.set(false);
          this.hasErrorLoading.set(false);
          this.toast.show(`All Wishes were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        },
      });
  }

  private createWish(wish: CreateWishDto) {
    this.wishService.postWishes(wish).subscribe({
      next: (data) => {
        this.toast.show(`Income ${data.description} created successfully`, 'success', 3000);
        this.loadWishes();
      },
      error: (_) => {
        this.toast.show('Error creating wish', 'error', 5000);
      },
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadWishes();
      });
  }
}
