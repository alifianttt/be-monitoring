import { reactive, toRefs } from 'vue'
import { fetchCallMonitoring } from '../services/callMonitoringApi'
import type {
  CallMonitoringRecord,
  SentimentFilterValue,
  SortDirection,
  SortableColumn
} from '../types/callMonitoring'

const DEBOUNCE_MS = 350

interface TableState {
  records: CallMonitoringRecord[]
  page: number
  totalPages: number
  totalElements: number
  loading: boolean
  error: string | null
}

interface FilterState {
  search: string
  startDate: string
  endDate: string
  sentiment: SentimentFilterValue
}

interface SortState {
  sortBy: SortableColumn
  sortDir: SortDirection
}

/**
 * Owns all Monitoring-table state: filters, sort, and pagination.
 * Every state change re-queries the backend rather than filtering
 * an already-fetched array, per the "no hardcoded/main data on the
 * frontend" rule.
 */
export function useCallMonitoring() {
  const state = reactive<TableState>({
    records: [],
    page: 0,
    totalPages: 0,
    totalElements: 0,
    loading: false,
    error: null
  })

  const filters = reactive<FilterState>({
    search: '',
    startDate: '',
    endDate: '',
    sentiment: ''
  })

  const sort = reactive<SortState>({
    sortBy: 'callTimestamp',
    sortDir: 'desc'
  })

  let debounceTimer: ReturnType<typeof setTimeout> | undefined

  async function load(): Promise<void> {
    state.loading = true
    state.error = null
    try {
      const data = await fetchCallMonitoring({
        search: filters.search,
        startDate: filters.startDate,
        endDate: filters.endDate,
        sentiment: filters.sentiment,
        sortBy: sort.sortBy,
        sortDir: sort.sortDir,
        page: state.page
      })
      state.records = data.content
      state.totalPages = data.totalPages
      state.totalElements = data.totalElements
    } catch (err) {
      state.error = 'Failed to load call monitoring data. Please try again.'
      state.records = []
    } finally {
      state.loading = false
    }
  }

  function resetToFirstPageAndLoad(): void {
    state.page = 0
    load()
  }

  function setSearch(value: string): void {
    filters.search = value
    clearTimeout(debounceTimer)
    debounceTimer = setTimeout(resetToFirstPageAndLoad, DEBOUNCE_MS)
  }

  function setPeriod(startDate: string, endDate: string): void {
    filters.startDate = startDate
    filters.endDate = endDate
    resetToFirstPageAndLoad()
  }

  function setSentiment(value: SentimentFilterValue): void {
    filters.sentiment = value
    resetToFirstPageAndLoad()
  }

  function toggleSort(column: SortableColumn): void {
    if (sort.sortBy === column) {
      sort.sortDir = sort.sortDir === 'asc' ? 'desc' : 'asc'
    } else {
      sort.sortBy = column
      sort.sortDir = 'asc'
    }
    load()
  }

  function nextPage(): void {
    if (state.page + 1 < state.totalPages) {
      state.page += 1
      load()
    }
  }

  function prevPage(): void {
    if (state.page > 0) {
      state.page -= 1
      load()
    }
  }

  return {
    ...toRefs(state),
    filters,
    sort,
    load,
    setSearch,
    setPeriod,
    setSentiment,
    toggleSort,
    nextPage,
    prevPage
  }
}
