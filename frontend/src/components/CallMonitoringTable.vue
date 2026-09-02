<script setup lang="ts">
import type { CallMonitoringRecord, SortDirection, SortableColumn } from '../types/callMonitoring'

const props = defineProps<{
  records: CallMonitoringRecord[]
  page: number
  totalPages: number
  totalElements: number
  loading: boolean
  error: string | null
  sortBy: SortableColumn
  sortDir: SortDirection
}>()

const emit = defineEmits<{
  sort: [column: SortableColumn]
  'next-page': []
  'prev-page': []
}>()

const columns: Array<{ key: SortableColumn; label: string }> = [
  { key: 'callId', label: 'Call ID' },
  { key: 'callTimestamp', label: 'Call Timestamp' },
  { key: 'csName', label: 'CS Name' },
  { key: 'customerName', label: 'Nama Nasabah' },
  { key: 'sentimentScore', label: 'Sentiment Score Nasabah' }
]

function sortIcon(column: SortableColumn): string {
  if (props.sortBy !== column) return ''
  return props.sortDir === 'asc' ? '▲' : '▼'
}

function formatTimestamp(value: string): string {
  return new Date(value).toLocaleString('id-ID', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function scoreClass(score: number): 'low' | 'high' {
  return score < 70 ? 'low' : 'high'
}
</script>

<template>
  <div>
    <table>
      <thead>
        <tr>
          <th>No.</th>
          <th
            v-for="col in columns"
            :key="col.key"
            @click="emit('sort', col.key)"
          >
            {{ col.label }}
            <span class="sort-icon">{{ sortIcon(col.key) }}</span>
          </th>
        </tr>
      </thead>
      <tbody v-if="!loading && !error && records.length">
        <tr v-for="(record, index) in records" :key="record.id">
          <td>{{ page * 5 + index + 1 }}</td>
          <td>{{ record.callId }}</td>
          <td>{{ formatTimestamp(record.callTimestamp) }}</td>
          <td>{{ record.csName }}</td>
          <td>{{ record.customerName }}</td>
          <td>
            <span class="score-pill" :class="scoreClass(record.sentimentScore)">
              {{ record.sentimentScore }}%
            </span>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="error" class="error-state">{{ error }}</div>
    <div v-else-if="!records.length" class="empty-state">
      No matching calls found. Try adjusting your search or filters.
    </div>

    <div class="pagination" v-if="!loading && !error">
      <span>Page {{ totalElements === 0 ? 0 : page + 1 }} of {{ totalPages }}</span>
      <button :disabled="page === 0" @click="emit('prev-page')">Previous</button>
      <button :disabled="page + 1 >= totalPages" @click="emit('next-page')">Next</button>
    </div>
  </div>
</template>
