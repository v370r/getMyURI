import { Component } from '@angular/core';

@Component({
  selector: 'app-list-routes',
  templateUrl: './list-routes.component.html',
  styleUrls: ['./list-routes.component.scss']
})
export class ListRoutesComponent {
  routes = [
    { path: 'myWebsite', target: 'https://www.example.com' },
    { path: 'portfolio', target: 'https://myportfolio.com' }
  ];
}
