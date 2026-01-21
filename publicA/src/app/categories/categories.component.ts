import { Component, computed, OnDestroy, OnInit, signal } from '@angular/core';
import { Category, CreateCategoryDto } from '../shared/models/category.model';
import { DataTableComponent, TableColumn } from '../shared/data-table/data-table.component';
import { MatIcon } from '@angular/material/icon';
import { CategoriesService } from './categories.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { AddCategoryDialogComponent } from './add-category-dialog/add-category-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { ToastService } from '../shared/toast.service';

@Component({
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

  columns: TableColumn<Category>[] = [
    { header: 'Name', field: 'name' },
    { header: 'description', field: 'description' },
  ];

  readonly allCategories = signal<Category[]>([]);
  categories = signal<Category[]>([]);
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
    this.categories.set(this.allCategories());
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

  private loadCategories(retryCount: number = 0, maxRetries: number = 3) {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.allCategories.set(data);
        this.categories.set(data);
        this.isLoading.set(false);
        this.toast.show(`All Categories were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadCategories(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error ocurred ', 'error', 5000);
        }
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
      .subscribe(() => this.filterCategories());
  }

  private filterCategories(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.categories.set(this.allCategories());
      return;
    }

    const filtered = this.allCategories().filter(
      (category) =>
        category.name.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        category.description?.toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.categories.set(filtered);
  }
}
