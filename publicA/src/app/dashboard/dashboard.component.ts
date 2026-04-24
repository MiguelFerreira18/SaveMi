import { Component, computed, effect, OnDestroy, OnInit, signal } from '@angular/core';
import { DashboardService } from './dashboard.service';
import { Subject } from 'rxjs';
import { Income } from '../shared/models/income.model';
import { Wish } from '../shared/models/wish.model';
import { Expense } from '../shared/models/expense.model';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import {
  DashboardDynamicTableComponent,
  TableColumn,
} from '../dashboard-dynamic-table/dashboard-dynamic-table.component';
import { GenericDropdownFilterComponent } from '../shared/generic-dropdown-filter/generic-dropdown-filter.component';
import { createEmptyCurrency, Currency } from '../shared/models/currency.model';
import { PieChartComponent, PieChartData } from '../shared/pie-chart/pie-chart.component';
import {
  MonthlyLineGraphComponent,
  LineChartData,
} from '../shared/monthly-line-graph/monthly-line-graph.component';
import { BudgetTableComponent } from '../budget-table/budget-table.component';
import { Investment } from '../shared/models/investment.model';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  imports: [
    MatButtonModule,
    MatTabsModule,
    DashboardDynamicTableComponent,
    GenericDropdownFilterComponent,
    PieChartComponent,
    MonthlyLineGraphComponent,
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
    this.dasboardService.loadAllData().subscribe((data) => {
      this.allReducedExpenses.set(data.expenses);
      this.allRawExpenses.set(data.rawExpenses);
      this.allReducedInvestments.set(data.investments);
      this.allRawInvestments.set(data.rawInvestments);
      this.allIncomes.set(data.incomes);
      this.allWishes.set(data.wishes);
      this.symbolsFilter.set(data.currencies);

      if (data.currencies.length !== 0) {
        this.selectedSymbol.set(data.currencies[0]);
        this.applyFilters(data.currencies[0].symbol);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  selectedSymbol = signal<Currency>(createEmptyCurrency());
  symbolsFilter = signal<Currency[]>([]);

  private readonly allIncomes = signal<Income[]>([]);
  private readonly allWishes = signal<Wish[]>([]);
  private readonly allRawExpenses = signal<Expense[]>([]);
  private readonly allRawInvestments = signal<Investment[]>([]);
  private readonly allReducedExpenses = signal<Expense[]>([]);
  private readonly allReducedInvestments = signal<Investment[]>([]);

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

  pieChartData = computed<PieChartData>(() => {
    const labels = ['Expense', 'Income', 'Wish', 'Investment'];
    if (
      !this.totalExpenses() &&
      !this.totalIncome() &&
      !this.totalWishes() &&
      !this.totalInvestments()
    ) {
      return {
        labels: labels,
        data: [],
      };
    }
    return {
      labels: labels,
      data: [this.totalExpenses(), this.totalIncome(), this.totalWishes(), this.totalInvestments()],
    };
  });

  monthlyLineChart = computed<LineChartData>(() => {
    return this.dasboardService.monthlyLineChartData(
      this.allRawExpenses(),
      this.incomes(),
      this.wishes(),
      this.allRawInvestments(),
      this.selectedSymbol().symbol
    );
  });

  private applyFilters(symbol: string) {
    this.expenses.set(this.allReducedExpenses().filter((e) => e.symbol == symbol));
    this.investments.set(this.allReducedInvestments().filter((inv) => inv.symbol == symbol));
    this.incomes.set(this.allIncomes().filter((i) => i.symbol == symbol));
    this.wishes.set(this.allWishes().filter((w) => w.symbol == symbol));
  }
}
