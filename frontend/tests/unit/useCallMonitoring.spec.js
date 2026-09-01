import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useCallMonitoring } from '../../src/composables/useCallMonitoring'
import { fetchCallMonitoring } from '../../src/services/callMonitoringApi'

vi.mock('../../src/services/callMonitoringApi', () => ({
  fetchCallMonitoring: vi.fn()
}))

function mockPage(content, overrides = {}) {
  return {
    content,
    page: 0,
    totalPages: 1,
    totalElements: content.length,
    ...overrides
  }
}

describe('useCallMonitoring', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads records on demand and exposes them as state', async () => {
    fetchCallMonitoring.mockResolvedValueOnce(
      mockPage([{ id: 1, callId: 'CALL-0001', sentimentScore: 80 }])
    )

    const { records, load } = useCallMonitoring()
    await load()

    expect(fetchCallMonitoring).toHaveBeenCalledTimes(1)
    expect(records.value).toHaveLength(1)
    expect(records.value[0].callId).toBe('CALL-0001')
  })

  it('resets to the first page when the sentiment filter changes', async () => {
    fetchCallMonitoring.mockResolvedValue(mockPage([]))

    const { page, setSentiment } = useCallMonitoring()
    page.value = 2

    setSentiment('BELOW_70')
    // setSentiment triggers an async load internally
    await Promise.resolve()
    await Promise.resolve()

    expect(page.value).toBe(0)
    expect(fetchCallMonitoring).toHaveBeenLastCalledWith(
      expect.objectContaining({ sentiment: 'BELOW_70', page: 0 })
    )
  })

  it('toggles sort direction when the same column is clicked twice', async () => {
    fetchCallMonitoring.mockResolvedValue(mockPage([]))

    const { sort, toggleSort } = useCallMonitoring()

    toggleSort('sentimentScore')
    await Promise.resolve()
    expect(sort.sortBy).toBe('sentimentScore')
    expect(sort.sortDir).toBe('asc')

    toggleSort('sentimentScore')
    await Promise.resolve()
    expect(sort.sortDir).toBe('desc')
  })

  it('sets an error message and clears records when the API call fails', async () => {
    fetchCallMonitoring.mockRejectedValueOnce(new Error('network error'))

    const { error, records, load } = useCallMonitoring()
    await load()

    expect(error.value).toBeTruthy()
    expect(records.value).toHaveLength(0)
  })
})
