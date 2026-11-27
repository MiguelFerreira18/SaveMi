import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, CreateCategoryDto } from '../shared/models/category.model';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class CategoriesService {
  private readonly apiUrl = `${environment.apiUrl}/api/categories`;
  constructor(private http: HttpClient) {}

  getCategories(
    pageSize: number = 5,
    pageIndex: number = 0,
    sort: string[] = ['id', 'asc'],
    text: string = ''
  ): Observable<Page<Category>> {
    let params = new HttpParams()
      .set('size', pageSize)
      .set('page', pageIndex)
      .set('sort', sort.join(','));
    return this.http.get<Page<Category>>(`${this.apiUrl}`, { withCredentials: true, params });
  }

  postCategory(category: CreateCategoryDto): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category, { withCredentials: true });
  }

  deleteCategories(ids: Set<number>): Observable<void> {
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
