import { Component, OnInit, HostListener, ElementRef, Inject, Renderer2 } from '@angular/core';
import { ContentViewerService } from './content-viewer.service';
import { interval } from 'rxjs';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-content-viewer',
  templateUrl: './content-viewer.component.html',
  styleUrls: ['./content-viewer.component.css']
})
export class ContentViewerComponent implements OnInit {
  imageToShow: any;
  public innerHigth: any;
  public currentOpacity = 1;
  public currentWidth;
  public currentHeight;

  constructor( private renderer: Renderer2, private contentViewerService: ContentViewerService, private el: ElementRef, @Inject(DOCUMENT) private document: Document) { }

  ngOnInit(): void {
    this.innerHigth = window.innerHeight;


    interval(1000 * 60).subscribe(x => {
      this.contentViewerService.getCurrentImage().subscribe(image => {
        var fields = image.split("|");
        
        this.extractSises(fields[0]);
        this.changeImage(fields[2]);
        this.setNewGreadient(fields[1]);
      })
    });
  }

  extractSises(sizes) {
    var partSize = sizes.split(";");
    this.currentWidth = partSize[0];
    this.currentHeight = partSize[1];
  }

  setNewGreadient(color: string) {
    this.renderer.setStyle(this.document.body, "background", `radial-gradient(${color})`)
  }


  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.innerHigth = window.innerHeight;
  }

  applyStyles() {

    var width;

    if(this.currentWidth && this.currentHeight && this.innerHigth) {
      
        if(this.currentWidth > this.currentHeight) {
          var percent = (this.currentHeight - this.innerHigth) / this.currentHeight;
          width = this.currentWidth - (this.currentWidth * percent);
        } else {
          var percent = (this.currentWidth - this.innerHigth) / this.currentWidth;
          width = this.currentHeight - (this.currentHeight * percent);
        }
    }

    var styles;
    if(!width) {
      styles =  {'max-width': '53em', 'max-height' : this.innerHigth+'px', 'margin':'0 auto', 'opacity':this.currentOpacity};
    } else {
      styles =  {'max-width': width+'px', 'max-height' : this.innerHigth+'px', 'margin':'0 auto', 'opacity':this.currentOpacity};
    }
    
    return styles;
  }

  changeImage(image) {

    this.changeOpacity(1, 0.5, 0.001);
    this.imageToShow = image;
    this.changeOpacity(0.5, 1, 0.002);
  }

  changeOpacity(from, to, pacing) {

    if(from < to) {
      for(var i= from; i <= to; i+=pacing) {
          this.currentOpacity = i;
      }
    } else {
      for(var i= from; i >= to; i-=pacing) {
        this.currentOpacity = i;
    }
    }
  }
}
