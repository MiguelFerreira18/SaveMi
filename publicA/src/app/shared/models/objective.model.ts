export interface Objective {
  id: number;
  symbol: string;
  description: string;
  amount: number;
  userId: string;
  target: number;
}

export interface CreateObjectiveDto {
  currencyId: number;
  amount: number;
  description: string;
  target: number;
}
