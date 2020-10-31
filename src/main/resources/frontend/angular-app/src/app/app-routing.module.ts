import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { ContentViewerComponent } from './content-viewer/content-viewer.component';
import { SourcesAddComponent } from './sources/sources-add/sources-add.component';
import { SourcesListComponent } from './sources/sources-list/sources-list.component';


const routes: Routes = [
  { path: 'add-source', component: SourcesAddComponent },
  { path: 'source-list', component: SourcesListComponent },
  { path: 'content-viewer', component: ContentViewerComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
