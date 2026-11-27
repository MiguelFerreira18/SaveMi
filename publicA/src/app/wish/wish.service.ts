import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateWishDto, Wish } from '../shared/models/wish.model';
import { Page } from '../shared/models/page';

@Injectable({
  providedIn: 'root',
})
export class WishService {
  private readonly apiUrl = `${environment.apiUrl}/api/wishes`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getWishes(
    pageSize: number = 5,
    pageIndex: number = 0,
    sort: string[] = ['id,asc'],
    text: string = ''
  ): Observable<Page<Wish>> {
    let params = new HttpParams()
      .set('size', pageSize)
      .set('page', pageIndex)
      .set('sort', sort.join(','));
    if (text.trim()) {
      params = params.set('text', text);
    }

    return this.http.get<Page<Wish>>(`${this.apiUrl}`, { withCredentials: true, params });
  }
  postWishes(wish: CreateWishDto): Observable<Wish> {
    return this.http.post<Wish>(this.apiUrl, wish, { withCredentials: true });
  }

  deleteWishes(ids: Set<number>): Observable<void> {
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
