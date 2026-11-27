import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateObjectiveDto, Objective } from '../shared/models/objective.model';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class ObjectiveService {
  private readonly apiUrl = ` ${environment.apiUrl}/api/objectives`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getObjective(
    pageSize: number = 5,
    pageIndex: number = 0,
    sort: string[] = ['id', 'asc'],
    text: string = ''
  ): Observable<Page<Objective>> {
    let params = new HttpParams()
      .set('page', pageIndex)
      .set('size', pageSize)
      .set('sort', sort.join(','));
    if (text.trim()) {
      params = params.set('text', text);
    }
    return this.http.get<Page<Objective>>(`${this.apiUrl}`, { withCredentials: true, params });
  }

  postObjective(objective: CreateObjectiveDto): Observable<Objective> {
    return this.http.post<Objective>(this.apiUrl, objective, { withCredentials: true });
  }

  deleteObjectives(ids: Set<number>): Observable<void> {
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
