import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Category, CreateCategoryDto } from '../shared/models/category.model';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { CategoriesService } from './categories.service';
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
import { AddCategoryDialogComponent } from './add-category-dialog/add-category-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { ToastService } from '../shared/toast.service';
import { emptyPage, Page } from '../shared/models/page';
import { PageEvent } from '@angular/material/paginator';

@Component({
  standalone: true,
  selector: 'app-categories',
  imports: [
    DataTableComponent,
    MatIcon,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    ErrorDisplayComponent,
  ],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css',
})
export class CategoriesComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private request?: Subscription;

  columns: TableColumn<Category>[] = [
    { header: 'Name', field: 'name' },
    { header: 'description', field: 'description' },
  ];

  readonly pageCategories = signal<Page<Category>>(emptyPage());
  categories = signal<Category[]>([]);
  selectedIds = signal<Set<number>>(new Set<number>());
  selectedPageSize = signal<number>(5);
  currentPageIndex = signal<number>(0);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  constructor(
    private categoryService: CategoriesService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.loadCategories();
    this.setupSearchFilter();
  }
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  clearFilter(): void {
    this.categories.set(this.pageCategories().content);
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddCategoryDialog() {
    const dialogRef = this.dialog.open(AddCategoryDialogComponent, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createCategory(result);
      }
    });
  }

  deleteCategories() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.categoryService.deleteCategories(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadCategories();
      },
      error: (_) => {
        this.toast.show('Error deleting categories', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.selectedPageSize.set(event.pageSize);
    this.currentPageIndex.set(event.pageIndex);
    this.loadCategories();
  }

  private loadCategories(maxRetries: number = 3) {
    this.request?.unsubscribe;
    this.isLoading.set(true);

    this.request = this.categoryService
      .getCategories(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          this.pageCategories.set(data);
          this.categories.set(data.content);
          this.isLoading.set(false);
          this.hasErrorLoading.set(false);
          this.toast.show(`All Categories were loaded successfully`, 'success', 3000);
        },
        error: () => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        },
      });
  }

  private createCategory(category: CreateCategoryDto) {
    this.categoryService.postCategory(category).subscribe({
      next: (data) => {
        this.toast.show(`Category ${data.name} created successfully`, 'success');
        this.loadCategories();
      },
      error: (_) => this.toast.show('Error creating category', 'error', 5000),
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadCategories();
      });
  }
}
