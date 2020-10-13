import { Component, OnInit, HostListener } from '@angular/core';
import { ContentViewerService } from './content-viewer.service';
import { interval } from 'rxjs';

@Component({
  selector: 'app-content-viewer',
  templateUrl: './content-viewer.component.html',
  styleUrls: ['./content-viewer.component.css']
})
export class ContentViewerComponent implements OnInit {

  imageToShow: any;
  public innerHigth: any;
  constructor(private contentViewerService: ContentViewerService) { }

  ngOnInit(): void {
    this.innerHigth = window.innerHeight;
    interval(2000 * 60).subscribe(x => {
      this.contentViewerService.getCurrentImage().subscribe(image => {
        this.imageToShow=image;
      })
    });
  }
  
  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.innerHigth = window.innerHeight;
  }

  applyStyles() {
    const styles = {'max-height' : this.innerHigth+'px', 'margin':'0 auto'};
    return styles;
  }
}
