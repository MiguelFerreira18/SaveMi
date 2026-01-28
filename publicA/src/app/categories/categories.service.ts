import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, CreateCategoryDto } from '../shared/models/category.model';

@Injectable({
  providedIn: 'root',
})
export class CategoriesService {
  private readonly apiUrl = `${environment.apiUrl}/api/category`;
  constructor(private http: HttpClient) {}

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  postCategory(category: CreateCategoryDto): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category, { withCredentials: true });
  }

  deleteCategories(ids: Set<number>): Observable<void> {
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
