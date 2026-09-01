import { reactive, ref, toRefs, watch } from 'vue'
import { fetchCallMonitoring } from '../services/callMonitoringApi'

const DEBOUNCE_MS = 350

/**
 * Owns all Monitoring-table state: filters, sort, and pagination.
 * Every state change re-queries the backend rather than filtering
 * an already-fetched array, per the "no hardcoded/main data on the
 * frontend" rule.
 */
export function useCallMonitoring() {
  const state = reactive({
    records: [],
    page: 0,
    totalPages: 0,
    totalElements: 0,
    loading: false,
    error: null
  })

  const filters = reactive({
    search: '',
    startDate: '',
    endDate: '',
    sentiment: ''
  })

  const sort = reactive({
    sortBy: 'callTimestamp',
    sortDir: 'desc'
  })

  let debounceTimer = null

  async function load() {
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

  function resetToFirstPageAndLoad() {
    state.page = 0
    load()
  }

  function setSearch(value) {
    filters.search = value
    clearTimeout(debounceTimer)
    debounceTimer = setTimeout(resetToFirstPageAndLoad, DEBOUNCE_MS)
  }

  function setPeriod(startDate, endDate) {
    filters.startDate = startDate
    filters.endDate = endDate
    resetToFirstPageAndLoad()
  }

  function setSentiment(value) {
    filters.sentiment = value
    resetToFirstPageAndLoad()
  }

  function toggleSort(column) {
    if (sort.sortBy === column) {
      sort.sortDir = sort.sortDir === 'asc' ? 'desc' : 'asc'
    } else {
      sort.sortBy = column
      sort.sortDir = 'asc'
    }
    load()
  }

  function nextPage() {
    if (state.page + 1 < state.totalPages) {
      state.page += 1
      load()
    }
  }

  function prevPage() {
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
