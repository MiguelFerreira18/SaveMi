import { formatDate } from '@angular/common';
import { Page } from '../models/page';

export function formatDateFromType<T extends { date: Date }>(
  page: Page<T>
): Page<T & { formattedDate: string }> {
  return {
    ...page,
    content: page.content.map((item) => ({
      ...item,
      formattedDate: dateFormatter(item.date),
    })),
  };
}

export function dateFormatter(date: Date | number[]): string {
  if (!date) return '';

  const value = Array.isArray(date) ? new Date(date[0], date[1] - 1, date[2]) : date;

  return formatDate(value, 'dd-MM-yyyy', 'en-US');
}
