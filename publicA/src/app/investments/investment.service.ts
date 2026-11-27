import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateInvestmentDto, Investment } from '../shared/models/investment.model';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class InvestmentService {
  private readonly apiUrl = `${environment.apiUrl}/api/investments`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getInvestments(
    size: number = 5,
    page: number = 0,
    sort: string[] = ['id', 'asc'],
    text: string = ''
  ): Observable<Page<Investment>> {
    let params = new HttpParams().set('size', size).set('page', page).set('sort', sort.join(','));
    if (text.trim()) {
      params.set('text', text);
    }
    return this.http.get<Page<Investment>>(`${this.apiUrl}`, {
      withCredentials: true,
      params: params,
    });
  }
  postInvestments(investment: CreateInvestmentDto): Observable<Investment> {
    return this.http.post<Investment>(this.apiUrl, investment, { withCredentials: true });
  }

  deleteInvestments(ids: Set<number>): Observable<void> {
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
