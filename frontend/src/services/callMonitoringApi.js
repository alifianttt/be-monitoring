import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const client = axios.create({
  baseURL: API_BASE_URL
})

/**
 * Fetches one page of call-monitoring records from the backend.
 * All search/filter/sort/pagination logic lives server-side; this
 * function only forwards the current UI state as query params.
 *
 * @param {object} params
 * @param {string} [params.search]
 * @param {string} [params.startDate] - ISO date (yyyy-MM-dd)
 * @param {string} [params.endDate] - ISO date (yyyy-MM-dd)
 * @param {'BELOW_70'|'AT_OR_ABOVE_70'} [params.sentiment]
 * @param {string} params.sortBy
 * @param {'asc'|'desc'} params.sortDir
 * @param {number} params.page - zero-based page index
 */
export async function fetchCallMonitoring(params) {
  const response = await client.get('/api/call-monitoring', {
    params: {
      search: params.search || undefined,
      startDate: params.startDate || undefined,
      endDate: params.endDate || undefined,
      sentiment: params.sentiment || undefined,
      sortBy: params.sortBy,
      sortDir: params.sortDir,
      page: params.page
    }
  })
  return response.data
}
