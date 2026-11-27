import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateIncomeDto, Income } from '../shared/models/income.model';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class IncomeService {
  private readonly apiUrl = ` ${environment.apiUrl}/api/incomes`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getIncome(
    pageSize: number = 5,
    pageIndex: number = 0,
    sort: string[] = ['id', 'asc'],
    text: string = ''
  ): Observable<Page<Income>> {
    let params = new HttpParams()
      .set('size', pageSize)
      .set('page', pageIndex)
      .set('sort', sort.join(','));
    if (text.trim()) {
      params = params.set('text', text);
    }
    return this.http.get<Page<Income>>(`${this.apiUrl}`, { withCredentials: true, params });
  }

  postIncome(income: CreateIncomeDto): Observable<Income> {
    return this.http.post<Income>(this.apiUrl, income, { withCredentials: true });
  }

  deleteIncome(ids: Set<number>): Observable<void> {
    if (ids.size > 1) {
      return this.http.delete<void>(`${this.apiUrl}`, {
        body: { ids: Array.from(ids) },
        withCredentials: true,
      });
    } else {
      const [id] = ids;
      return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
    }
  }
}
