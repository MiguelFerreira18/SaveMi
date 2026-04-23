import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable, of, catchError, forkJoin } from 'rxjs';
import { Expense } from '../shared/models/expense.model';
import { Income } from '../shared/models/income.model';
import { Wish } from '../shared/models/wish.model';
import { Currency } from '../shared/models/currency.model';
import { Investment } from '../shared/models/investment.model';
import { LineChartData } from '../shared/monthly-line-graph/monthly-line-graph.component';

interface DashboardData {
  expenses: Expense[];
  rawExpenses: Expense[];
  investments: Investment[];
  rawInvestments: Investment[];
  incomes: Income[];
  wishes: Wish[];
  currencies: Currency[];
}

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly apiUrl = `${environment.apiUrl}/api`;
  private readonly expenseUri = 'expenses';
  private readonly investmentUri = 'investments';
  private readonly incomeUri = 'incomes';
  private readonly wishesUri = 'wishes';
  private readonly currenciesUri = 'currencies';
  private readonly http = inject(HttpClient);

  constructor() {}

  loadAllData(month: Date = new Date()): Observable<DashboardData> {
    const params = new HttpParams().set(
      'month',
      `${month.getFullYear()}-${(month.getMonth() + 1).toString().padStart(2, '0')}`
    );
    return forkJoin({
      rawExpenses: this.getExpenses(params).pipe(catchError(() => of([]))),
      rawInvestments: this.getInvestments(params).pipe(catchError(() => of([]))),
      incomes: this.getIncomes(params).pipe(catchError(() => of([]))),
      wishes: this.getWishes(params).pipe(catchError(() => of([]))),
      currencies: this.getCurrencies().pipe(catchError(() => of([]))),
    }).pipe(
      map((data) => {
        const expenses = this.reduceExpenses(data.rawExpenses);
        const investments = this.reduceInvestments(data.rawInvestments);
        return this.roundAllAmounts({
          ...data,
          expenses,
          investments,
        });
      })
    );
  }

  monthlyLineChartData(
    expenses: Expense[],
    incomes: Income[],
    wishes: Wish[],
    investments: Investment[],
    symbol: string,
    date: Date = new Date()
  ): LineChartData {
    const totalDaysOfMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
    const labels = Array.from({ length: totalDaysOfMonth }, (_, i) => i + 1);

    return {
      labels: labels,
      datasets: [
        {
          label: 'Expense',
          data: this.mapDataPerDay(
            this.filterByDateAndSymbol(expenses, symbol, date.getMonth(), date.getFullYear()),
            labels
          ),
          borderColor: 'rgb(255, 99, 132)',
        },
        {
          label: 'Income',
          data: this.mapDataPerDay(
            this.filterByDateAndSymbol(incomes, symbol, date.getMonth(), date.getFullYear()),
            labels
          ),
          borderColor: 'rgb(75, 192, 192)',
        },
        {
          label: 'Wish',
          data: this.mapDataPerDay(
            this.filterByDateAndSymbol(wishes, symbol, date.getMonth(), date.getFullYear()),
            labels
          ),
          borderColor: 'rgb(153, 102, 255)',
        },
        {
          label: 'Investment',
          data: this.mapDataPerDay(
            this.filterByDateAndSymbol(investments, symbol, date.getMonth(), date.getFullYear()),
            labels
          ),
          borderColor: 'rgb(255, 205, 86)',
        },
      ],
    };
  }

  private mapDataPerDay<T extends { date: Date; amount: number }>(
    a: T[],
    labels: number[]
  ): number[] {
    const dayMap = new Map<number, number>();

    a.map((item) => {
      const date = new Date(item.date);
      const day = date.getDate();
      dayMap.set(day, (dayMap.get(day) || 0) + item.amount);
    });

    return labels.map((day) => dayMap.get(day) || 0);
  }
  private filterByDateAndSymbol<T extends { date: Date; symbol: string }>(
    a: T[],
    symbol: string,
    month: number,
    year: number
  ): T[] {
    return a.filter((data) => {
      const date = new Date(data.date);
      return data.symbol === symbol && date.getMonth() === month && date.getFullYear() === year;
    });
  }

  private reduceExpenses(filteredData: Expense[]): Expense[] {
    return filteredData.reduce<Expense[]>((acc, expense) => {
      const existingExpenseLocation = acc.findIndex(
        (e) => e.category === expense.category && e.symbol === expense.symbol
      );

      if (existingExpenseLocation !== -1) {
        acc[existingExpenseLocation].amount += expense.amount;
      } else {
        acc.push({
          ...expense,
          description: '',
        });
      }

      return acc;
    }, []);
  }

  private reduceInvestments(filteredData: Investment[]): Investment[] {
    return filteredData.reduce<Investment[]>((acc, investment) => {
      const existingInvestmentLocation = acc.findIndex(
        (i) => i.strategyType === investment.strategyType && i.symbol === investment.symbol
      );

      if (existingInvestmentLocation !== -1) {
        acc[existingInvestmentLocation].amount += investment.amount;
      } else {
        acc.push({
          ...investment,
          description: '',
        });
      }

      return acc;
    }, []);
  }

  getIncomes(queryParams: HttpParams): Observable<Income[]> {
    return this.http.get<Income[]>(`${this.apiUrl}/${this.incomeUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }
  getWishes(queryParams: HttpParams): Observable<Wish[]> {
    return this.http.get<Wish[]>(`${this.apiUrl}/${this.wishesUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }

  private getCurrencies(): Observable<Currency[]> {
    return this.http.get<Currency[]>(`${this.apiUrl}/${this.currenciesUri}`, {
      withCredentials: true,
    });
  }

  private getExpenses(queryParams: HttpParams): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}/${this.expenseUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }
  private getInvestments(queryParams: HttpParams): Observable<Investment[]> {
    return this.http.get<Investment[]>(`${this.apiUrl}/${this.investmentUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }

  private roundAllAmounts(data: DashboardData): DashboardData {
    return {
      expenses: this.roundAmounts(data.expenses),
      rawExpenses: this.roundAmounts(data.rawExpenses),
      investments: this.roundAmounts(data.investments),
      rawInvestments: this.roundAmounts(data.rawInvestments),
      incomes: this.roundAmounts(data.incomes),
      wishes: this.roundAmounts(data.wishes),
      currencies: data.currencies,
    };
  }

  private roundAmounts<T extends { amount: number }>(items: T[]): T[] {
    return items.map((item) => ({
      ...item,
      amount: Number(item.amount.toFixed(2)),
    }));
  }
}
