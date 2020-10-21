import { Component, OnInit } from '@angular/core';
import { SourceService } from './source.service';
import { source } from './sources.model';

@Component({
  selector: 'app-sources',
  templateUrl: './sources.component.html',
  styleUrls: ['./sources.component.css']
})
export class SourcesComponent implements OnInit {
  public sources: source[] = [];
  
  constructor(private sourceService: SourceService) { }

  ngOnInit(): void {
    this.sourceService.getAllSources().subscribe(source => {
      this.sources = source;
    });
  }
}
