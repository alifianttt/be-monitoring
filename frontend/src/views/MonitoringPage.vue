<script setup lang="ts">
import { onMounted } from 'vue'
import FilterBar from '../components/FilterBar.vue'
import CallMonitoringTable from '../components/CallMonitoringTable.vue'
import { useCallMonitoring } from '../composables/useCallMonitoring'

const {
  records,
  page,
  totalPages,
  totalElements,
  loading,
  error,
  filters,
  sort,
  load,
  setSearch,
  setPeriod,
  setSentiment,
  toggleSort,
  nextPage,
  prevPage
} = useCallMonitoring()

onMounted(load)
</script>

<template>
  <div class="page">
    <h1>Monitoring</h1>
    <div class="card">
      <FilterBar
        :search="filters.search"
        :start-date="filters.startDate"
        :end-date="filters.endDate"
        :sentiment="filters.sentiment"
        @update:search="setSearch"
        @update:period="setPeriod"
        @update:sentiment="setSentiment"
      />

      <CallMonitoringTable
        :records="records"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :loading="loading"
        :error="error"
        :sort-by="sort.sortBy"
        :sort-dir="sort.sortDir"
        @sort="toggleSort"
        @next-page="nextPage"
        @prev-page="prevPage"
      />
    </div>
  </div>
</template>
