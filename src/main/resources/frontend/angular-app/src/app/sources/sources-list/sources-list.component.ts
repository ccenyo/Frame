import { Component, Input, OnInit } from '@angular/core';
import { SourceService } from '../source.service';
import { source } from '../sources.model';

@Component({
  selector: 'app-sources-list',
  templateUrl: './sources-list.component.html',
  styleUrls: ['./sources-list.component.css']
})
export class SourcesListComponent implements OnInit {

  @Input() sources;

  constructor(private sourceService: SourceService) {

  }

  ngOnInit(): void {
  }

}
