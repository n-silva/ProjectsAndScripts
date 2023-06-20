import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {  throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
import {Functdata} from '../models/functdata';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private REST_API_SERVER = 'http://localhost:5000';

  constructor(private httpClient: HttpClient) { }

  handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error!';
    if (error.error instanceof ErrorEvent) {
      // Client-side errors
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side errors
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    window.alert(errorMessage);
    return throwError(errorMessage);
  }

  public getList(){
    return this.httpClient.get<Functdata[]>(this.REST_API_SERVER + '/search');
  }

  public searchById(id: string){
    return this.httpClient.get<Functdata>(this.REST_API_SERVER + '/search?id=' + id);
  }

  public searchByName(name: string){
    return this.httpClient.get<Functdata>(this.REST_API_SERVER + '/search?name=' + name);
  }

  public calculate(formula: string){
    return this.httpClient.get<object>(this.REST_API_SERVER + '?calc=' + formula);
  }

  public create(body: Partial<any>){
    return this.httpClient.post<Functdata>(this.REST_API_SERVER + '/create', body);
  }

  public update(id: string , body: Partial<any>){
    return this.httpClient.put<Functdata>(this.REST_API_SERVER + '/update/' + id, body);
  }

  public delete(id: string){
    return this.httpClient.delete(this.REST_API_SERVER + '/delete/' + id );
  }
}
