import { Component, computed, OnDestroy, OnInit, signal } from '@angular/core';
import { Category, CreateCategoryDto } from '../shared/models/category.model';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { ToastService } from '../shared/toast.service';
import { CreateStrategyTypeDto, StrategyType } from '../shared/models/strategy-type.model';
import { AddStrategyTypeDialogComponent } from './add-strategy-type-dialog/add-strategy-type-dialog.component';
import { StrategyTypeService } from './strategy-type.service';

@Component({
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

  columns: TableColumn<StrategyType>[] = [
    { header: 'Name', field: 'name' },
    { header: 'description', field: 'description' },
  ];

  readonly allStrategyTypes = signal<StrategyType[]>([]);
  strategyTypes = signal<StrategyType[]>([]);
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
    this.strategyTypes.set(this.allStrategyTypes());
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

  private loadStrategyTypes(retryCount: number = 0, maxRetries: number = 3) {
    this.strategyService.getStrategyTypes().subscribe({
      next: (data) => {
        this.allStrategyTypes.set(data);
        this.strategyTypes.set(data);
        this.isLoading.set(false);
        this.toast.show(`All strategies were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadStrategyTypes(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error ocurred ', 'error', 5000);
        }
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
      .subscribe(() => this.filterStrategies());
  }

  private filterStrategies(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.strategyTypes.set(this.allStrategyTypes());
      return;
    }

    const filtered = this.allStrategyTypes().filter(
      (category) =>
        category.name.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        category.description?.toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.strategyTypes.set(filtered);
  }
}
