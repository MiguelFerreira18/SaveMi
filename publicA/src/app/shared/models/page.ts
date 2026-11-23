export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface Page<T> {
  content: T[];
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // current page (1-based)
  numberOfElements: number; // items in the current page
  first: boolean;
  last: boolean;
  empty: boolean;
  sort: Sort;
}

export function emptyPage<T>(): Page<T> {
  return {
    content: [],
    pageable: {
      pageNumber: 0,
      pageSize: 0,
      sort: { empty: true, sorted: false, unsorted: true },
      offset: 0,
      paged: true,
      unpaged: false,
    },
    totalElements: 0,
    totalPages: 0,
    size: 0,
    number: 0,
    numberOfElements: 0,
    first: true,
    last: true,
    empty: true,
    sort: { empty: true, sorted: false, unsorted: true },
  };
}
