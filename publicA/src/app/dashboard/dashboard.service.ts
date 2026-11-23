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
import { emptyPage, Page } from '../shared/models/page';

interface DashboardData {
  expenses: Expense[];
  rawExpenses: Page<Expense>;
  investments: Investment[];
  rawInvestments: Page<Investment>;
  incomes: Page<Income>;
  wishes: Page<Wish>;
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

  loadAllData(symbol: Currency, month: Date = new Date()): Observable<DashboardData> {
    let params = new HttpParams()
      .set(
        'dateStart',
        new Date(month.getFullYear(), month.getMonth(), 1).toISOString().slice(0, 10)
      )
      .set(
        'dateFinish',
        new Date(month.getFullYear(), month.getMonth() + 1, 0).toISOString().slice(0, 10)
      )
      .set('size', 2000);

    if (symbol !== null && symbol.id !== 0) {
      params = params.set('symbol', symbol.id);
    }

    return forkJoin({
      rawExpenses: this.getExpenses(params).pipe(catchError(() => of(emptyPage<Expense>()))),
      rawInvestments: this.getInvestments(params).pipe(
        catchError(() => of(emptyPage<Investment>()))
      ),
      incomes: this.getIncomes(params).pipe(catchError(() => of(emptyPage<Income>()))),
      wishes: this.getWishes(params).pipe(catchError(() => of(emptyPage<Wish>()))),
    }).pipe(
      map((data) => {
        const expenses = this.reduceExpenses(data.rawExpenses);
        const investments = this.reduceInvestments(data.rawInvestments);
        return {
          ...data,
          expenses,
          investments,
        };
      })
    );
  }

  loadCurrencies() {
    return this.getCurrencies().pipe(catchError(() => of([])));
  }

  monthlyLineChartData(
    expenses: Expense[],
    incomes: Income[],
    wishes: Wish[],
    investments: Investment[],
    date: Date = new Date()
  ): LineChartData {
    const totalDaysOfMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
    const labels = Array.from({ length: totalDaysOfMonth }, (_, i) => i + 1);

    return {
      labels: labels,
      datasets: [
        {
          label: 'Expense',
          data: this.mapDataPerDay(expenses, labels),
          borderColor: 'rgb(255, 99, 132)',
        },
        {
          label: 'Income',
          data: this.mapDataPerDay(incomes, labels),
          borderColor: 'rgb(75, 192, 192)',
        },
        {
          label: 'Wish',
          data: this.mapDataPerDay(wishes, labels),
          borderColor: 'rgb(153, 102, 255)',
        },
        {
          label: 'Investment',
          data: this.mapDataPerDay(investments, labels),
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

  private reduceExpenses(expensesPage: Page<Expense>): Expense[] {
    return expensesPage.content
      .reduce<Expense[]>((acc, expense) => {
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
      }, [])
      .map((exp) => ({ ...exp, amount: this.round(exp.amount) }));
  }

  private reduceInvestments(filteredData: Page<Investment>): Investment[] {
    return filteredData.content
      .reduce<Investment[]>((acc, investment) => {
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
      }, [])
      .map((inv) => ({ ...inv, amount: this.round(inv.amount) }));
  }

  private round(n: number) {
    return Math.round((n + Number.EPSILON) * 100) / 100;
  }

  getIncomes(queryParams: HttpParams): Observable<Page<Income>> {
    return this.http.get<Page<Income>>(`${this.apiUrl}/${this.incomeUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }
  getWishes(queryParams: HttpParams): Observable<Page<Wish>> {
    return this.http.get<Page<Wish>>(`${this.apiUrl}/${this.wishesUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }

  private getCurrencies(): Observable<Currency[]> {
    return this.http.get<Currency[]>(`${this.apiUrl}/${this.currenciesUri}`, {
      withCredentials: true,
    });
  }

  private getExpenses(queryParams: HttpParams): Observable<Page<Expense>> {
    return this.http.get<Page<Expense>>(`${this.apiUrl}/${this.expenseUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }
  private getInvestments(queryParams: HttpParams): Observable<Page<Investment>> {
    return this.http.get<Page<Investment>>(`${this.apiUrl}/${this.investmentUri}`, {
      withCredentials: true,
      params: queryParams,
    });
  }
}
