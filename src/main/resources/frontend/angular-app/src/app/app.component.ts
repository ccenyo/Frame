import { Component, Renderer2, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { SourceService } from './sources/source.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-app';
  public sources = [];
  constructor(public sourceService: SourceService) {
  }

  ngOnInit(): void {
    this.sourceService.getAllSources().subscribe(source => {
      this.sources = source;
    });
  }
}
