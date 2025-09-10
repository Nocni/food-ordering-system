import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ErrorMessage } from '../models/error-message.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorMessageService {
  private apiUrl = 'http://localhost:8080/api/errors';

  constructor(private http: HttpClient) { }

  getErrors(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  getAllErrors(): Observable<ErrorMessage[]> {
    return this.http.get<ErrorMessage[]>(`${this.apiUrl}/all`);
  }
}
