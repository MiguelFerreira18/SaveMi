import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateWishDto, Wish } from '../shared/models/wish.model';

@Injectable({
  providedIn: 'root',
})
export class WishService {
  private readonly apiUrl = `${environment.apiUrl}/api/wishes`;
  private http: HttpClient = inject(HttpClient);

  constructor() {}

  getWishes(): Observable<Wish[]> {
    return this.http.get<Wish[]>(`${this.apiUrl}`, { withCredentials: true });
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
