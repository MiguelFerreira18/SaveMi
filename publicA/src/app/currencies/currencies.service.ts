import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateCurrencyDto, Currency } from '../shared/models/currency.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CurrenciesService {
  private readonly apiUrl = `${environment.apiUrl}/api/currency`;
  constructor(private http: HttpClient) {}

  getCurrencies(): Observable<Currency[]> {
    return this.http.get<Currency[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  postCurrencies(currency: CreateCurrencyDto): Observable<Currency> {
    return this.http.post<Currency>(this.apiUrl, currency, { withCredentials: true });
  }

  deleteCurrencies(ids: Set<number>): Observable<void> {
    if (ids.size > 1) {
      return this.http.post<void>(
        `${this.apiUrl}/bulk-delete`,
        {
          ids: Array.from(ids),
        },
        {
          withCredentials: true,
        }
      );
    } else {
      const [id] = ids;
      return this.http.delete<void>(`${this.apiUrl}/${id}`, {
        withCredentials: true,
      });
    }
  }
}
