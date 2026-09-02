import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useCallMonitoring } from '../../src/composables/useCallMonitoring'
import { fetchCallMonitoring } from '../../src/services/callMonitoringApi'
import type { CallMonitoringRecord, PageResponse } from '../../src/types/callMonitoring'

vi.mock('../../src/services/callMonitoringApi', () => ({
  fetchCallMonitoring: vi.fn()
}))

const mockedFetch = vi.mocked(fetchCallMonitoring)

function mockPage(
  content: CallMonitoringRecord[],
  overrides: Partial<PageResponse<CallMonitoringRecord>> = {}
): PageResponse<CallMonitoringRecord> {
  return {
    content,
    page: 0,
    size: 5,
    totalPages: 1,
    totalElements: content.length,
    ...overrides
  }
}

function record(overrides: Partial<CallMonitoringRecord> = {}): CallMonitoringRecord {
  return {
    id: 1,
    callId: 'CALL-0001',
    callTimestamp: '2026-08-01T10:00:00',
    csName: 'Andi',
    customerName: 'Rina',
    sentimentScore: 80,
    ...overrides
  }
}

describe('useCallMonitoring', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads records on demand and exposes them as state', async () => {
    mockedFetch.mockResolvedValueOnce(mockPage([record()]))

    const { records, load } = useCallMonitoring()
    await load()

    expect(mockedFetch).toHaveBeenCalledTimes(1)
    expect(records.value).toHaveLength(1)
    expect(records.value[0].callId).toBe('CALL-0001')
  })

  it('resets to the first page when the sentiment filter changes', async () => {
    mockedFetch.mockResolvedValue(mockPage([]))

    const { page, setSentiment } = useCallMonitoring()
    page.value = 2

    setSentiment('BELOW_70')
    // setSentiment triggers an async load internally
    await Promise.resolve()
    await Promise.resolve()

    expect(page.value).toBe(0)
    expect(mockedFetch).toHaveBeenLastCalledWith(
      expect.objectContaining({ sentiment: 'BELOW_70', page: 0 })
    )
  })

  it('toggles sort direction when the same column is clicked twice', async () => {
    mockedFetch.mockResolvedValue(mockPage([]))

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
    mockedFetch.mockRejectedValueOnce(new Error('network error'))

    const { error, records, load } = useCallMonitoring()
    await load()

    expect(error.value).toBeTruthy()
    expect(records.value).toHaveLength(0)
  })
})
