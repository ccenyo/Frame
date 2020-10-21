import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { SourcesAddComponent } from './sources/sources-add/sources-add.component';


const routes: Routes = [
  { path: 'add-source', component: SourcesAddComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
