import { Component, computed, effect, OnDestroy, OnInit, signal } from '@angular/core';
import { DashboardService } from './dashboard.service';
import { Subject } from 'rxjs';
import { Income } from '../shared/models/income.model';
import { Wish } from '../shared/models/wish.model';
import { Expense } from '../shared/models/expense.model';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ErrorDisplayComponent } from '../shared/error-display/error-display.component';
import { MatIcon } from '@angular/material/icon';
import {
  DashboardDynamicTableComponent,
  TableColumn,
} from '../dashboard-dynamic-table/dashboard-dynamic-table.component';
import { GenericDropdownFilterComponent } from '../shared/generic-dropdown-filter/generic-dropdown-filter.component';
import { createEmptyCurrency, Currency } from '../shared/models/currency.model';
import { PieChartComponent, PieChartData } from '../shared/pie-chart/pie-chart.component';
import { BudgetTableComponent } from '../budget-table/budget-table.component';
import { Investment } from '../shared/models/investment.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    MatIcon,
    MatButtonModule,
    MatProgressSpinner,
    ErrorDisplayComponent,
    DashboardDynamicTableComponent,
    GenericDropdownFilterComponent,
    PieChartComponent,
    BudgetTableComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  expenseColumns: TableColumn[] = [
    { key: 'category', header: 'Category', align: 'left' },
    { key: 'amount', header: 'Amount', align: 'right' },
  ];
  investmentColumns: TableColumn[] = [
    { key: 'strategyType', header: 'Strategy Type', align: 'left' },
    { key: 'amount', header: 'Amount', align: 'right' },
  ];
  incomeColumns: TableColumn[] = [
    { key: 'description', header: 'Description', align: 'left' },
    { key: 'amount', header: 'Amount', align: 'right' },
  ];
  wishColumns: TableColumn[] = [
    { key: 'description', header: 'Description', align: 'left' },
    { key: 'amount', header: 'Amount', align: 'right' },
  ];

  constructor(private dasboardService: DashboardService) {
    effect(() => {
      const symbol = this.selectedSymbol().symbol;
      this.applyFilters(symbol);
    });
  }

  ngOnInit(): void {
    this.loadCurrencies();
    this.loadExpenses();
    this.loadIncome();
    this.loadWishes();
    this.loadInvestments();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadedCount = 0;
  private readonly TOTAL_LOADS = 5; //INFO: AT LEAST FOR NOW THERE ARE 5

  selectedSymbol = signal<Currency>(createEmptyCurrency());
  symbolsFilter = signal<Currency[]>([]);

  private readonly allIncomes = signal<Income[]>([]);
  private readonly allWishes = signal<Wish[]>([]);
  private readonly allExpenses = signal<Expense[]>([]);
  private readonly allInvestments = signal<Investment[]>([]);

  incomes = signal<Income[]>([]);
  wishes = signal<Wish[]>([]);
  expenses = signal<Expense[]>([]);
  investments = signal<Investment[]>([]);

  totalIncome = computed(() => this.incomes().reduce((acc, i) => acc + i.amount, 0));
  totalWishes = computed(() => this.wishes().reduce((acc, w) => acc + w.amount, 0));
  totalExpenses = computed(() => this.expenses().reduce((acc, e) => acc + e.amount, 0));
  totalInvestments = computed(() => this.investments().reduce((acc, e) => acc + e.amount, 0));

  displaySymbol(currency: Currency): string {
    return currency.symbol;
  }

  pieChartData(): PieChartData {
    return {
      labels: ['Expense', 'Income', 'Wish', 'Investment'],
      data: [this.totalExpenses(), this.totalIncome(), this.totalWishes(), this.totalInvestments()],
    };
  }

  private loadExpenses() {
    this.dasboardService.reducedExpenses().subscribe({
      next: (expenses) => {
        const roundedExpenses = expenses.map((e) => {
          e['amount'] = Number(e.amount.toFixed(2));
          return e;
        });
        this.allExpenses.set(roundedExpenses);
        this.expenses.set(roundedExpenses);
        this.checkAllLoads();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
  private loadInvestments() {
    this.dasboardService.reducedInvestments().subscribe({
      next: (investments) => {
        const roundedInvestments = investments.map((e) => {
          e['amount'] = Number(e.amount.toFixed(2));
          return e;
        });
        this.allInvestments.set(roundedInvestments);
        this.investments.set(roundedInvestments);
        this.checkAllLoads();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
  private loadIncome() {
    this.dasboardService.getIncomes().subscribe({
      next: (incomes) => {
        console.log(incomes);

        const roundedIncomes = incomes.map((i) => {
          i['amount'] = Number(i.amount.toFixed(2));
          return i;
        });

        this.allIncomes.set(roundedIncomes);
        this.incomes.set(roundedIncomes);
        this.checkAllLoads();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  private loadWishes() {
    this.dasboardService.getWishes().subscribe({
      next: (wishes) => {
        const roundedWishes = wishes.map((w) => {
          w['amount'] = Number(w.amount.toFixed(2));
          return w;
        });
        this.allWishes.set(roundedWishes);
        this.wishes.set(roundedWishes);
        this.checkAllLoads();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  private loadCurrencies() {
    this.dasboardService.getCurrencies().subscribe({
      next: (currencies) => {
        this.symbolsFilter.set(currencies);
        if (currencies.length !== 0) {
          this.checkAllLoads();
          this.selectedSymbol.set(currencies[0]);
        }
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  private checkAllLoads() {
    this.loadedCount++;
    if (this.loadedCount >= this.TOTAL_LOADS) {
      this.applyFilters(this.selectedSymbol().symbol);
    }
  }

  private applyFilters(symbol: string) {
    this.expenses.set(this.allExpenses().filter((e) => e.symbol == symbol));
    this.investments.set(this.allInvestments().filter((inv) => inv.symbol == symbol));
    this.incomes.set(this.allIncomes().filter((i) => i.symbol == symbol));
    this.wishes.set(this.allWishes().filter((w) => w.symbol == symbol));
  }
}
