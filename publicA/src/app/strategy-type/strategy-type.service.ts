import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateStrategyTypeDto, StrategyType } from '../shared/models/strategy-type.model';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class StrategyTypeService {
  private readonly apiUrl = `${environment.apiUrl}/api/strategies`;
  private http = inject(HttpClient);

  constructor() {}

  getStrategyTypes(
    pageSize: number = 5,
    pageIndex: number = 0,
    sort: string[] = ['id', 'asc'],
    text: string = ''
  ): Observable<Page<StrategyType>> {
    let params = new HttpParams()
      .set('page', pageIndex)
      .set('size', pageSize)
      .set('sort', sort.join(','));
    if (text.trim()) {
      params = params.set('text', text);
    }
    return this.http.get<Page<StrategyType>>(`${this.apiUrl}`, { withCredentials: true, params });
  }

  postStrategyType(strategyType: CreateStrategyTypeDto): Observable<StrategyType> {
    return this.http.post<StrategyType>(this.apiUrl, strategyType, { withCredentials: true });
  }

  deleteStrategies(ids: Set<number>): Observable<void> {
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
