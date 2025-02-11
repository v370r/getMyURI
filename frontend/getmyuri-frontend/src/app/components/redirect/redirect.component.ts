import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UrlShortenerService } from 'src/app/services/url-shortener.service';

@Component({
  selector: 'app-redirect',
  templateUrl: './redirect.component.html',
  styleUrls: ['./redirect.component.scss']
})
export class RedirectComponent {
  constructor(private route: ActivatedRoute, private urlService: UrlShortenerService, private router: Router) {}

  ngOnInit() {
    const path = this.route.snapshot.paramMap.get('path');
    if (path) {
      this.urlService.getRedirect(path).subscribe(targetUrl => {

        if (targetUrl.startsWith("Redirecting to: ")) {
          let finalUrl = targetUrl.replace("Redirecting to: ", "").trim();

          // Ensure it has a valid URL scheme
          if (!/^https?:\/\//i.test(finalUrl)) {
            finalUrl = "https://" + finalUrl; // Default to HTTPS
          }

          console.log("Redirecting now to:", finalUrl);

          // Ensure a real browser redirect
          window.location.assign(finalUrl);
        } else {
          alert("404 - Route not found");
          this.router.navigate(['/']);
        }
      });
    }
  }
}
