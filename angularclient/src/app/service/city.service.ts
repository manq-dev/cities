import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class CityService {

  private readonly citiesUrl: string;

  constructor(private http: HttpClient) {
    this.citiesUrl = 'http://localhost:8080/cities';
  }

  public findAll(params: any): Observable<any> {
    return this.http.get<any>(this.citiesUrl, { params });
  }

  public update(id: any, data: any): Observable<any> {
    return this.http.patch(`${this.citiesUrl}/${id}`, data);
  }

}
