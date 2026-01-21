import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
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

@Component({
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
  readonly allObjectives = signal<Objective[]>([]);
  objectives = signal<Objective[]>([]);
  isLoading = signal<boolean>(true);
  hasErrorLoading = signal<boolean>(false);

  searchControl = new FormControl('');

  clearFilter(): void {
    this.objectives.set(this.allObjectives());
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
  private loadObjectives(retryCount: number = 0, maxRetries: number = 3) {
    this.objectiveService.getObjective().subscribe({
      next: (data) => {
        this.allObjectives.set(data);
        this.objectives.set(data);
        this.isLoading.set(false);
        this.toast.show(`All Incomes were loaded successfully`, 'success', 3000);
      },
      error: (_) => {
        this.hasErrorLoading.set(true);
        if (retryCount < maxRetries) {
          const delay = 5000 * Math.pow(2, retryCount);
          setTimeout(() => {
            this.loadObjectives(retryCount + 1, maxRetries);
          }, delay);
        } else {
          this.isLoading.set(false);
          this.toast.show('An Error occurred ', 'error', 5000);
        }
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
      .subscribe(() => this.filterObjectives());
  }

  private filterObjectives(): void {
    const searchTerm = this.searchControl.value;
    if (
      searchTerm === null ||
      searchTerm === undefined ||
      searchTerm === '' ||
      searchTerm.trim() === ''
    ) {
      this.objectives.set(this.allObjectives());
      return;
    }

    const filtered = this.allObjectives().filter(
      (objective) =>
        objective.description.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        objective.symbol.toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        objective.amount.toString().toLowerCase().includes(searchTerm.trim().toLowerCase()) ||
        objective.target.toString().toLowerCase().includes(searchTerm.trim().toLowerCase())
    );
    this.objectives.set(filtered);
  }
}
