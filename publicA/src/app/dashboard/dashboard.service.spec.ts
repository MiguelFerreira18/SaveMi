import { TestBed } from '@angular/core/testing';
import { HttpParams, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DashboardService } from './dashboard.service';
import { environment } from '../../environments/environment';
import { Expense } from '../shared/models/expense.model';
import { Income } from '../shared/models/income.model';
import { Wish } from '../shared/models/wish.model';
import { Currency } from '../shared/models/currency.model';
import { Investment } from '../shared/models/investment.model';
import { LineChartData } from '../shared/monthly-line-graph/monthly-line-graph.component';

describe('DashboardService', () => {
  let service: DashboardService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/api`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [DashboardService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(DashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('loadAllData', () => {
    it('should load, filter, reduce and round all data correctly', () => {
      const targetMonth = new Date(2024, 3, 15);

      const mockExpenses: Expense[] = [
        {
          id: 1,
          category: 'Food',
          symbol: '€',
          description: 'Lunch',
          amount: 10.123,
          userId: '1',
          date: targetMonth,
        },
        {
          id: 2,
          category: 'Food',
          symbol: '€',
          description: 'Dinner',
          amount: 20.456,
          userId: '1',
          date: targetMonth,
        },
      ];

      const mockInvestments: Investment[] = [
        {
          id: 1,
          strategyType: 'Stocks',
          symbol: '€',
          description: 'AAPL',
          amount: 100.789,
          userId: '1',
          date: targetMonth,
        },
        {
          id: 2,
          strategyType: 'Stocks',
          symbol: '€',
          description: 'GOOG',
          amount: 200.111,
          userId: '1',
          date: targetMonth,
        },
      ];

      const mockIncomes: Income[] = [
        {
          id: 1,
          symbol: '€',
          description: 'Salary',
          amount: 1000.555,
          userId: '1',
          date: targetMonth,
        },
      ];

      const mockWishes: Wish[] = [
        { id: 1, symbol: '€', description: 'Game', amount: 50.666, userId: '1', date: targetMonth },
      ];

      const mockCurrencies: Currency[] = [{ id: 1, name: 'Euro', symbol: '€' }];

      service.loadAllData(targetMonth).subscribe((data) => {
        expect(data.rawExpenses.length).toBe(2);
        expect(data.rawExpenses[0].amount).toBe(10.12);
        expect(data.rawExpenses[1].amount).toBe(20.46);

        expect(data.expenses.length).toBe(1);
        expect(data.expenses[0].category).toBe('Food');
        expect(data.expenses[0].amount).toBe(30.58);

        expect(data.rawInvestments.length).toBe(2);
        expect(data.investments.length).toBe(1);
        expect(data.investments[0].amount).toBe(300.9);

        expect(data.incomes[0].amount).toBe(1000.55);
        expect(data.wishes[0].amount).toBe(50.67);

        expect(data.currencies).toEqual(mockCurrencies);
      });

      const reqExp = httpMock.expectOne(`${apiUrl}/expenses?month=2024-04`);
      expect(reqExp.request.method).toBe('GET');
      reqExp.flush(mockExpenses);

      const reqInv = httpMock.expectOne(`${apiUrl}/investments?month=2024-04`);
      expect(reqInv.request.method).toBe('GET');
      reqInv.flush(mockInvestments);

      const reqInc = httpMock.expectOne(`${apiUrl}/incomes?month=2024-04`);
      expect(reqInc.request.method).toBe('GET');
      reqInc.flush(mockIncomes);

      const reqWish = httpMock.expectOne(`${apiUrl}/wishes?month=2024-04`);
      expect(reqWish.request.method).toBe('GET');
      reqWish.flush(mockWishes);

      const reqCur = httpMock.expectOne(`${apiUrl}/currencies`);
      expect(reqCur.request.method).toBe('GET');
      reqCur.flush(mockCurrencies);
    });

    it('should return empty arrays on error', () => {
      service.loadAllData().subscribe((data) => {
        expect(data.rawExpenses).toEqual([]);
        expect(data.rawInvestments).toEqual([]);
        expect(data.incomes).toEqual([]);
        expect(data.wishes).toEqual([]);
      });
      const today = new Date();
      const monthParam = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;

      httpMock.expectOne(`${apiUrl}/expenses?month=${monthParam}`).flush(null, {
        status: 500,
        statusText: 'Internal Server Error',
      });
      httpMock.expectOne(`${apiUrl}/investments?month=${monthParam}`).flush(null, {
        status: 500,
        statusText: 'Internal Server Error',
      });
      httpMock.expectOne(`${apiUrl}/incomes?month=${monthParam}`).flush(null, {
        status: 500,
        statusText: 'Internal Server Error',
      });
      httpMock.expectOne(`${apiUrl}/wishes?month=${monthParam}`).flush(null, {
        status: 500,
        statusText: 'Internal Server Error',
      });
      httpMock.expectOne(`${apiUrl}/currencies`).flush([]);
    });
  });

  describe('monthlyLineChartData', () => {
    it('should return a LineChartData Object', () => {
      const mockSymbol: string = '€';
      const mockIncomes: Income[] = [
        {
          id: 1,
          symbol: '€',
          description: 'Jan',
          amount: 100,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
        {
          id: 2,
          symbol: '€',
          description: 'Feb',
          amount: 200,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
      ];
      const mockExpenses: Expense[] = [
        {
          id: 1,
          category: 'Food',
          symbol: '€',
          description: 'Lunch',
          amount: 10.123,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
        {
          id: 2,
          category: 'Food',
          symbol: '€',
          description: 'Dinner',
          amount: 20.456,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
        {
          id: 3,
          category: 'Rent',
          symbol: '€',
          description: 'Rent',
          amount: 500,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
      ];

      const mockInvestments: Investment[] = [
        {
          id: 1,
          strategyType: 'Stocks',
          symbol: '€',
          description: 'AAPL',
          amount: 100.789,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
        {
          id: 2,
          strategyType: 'Stocks',
          symbol: '€',
          description: 'GOOG',
          amount: 200.111,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
      ];
      const mockWishes: Wish[] = [
        {
          id: 1,
          symbol: '€',
          description: 'AAPL',
          amount: 100.789,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
        {
          id: 2,
          symbol: '€',
          description: 'GOOG',
          amount: 200.111,
          userId: '1',
          date: new Date(2024, 0, 10),
        },
      ];
      const monthlyLineChartData: LineChartData = service.monthlyLineChartData(
        mockExpenses,
        mockIncomes,
        mockWishes,
        mockInvestments,
        mockSymbol,
        new Date(2024, 0, 10)
      );

      expect(monthlyLineChartData.labels.length).toBe(31);
      expect(monthlyLineChartData.datasets.length).toBe(4);
      expect(
        monthlyLineChartData.datasets.find((data) => data.label === 'Expense')
      ).not.toBeUndefined();
      expect(
        monthlyLineChartData.datasets.find((data) => data.label === 'Income')
      ).not.toBeUndefined();
      expect(
        monthlyLineChartData.datasets.find((data) => data.label === 'Investment')
      ).not.toBeUndefined();

      expect(
        monthlyLineChartData.datasets.find((data) => data.label === 'Wish')
      ).not.toBeUndefined();

      expect(monthlyLineChartData.datasets.every((d) => d.data.length > 0)).toBeTruthy();
    });
  });
});
