import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateCurrencyDto, Currency } from '../shared/models/currency.model';
import { environment } from '../../environments/environment';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class CurrenciesService {
  private readonly apiUrl = `${environment.apiUrl}/api/currencies`;
  constructor(private http: HttpClient) {}

  getCurrencies(
    pageSize: number = 5,
    pageIndex: number = 0,
    sort: string[] = ['id', 'asc'],
    text: string = ''
  ): Observable<Page<Currency>> {
    let params = new HttpParams()
      .set('page', pageIndex)
      .set('size', pageSize)
      .set('sort', sort.join(','));
    if (text.trim()) {
      params = params.set('text', text);
    }
    return this.http.get<Page<Currency>>(`${this.apiUrl}`, { withCredentials: true, params });
  }

  postCurrencies(currency: CreateCurrencyDto): Observable<Currency> {
    return this.http.post<Currency>(this.apiUrl, currency, { withCredentials: true });
  }

  deleteCurrencies(ids: Set<number>): Observable<void> {
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
