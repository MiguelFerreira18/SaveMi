import { Routes } from '@angular/router';
import { CategoriesComponent } from './categories/categories.component';
import { AuthGuardService } from './auth/auth-guard.service';
import { AuthComponent } from './auth/auth/auth.component';
import { MainComponent } from './main/main.component';
import { CurrenciesComponent } from './currencies/currencies.component';
import { ExpensesComponent } from './expenses/expenses.component';
import { IncomeComponent } from './income/income.component';
import { WishComponent } from './wish/wish.component';
import { StrategyTypeComponent } from './strategy-type/strategy-type.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { InvestmentComponent } from './investments/investment.component';
import { ObjectiveComponent } from './objective/objective.component';

//INFO: PATHS ARE BY LISTED AS ORDER OF IMPLMENTATION BUT IN MAIN.ts THEY ARE LISTED BY FEATURE
export const routes: Routes = [
  {
    path: 'login',
    component: AuthComponent,
    title: 'Auth Page',
  },
  {
    path: '',
    component: MainComponent,
    canActivate: [AuthGuardService],
    children: [
      {
        path: 'category',
        component: CategoriesComponent,
        title: 'Categories Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'currency',
        component: CurrenciesComponent,
        title: 'Currency Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'strategy-type',
        component: StrategyTypeComponent,
        title: 'Strategy Type Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'expense',
        component: ExpensesComponent,
        title: 'Expense Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'income',
        component: IncomeComponent,
        title: 'Income Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'wish',
        component: WishComponent,
        title: 'Wish Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'investment',
        component: InvestmentComponent,
        title: 'Investment Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
        title: 'dashboard Page',
        canActivate: [AuthGuardService],
      },
      {
        path: 'objective',
        component: ObjectiveComponent,
        title: 'Objective Page',
        canActivate: [AuthGuardService],
      },
    ],
  },
];
