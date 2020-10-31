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
    //sources.push(new source(1, "source1", "Synology", "localhost", 5050, "cenyo","/temp"))
    //sources.push(new source(2, "source2", "Synology", "localhost", 5050, "cenyo","/temp"))
    //sources.push(new source(3, "source4", "Synology", "localhost", 5050, "cenyo","/temp"))
    //sources.push(new source(4, "source4", "Synology", "localhost", 5050, "cenyo","/temp"))
    //sources.push(new source(5, "source5", "Synology", "localhost", 5050, "cenyo","/temp"))
    
    //this.http.get<source[]>('http://localhost:8080/source/getAll');
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
