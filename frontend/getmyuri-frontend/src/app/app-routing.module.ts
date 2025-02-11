import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListRoutesComponent } from './components/list-routes/list-routes.component';
import { AddRouteComponent } from './components/add-route/add-route.component';
import { RedirectComponent } from './components/redirect/redirect.component';

const routes: Routes = [
  { path: '', component: ListRoutesComponent, pathMatch: 'full' },
  { path: 'add', component: AddRouteComponent , pathMatch: 'full' },
  { path: '**', component: RedirectComponent } // Catch-all to handle all paths dynamically
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
