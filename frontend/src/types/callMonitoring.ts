export type SentimentFilterValue = 'BELOW_70' | 'AT_OR_ABOVE_70' | ''

export type SortDirection = 'asc' | 'desc'

export type SortableColumn =
  | 'callId'
  | 'callTimestamp'
  | 'csName'
  | 'customerName'
  | 'sentimentScore'

export interface CallMonitoringRecord {
  id: number
  callId: string
  callTimestamp: string
  csName: string
  customerName: string
  sentimentScore: number
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface CallMonitoringQueryParams {
  search?: string
  startDate?: string
  endDate?: string
  sentiment?: SentimentFilterValue
  sortBy: SortableColumn
  sortDir: SortDirection
  page: number
}
