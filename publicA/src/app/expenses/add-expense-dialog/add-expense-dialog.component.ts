import { Component, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CategoriesService } from '../../categories/categories.service';
import { CurrenciesService } from '../../currencies/currencies.service';
import { Currency } from '../../shared/models/currency.model';
import { Category } from '../../shared/models/category.model';

@Component({
  selector: 'app-add-expense-dialog',
  providers: [provideNativeDateAdapter()],
  imports: [
    MatDialogModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-expense-dialog.component.html',
  styleUrl: './add-expense-dialog.component.css',
})
export class AddExpenseDialogComponent implements OnInit {
  currencies = signal<Currency[]>([]);
  categories = signal<Category[]>([]);
  todayOrFuture = (date: Date | null): boolean => {
    if (!date) {
      return false;
    }
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    date.setHours(0, 0, 0, 0);
    return date >= today;
  };

  expense = new FormGroup({
    currencyId: new FormControl(-1),
    categoryId: new FormControl(-1),
    amount: new FormControl(0),
    description: new FormControl(''),
    date: new FormControl(''),
  });

  constructor(
    private dialogRef: MatDialogRef<AddExpenseDialogComponent>,
    private categoryService: CategoriesService,
    private currencyService: CurrenciesService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadCurrencies();
  }

  submit() {
    const expenseValue = { ...this.expense.value };

    if (expenseValue.date) {
      const date = new Date(expenseValue.date);

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      expenseValue.date = `${year}-${month}-${day}`;
    }

    this.dialogRef.close(expenseValue);
  }

  private loadCurrencies() {
    this.currencyService.getCurrencies().subscribe({
      next: (currencies) => {
        this.currencies.set(currencies);
      },
      error: (err) => console.log(err), //TODO: ADD A LOADING SPINNER LATER TO REQUEST THIS
    });
  }

  private loadCategories() {
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
      },
      error: (err) => console.log(err), //TODO: ADD A LOADING SPINNER LATER TO REQUEST THIS
    });
  }
}
