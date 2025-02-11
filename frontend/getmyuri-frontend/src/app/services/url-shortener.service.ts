import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UrlShortenerService {
  private backendUrl = 'http://localhost:9090';

  constructor(private http: HttpClient) { }

  addRoute(path: string, targetUrl: string): Observable<string> {
    return this.http.post(`${this.backendUrl}/add-route?path=${path}&targetUrl=${targetUrl}`, {}, { responseType: 'text' });
  }

  getRedirect(path: string): Observable<string> {
    return this.http.get(`${this.backendUrl}/${path}`, { responseType: 'text' });
  }
}
