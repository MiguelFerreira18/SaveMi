import { Component, OnInit, signal } from '@angular/core';
import { Currency } from '../../shared/models/currency.model';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { CurrenciesService } from '../../currencies/currencies.service';
import { APP_CONSTANTS } from '../../app.constants';

@Component({
  selector: 'app-add-income-dialog',
  imports: [
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-income-dialog.component.html',
  styleUrl: './add-income-dialog.component.css',
  providers: [provideNativeDateAdapter()],
})
export class AddIncomeDialogComponent implements OnInit {
  currencies = signal<Currency[]>([]);

  todayOrFuture = (date: Date | null): boolean => {
    if (!date) {
      return false;
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    date.setHours(0, 0, 0, 0);
    return date >= today;
  };

  income = new FormGroup({
    currencyId: new FormControl(-1),
    amount: new FormControl(0),
    description: new FormControl(''),
    date: new FormControl(''),
  });

  constructor(
    private dialogRef: MatDialogRef<AddIncomeDialogComponent>,
    private currencyService: CurrenciesService
  ) {}

  ngOnInit(): void {
    this.loadCurrencies();
  }

  submit() {
    const incomeValue = { ...this.income.value };

    if (incomeValue.date) {
      const date = new Date(incomeValue.date);

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');

      incomeValue.date = `${year}-${month}-${day}`;
    }

    this.dialogRef.close(incomeValue);
  }

  private loadCurrencies() {
    this.currencyService
      .getCurrencies(APP_CONSTANTS.PAGE_SIZE_MAX, APP_CONSTANTS.PAGE_INDEX_MIN)
      .subscribe({
        next: (currencies) => {
          this.currencies.set(currencies.content);
        },
        error: (_) => {}, //TODO: ADD a LOADING SPINNER
      });
  }
}
