import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {
  count,
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  Subscription,
  takeUntil,
  timer,
} from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { ToastService } from '../shared/toast.service';
import { CreateStrategyTypeDto, StrategyType } from '../shared/models/strategy-type.model';
import { AddStrategyTypeDialogComponent } from './add-strategy-type-dialog/add-strategy-type-dialog.component';
import { StrategyTypeService } from './strategy-type.service';
import { emptyPage, Page } from '../shared/models/page';
import { PageEvent } from '@angular/material/paginator';

@Component({
  standalone: true,
  selector: 'app-strategy-type',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    ErrorDisplayComponent,
  ],
  templateUrl: './strategy-type.component.html',
  styleUrl: './strategy-type.component.css',
})
export class StrategyTypeComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private request?: Subscription;

  columns: TableColumn<StrategyType>[] = [
    { header: 'Name', field: 'name' },
    { header: 'description', field: 'description' },
  ];

  readonly pageStrategyTypes = signal<Page<StrategyType>>(emptyPage());
  strategyTypes = signal<StrategyType[]>([]);
  selectedIds = signal<Set<number>>(new Set<number>());
  selectedPageSize = signal<number>(5);
  currentPageIndex = signal<number>(0);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  constructor(
    private strategyService: StrategyTypeService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadStrategyTypes();
    this.setupSearchFilter();
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  clearFilter(): void {
    this.strategyTypes.set(this.pageStrategyTypes().content);
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddStrategyTypeDialog() {
    const dialogRef = this.dialog.open(AddStrategyTypeDialogComponent, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createStrategyType(result);
      }
    });
  }

  deleteStrategies() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.strategyService.deleteStrategies(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadStrategyTypes();
      },
      error: (_) => {
        this.toast.show('Error deleting strategy type', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.selectedPageSize.set(event.pageSize);
    this.currentPageIndex.set(event.pageIndex);
    this.loadStrategyTypes();
  }

  private loadStrategyTypes(maxRetries: number = 3) {
    this.request?.unsubscribe;
    this.isLoading.set(true);

    this.request = this.strategyService
      .getStrategyTypes(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          this.pageStrategyTypes.set(data);
          this.strategyTypes.set(data.content);
          this.isLoading.set(false);
          this.hasErrorLoading.set(false);
          this.toast.show(`All strategies were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error ocurred ', 'error', 5000);
        },
      });
  }

  private createStrategyType(strategy: CreateStrategyTypeDto) {
    this.strategyService.postStrategyType(strategy).subscribe({
      next: (data) => {
        this.toast.show(`Strategy ${data.name} created successfully`, 'success');
        this.loadStrategyTypes();
      },
      error: (_) => this.toast.show('Error creating category', 'error', 5000),
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadStrategyTypes();
      });
  }
}
