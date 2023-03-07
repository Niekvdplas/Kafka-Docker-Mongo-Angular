import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';
import { MessageStreamComponent } from './message-stream/message-stream.component';
import { PDFComponent } from './pdfs/pdf.component';

const routes: Route[] = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: MessageStreamComponent },
  { path: 'pdf', component: PDFComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
