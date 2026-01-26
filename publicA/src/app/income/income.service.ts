import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateIncomeDto, Income } from '../shared/models/income.model';

@Injectable({
  providedIn: 'root',
})
export class IncomeService {
  private readonly apiUrl = ` ${environment.apiUrl}/api/income`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getIncome(): Observable<Income[]> {
    return this.http.get<Income[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  postIncome(income: CreateIncomeDto): Observable<Income> {
    return this.http.post<Income>(this.apiUrl, income, { withCredentials: true });
  }

  deleteIncome(ids: Set<number>): Observable<void> {
    if (ids.size > 1) {
      return this.http.post<void>(
        `${this.apiUrl}/bulk-delete`,
        { ids: Array.from(ids) },
        { withCredentials: true }
      );
    } else {
      const [id] = ids;
      return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
    }
  }
}
