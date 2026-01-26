import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateStrategyTypeDto, StrategyType } from '../shared/models/strategy-type.model';

@Injectable({
  providedIn: 'root',
})
export class StrategyTypeService {
  private readonly apiUrl = `${environment.apiUrl}/api/strategy-type`;
  private http = inject(HttpClient);

  constructor() {}

  getStrategyTypes(): Observable<StrategyType[]> {
    return this.http.get<StrategyType[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  postStrategyType(strategyType: CreateStrategyTypeDto): Observable<StrategyType> {
    return this.http.post<StrategyType>(this.apiUrl, strategyType, { withCredentials: true });
  }

  deleteStrategies(ids: Set<number>): Observable<void> {
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
