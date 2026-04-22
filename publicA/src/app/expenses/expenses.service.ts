import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateExpenseDto, Expense } from '../shared/models/expense.model';

@Injectable({
  providedIn: 'root',
})
export class ExpensesService {
  private readonly apiUrl = `${environment.apiUrl}/api/expenses`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getExpenses(): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}`, { withCredentials: true });
  }

  postExpense(expense: CreateExpenseDto): Observable<Expense> {
    return this.http.post<Expense>(this.apiUrl, expense, { withCredentials: true });
  }

  deleteExpenses(ids: Set<number>): Observable<void> {
    if (ids.size > 1) {
      return this.http.delete<void>(`${this.apiUrl}`, {
        body: { ids: Array.from(ids) },

        withCredentials: true,
      });
    } else {
      const [id] = ids;
      return this.http.delete<void>(`${this.apiUrl}/${id}`, {
        withCredentials: true,
      });
    }
  }
}
