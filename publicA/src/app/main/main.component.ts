import { Component, ViewChild } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { ToastComponent } from '../shared/toast/toast.component';

@Component({
  selector: 'app-main',
  imports: [
    RouterOutlet,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    ToastComponent,
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css',
})
export class MainComponent {
  @ViewChild('sidenav') sidenav!: MatSidenav;
  // INFO: As more paths appear they are added here
  routerLinks = [
    {
      name: 'Dashboard',
      link: 'dashboard',
    },
    {
      name: 'Investments',
      link: 'investment',
    },
    {
      name: 'Income',
      link: 'income',
    },
    {
      name: 'Expenses',
      link: 'expense',
    },
    {
      name: 'Wishes',
      link: 'wish',
    },
    {
      name: 'Objectives',
      link: 'objective',
    },
    {
      name: 'Currency',
      link: 'currency',
    },
    {
      name: 'Category',
      link: 'category',
    },
    {
      name: 'Strategy types',
      link: 'strategy-type',
    },
    {
      name: 'Login',
      link: 'login',
    },
  ];

  toggleSidebar() {
    this.sidenav.toggle();
  }
}
