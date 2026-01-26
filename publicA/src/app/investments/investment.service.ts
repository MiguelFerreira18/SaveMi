import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateInvestmentDto, Investment } from '../shared/models/investment.model';

@Injectable({
  providedIn: 'root',
})
export class InvestmentService {
  private readonly apiUrl = `${environment.apiUrl}/api/investment`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getInvestments(): Observable<Investment[]> {
    return this.http.get<Investment[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }
  postInvestments(investment: CreateInvestmentDto): Observable<Investment> {
    return this.http.post<Investment>(this.apiUrl, investment, { withCredentials: true });
  }

  deleteInvestments(ids: Set<number>): Observable<void> {
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
