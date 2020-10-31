import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable, HostListener } from '@angular/core';
import { source } from './sources.model';

const optionRequete = {
  headers: new HttpHeaders({
    'Access-Control-Allow-Origin':'*',
    'Access-Control-Allow-Methods': 'GET,POST'
  }),
};

@Injectable({providedIn: 'root'})
export class SourceService {
  constructor(private http: HttpClient) {}


  ngOnInit() {}

  getAllSources(){
    var sources: source[] = [];
    return this.http.get<source[]>('http://localhost:8080/source/getAll');
  }

  checkFolder(sr: source) {
    return this.http.post<boolean>('http://localhost:8080/source/checkFolder',sr, optionRequete);
  }

  saveSource(sr: source) {
    return this.http.post('http://localhost:8080/source/save', sr, optionRequete);
  }

  tryConnexion(sr: source) {
    return this.http.post('http://localhost:8080/source/tryConnexion', sr, optionRequete);
  }

  save(sr: source) {
    return this.http.post<source>('http://localhost:8080/source/save', sr, optionRequete);
  }

}
