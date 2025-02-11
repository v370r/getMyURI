import { Component } from '@angular/core';
import { UrlShortenerService } from 'src/app/services/url-shortener.service';

@Component({
  selector: 'app-add-route',
  templateUrl: './add-route.component.html',
  styleUrls: ['./add-route.component.scss']
})
export class AddRouteComponent {
  path = '';
  targetUrl = '';
  message = '';

  constructor(private urlService: UrlShortenerService) { }

  addRoute() {
    this.urlService.addRoute(this.path, this.targetUrl).subscribe(response => {
      this.message = response;
    });
  }
}
