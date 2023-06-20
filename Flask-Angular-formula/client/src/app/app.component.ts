import { Component, ElementRef } from '@angular/core';
import { OverlayContainer } from '@angular/cdk/overlay';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isDarkTheme = false;

  constructor(private _overlay: OverlayContainer, private _elementRef: ElementRef) {

  }

  // Dynamically adds/removes class to parent container to apply different theme at runtime
  toggleTheme() {
    this.isDarkTheme = !this.isDarkTheme;
    if (this.isDarkTheme) {
      this._elementRef.nativeElement.classList.add('dark-theme');
      this._overlay.getContainerElement().classList.add('dark-theme');
    } else {
      this._elementRef.nativeElement.classList.remove('dark-theme');
      this._overlay.getContainerElement().classList.remove('dark-theme');
    }
  }
}
