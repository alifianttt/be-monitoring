<script setup>
import { computed } from 'vue'

const props = defineProps({
  search: { type: String, default: '' },
  startDate: { type: String, default: '' },
  endDate: { type: String, default: '' },
  sentiment: { type: String, default: '' }
})

const emit = defineEmits(['update:search', 'update:period', 'update:sentiment'])

// Story requirement: selectable period is limited to the latest three months.
const today = new Date()
const maxDate = today.toISOString().slice(0, 10)
const minDate = computed(() => {
  const d = new Date(today)
  d.setMonth(d.getMonth() - 3)
  return d.toISOString().slice(0, 10)
})

function onSearchInput(event) {
  emit('update:search', event.target.value)
}

function onStartDateChange(event) {
  emit('update:period', event.target.value, props.endDate)
}

function onEndDateChange(event) {
  emit('update:period', props.startDate, event.target.value)
}

function onSentimentChange(event) {
  emit('update:sentiment', event.target.value)
}
</script>

<template>
  <div class="filter-bar">
    <div class="filter-field">
      <label for="search">Search</label>
      <input
        id="search"
        type="text"
        placeholder="Search Call ID, CS, customer..."
        :value="search"
        @input="onSearchInput"
      />
    </div>

    <div class="filter-field">
      <label for="start-date">Start Period</label>
      <input
        id="start-date"
        type="date"
        :min="minDate"
        :max="maxDate"
        :value="startDate"
        @change="onStartDateChange"
      />
    </div>

    <div class="filter-field">
      <label for="end-date">End Period</label>
      <input
        id="end-date"
        type="date"
        :min="minDate"
        :max="maxDate"
        :value="endDate"
        @change="onEndDateChange"
      />
    </div>

    <div class="filter-field">
      <label for="sentiment">Sentiment</label>
      <select id="sentiment" :value="sentiment" @change="onSentimentChange">
        <option value="">All</option>
        <option value="BELOW_70">Di bawah 70%</option>
        <option value="AT_OR_ABOVE_70">70% atau lebih</option>
      </select>
    </div>
  </div>
</template>
