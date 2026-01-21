import { Component, OnInit, signal } from '@angular/core';
import { Currency } from '../../shared/models/currency.model';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CurrenciesService } from '../../currencies/currencies.service';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';

@Component({
  selector: 'app-add-objective-dialog',
  imports: [
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-objective-dialog.component.html',
  styleUrl: './add-objective-dialog.component.css',
})
export class AddObjectiveDialogComponent implements OnInit {
  currencies = signal<Currency[]>([]);

  todayOrFuture = (year: number | null): boolean => {
    if (!year) {
      return false;
    }
    const today = new Date();
    const currYear = today.getFullYear();
    return year >= currYear;
  };

  objective = new FormGroup({
    currencyId: new FormControl(-1),
    amount: new FormControl(0),
    description: new FormControl(''),
    target: new FormControl(''),
  });

  constructor(
    private dialogRef: MatDialogRef<AddObjectiveDialogComponent>,
    private currencyService: CurrenciesService
  ) {}

  ngOnInit(): void {
    this.loadCurrencies();
  }

  submit() {
    this.dialogRef.close(this.objective.value);
  }

  private loadCurrencies() {
    this.currencyService.getCurrencies().subscribe({
      next: (currencies) => {
        this.currencies.set(currencies);
      },
      error: (_) => {}, //TODO: ADD a LOADING SPINNER
    });
  }
}
