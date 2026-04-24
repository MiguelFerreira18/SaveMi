import { Routes } from '@angular/router';
import { AuthGuardService } from './auth/auth-guard.service';

//INFO: PATHS ARE BY LISTED AS ORDER OF IMPLMENTATION BUT IN MAIN.ts THEY ARE LISTED BY FEATURE
export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./auth/auth/auth.component').then((m) => m.AuthComponent),
    title: 'Auth Page',
  },
  {
    path: '',
    loadComponent: () => import('./main/main.component').then((m) => m.MainComponent),
    canActivate: [AuthGuardService],
    children: [
      {
        path: 'category',
        loadComponent: () =>
          import('./categories/categories.component').then((m) => m.CategoriesComponent),
        title: 'Categories Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'currency',
        loadComponent: () =>
          import('./currencies/currencies.component').then((m) => m.CurrenciesComponent),
        title: 'Currency Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'strategy-type',
        loadComponent: () =>
          import('./strategy-type/strategy-type.component').then((m) => m.StrategyTypeComponent),
        title: 'Strategy Type Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'expense',
        loadComponent: () =>
          import('./expenses/expenses.component').then((m) => m.ExpensesComponent),
        title: 'Expense Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'income',
        loadComponent: () => import('./income/income.component').then((m) => m.IncomeComponent),
        title: 'Income Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'wish',
        loadComponent: () => import('./wish/wish.component').then((m) => m.WishComponent),
        title: 'Wish Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'investment',
        loadComponent: () =>
          import('./investments/investment.component').then((m) => m.InvestmentComponent),
        title: 'Investment Page',
        canActivate: [AuthGuardService],
      },
      {
        path: '',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then((m) => m.DashboardComponent),
        title: 'Dashboard Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'objective',
        loadComponent: () =>
          import('./objective/objective.component').then((m) => m.ObjectiveComponent),
        title: 'Objective Page',
        canActivate: [AuthGuardService],
      },
    ],
  },
];
