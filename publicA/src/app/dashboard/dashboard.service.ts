import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map, Observable, of, catchError, forkJoin } from 'rxjs';
import { Expense } from '../shared/models/expense.model';
import { Income } from '../shared/models/income.model';
import { Wish } from '../shared/models/wish.model';
import { Currency } from '../shared/models/currency.model';
import { Investment } from '../shared/models/investment.model';

interface DashboardData {
  expenses: Expense[];
  investments: Investment[];
  incomes: Income[];
  wishes: Wish[];
  currencies: Currency[];
}

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly apiUrl = `${environment.apiUrl}/api`;
  private readonly expenseUri = 'expense';
  private readonly investmentUri = 'investment';
  private readonly incomeUri = 'income';
  private readonly wishesUri = 'wish';
  private readonly currenciesUri = 'currency';
  private readonly http = inject(HttpClient);

  constructor() {}

  loadAllData(): Observable<DashboardData> {
    return forkJoin({
      expenses: this.reducedExpenses(),
      investments: this.reducedInvestments(),
      incomes: this.getIncomes(),
      wishes: this.getWishes(),
      currencies: this.getCurrencies(),
    }).pipe(map((data) => this.roundAllAmounts(data)));
  }

  reducedExpenses(month: Date = new Date()): Observable<Expense[]> {
    return this.getExpenses().pipe(
      map((data: Expense[]) => {
        const filteredData = data.filter((expense) => {
          const expenseDate = new Date(expense.date);
          return (
            expenseDate.getMonth() === month.getMonth() &&
            expenseDate.getFullYear() === month.getFullYear()
          );
        });

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
      }),
      catchError((err) => {
        console.error(err);
        return of([]);
      })
    );
  }
  reducedInvestments(month: Date = new Date()): Observable<Investment[]> {
    return this.getInvestments().pipe(
      map((data: Investment[]) => {
        const filteredData = data.filter((investment) => {
          const investmentDate = new Date(investment.date);
          return (
            investmentDate.getMonth() === month.getMonth() &&
            investmentDate.getFullYear() === month.getFullYear()
          );
        });

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
      }),
      catchError((err) => {
        console.error(err);
        return of([]);
      })
    );
  }

  getIncomes(month: Date = new Date()): Observable<Income[]> {
    return this.http
      .get<Income[]>(`${this.apiUrl}/${this.incomeUri}/all`, {
        withCredentials: true,
      })
      .pipe(
        map((data: Income[]) => {
          const filteredData = data.filter((income) => {
            const incomeDate = new Date(income.date);
            return (
              incomeDate.getMonth() === month.getMonth() &&
              incomeDate.getFullYear() === month.getFullYear()
            );
          });
          return filteredData;
        }),
        catchError((err) => {
          console.error(err);
          return of([]);
        })
      );
  }
  getWishes(month: Date = new Date()): Observable<Wish[]> {
    return this.http
      .get<Wish[]>(`${this.apiUrl}/${this.wishesUri}/all`, {
        withCredentials: true,
      })
      .pipe(
        map((data: Wish[]) => {
          const filteredData = data.filter((wish) => {
            const incomeDate = new Date(wish.date);
            return (
              incomeDate.getMonth() === month.getMonth() &&
              incomeDate.getFullYear() === month.getFullYear()
            );
          });
          return filteredData;
        }),
        catchError((err) => {
          console.error(err);
          return of([]);
        })
      );
  }

  getCurrencies(): Observable<Currency[]> {
    return this.http.get<Currency[]>(`${this.apiUrl}/${this.currenciesUri}/all`, {
      withCredentials: true,
    });
  }

  private getExpenses(): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}/${this.expenseUri}/all`, {
      withCredentials: true,
    });
  }
  private getInvestments(): Observable<Investment[]> {
    return this.http.get<Investment[]>(`${this.apiUrl}/${this.investmentUri}/all`, {
      withCredentials: true,
    });
  }

  private roundAllAmounts(data: DashboardData): DashboardData {
    return {
      expenses: this.roundAmounts(data.expenses),
      investments: this.roundAmounts(data.investments),
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
