import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateObjectiveDto, Objective } from '../shared/models/objective.model';

@Injectable({
  providedIn: 'root',
})
export class ObjectiveService {
  private readonly apiUrl = ` ${environment.apiUrl}/api/objective`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getObjective(): Observable<Objective[]> {
    return this.http.get<Objective[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  postObjective(objective: CreateObjectiveDto): Observable<Objective> {
    return this.http.post<Objective>(this.apiUrl, objective, { withCredentials: true });
  }

  deleteObjectives(ids: Set<number>): Observable<void> {
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
