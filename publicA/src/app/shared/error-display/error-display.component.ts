
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-error-display',
  imports: [MatIconModule],
  template: `
    <div class="h-150 flex flex-col justify-center items-center text-center p-8">
      <mat-icon class="text-red-500 text-6xl mb-4">error_outline</mat-icon>
      <h2 class="text-xl font-semibold text-gray-800 mb-2">{{ title }}</h2>
      <p class="text-gray-600">{{ message }}</p>
      <p>You should try again later</p>
    </div>
  `,
  styleUrl: './error-display.component.css',
})
export class ErrorDisplayComponent {
  @Input() title: string = 'Oops! Something wen wrong';
  @Input() message: string = 'We encountered an error. Please try again';
}
