import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable, HostListener } from '@angular/core';

const optionRequete = {
  headers: new HttpHeaders({
    'Access-Control-Allow-Origin':'*',
    'Access-Control-Allow-Methods': 'GET,POST'
  }),
};

@Injectable({providedIn: 'root'})
export class ContentViewerService {
  constructor(private http: HttpClient) {}


  ngOnInit() {}

  getCurrentImage() {
    return this.http.get('http://localhost:8080/image/current', {responseType: 'text'});
  }

    updateBase() {
      return this.http.post('http://localhost:8080/image/updateBase', {responseType: 'text'});
    }
}
