import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import {
  debounceTime,
  distinctUntilChanged,
  retry,
  Subject,
  Subscription,
  takeUntil,
  timer,
} from 'rxjs';
import { Objective, CreateObjectiveDto } from '../shared/models/objective.model';
import { TableColumn, DataTableComponent } from '../shared/data-table/data-table.component';
import { ObjectiveService } from './objective.service';
import { MatDialog } from '@angular/material/dialog';
import { ToastService } from '../shared/toast.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { AddObjectiveDialogComponent } from './add-objective-dialog/add-objective-dialog.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { MatIcon } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { emptyPage, Page } from '../shared/models/page';
import { PageEvent } from '@angular/material/paginator';

@Component({
  standalone: true,
  selector: 'app-objective',
  imports: [
    MatProgressSpinner,
    ErrorDisplayComponent,
    MatIcon,
    ReactiveFormsModule,
    DataTableComponent,
    MatButtonModule,
  ],
  templateUrl: './objective.component.html',
  styleUrl: './objective.component.css',
})
export class ObjectiveComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private request?: Subscription;
  columns: TableColumn<Objective>[] = [
    { header: 'Symbol', field: 'symbol' },
    { header: 'Description', field: 'description' },
    { header: 'Amount', field: 'amount' },
    { header: 'Target', field: 'target' },
  ];

  constructor(
    private objectiveService: ObjectiveService,
    private dialog: MatDialog,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadObjectives();
    this.setupSearchFilter();
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  readonly pageObjectives = signal<Page<Objective>>(emptyPage());
  objectives = signal<Objective[]>([]);
  selectedIds = signal<Set<number>>(new Set<number>());
  currentPageIndex = signal<number>(0);
  selectedPageSize = signal<number>(5);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.objectives.set(this.pageObjectives().content);
    this.searchControl.setValue('', { emitEvent: false });
  }

  openAddObjectiveDialog() {
    const dialogRef = this.dialog.open(AddObjectiveDialogComponent, {
      width: '400px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createObjective(result);
      }
    });
  }

  deleteObjectives() {
    if (this.selectedIds().size == 0) return;
    const idsToDelete = this.selectedIds();
    this.objectiveService.deleteObjectives(idsToDelete).subscribe({
      next: (_) => {
        this.selectedIds.set(new Set<number>());
        this.loadObjectives();
      },
      error: (_) => {
        this.toast.show('Error deleting objectives', 'error', 5000);
      },
    });
  }

  updateSelectedIds(updater: (s: Set<number>) => Set<number>) {
    this.selectedIds.update(updater);
  }

  onPageChange(event: PageEvent) {
    this.currentPageIndex.set(event.pageIndex);
    this.selectedPageSize.set(event.pageSize);
    this.loadObjectives();
  }

  private loadObjectives(maxRetries: number = 3) {
    this.request?.unsubscribe();
    this.isLoading.set(true);

    this.objectiveService
      .getObjective(this.selectedPageSize(), this.currentPageIndex())
      .pipe(retry({ count: maxRetries, delay: (_, n) => timer(5000 * 2 ** (n - 1)) }))
      .subscribe({
        next: (data) => {
          this.pageObjectives.set(data);
          this.objectives.set(data.content);
          this.isLoading.set(false);
          this.toast.show(`All Incomes were loaded successfully`, 'success', 3000);
        },
        error: (_) => {
          this.hasErrorLoading.set(true);
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        },
      });
  }

  private createObjective(objective: CreateObjectiveDto) {
    this.objectiveService.postObjective(objective).subscribe({
      next: (data) => {
        this.toast.show(`Objective ${data.description} created successfully`, 'success', 3000);
        this.loadObjectives();
      },
      error: (_) => {
        this.toast.show('Error creating Income', 'error', 5000);
      },
    });
  }

  private setupSearchFilter() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPageIndex.set(0);
        this.loadObjectives();
      });
  }
}
