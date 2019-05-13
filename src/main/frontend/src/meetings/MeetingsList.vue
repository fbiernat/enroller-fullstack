<template>
  <table v-if="meetings.length > 0">
    <thead>
    <tr>
      <th>Nazwa spotkania</th>
      <th>Data</th>
      <th>Opis</th>
      <th>Uczestnicy</th>
      <td></td>
    </tr>
    </thead>
    <tbody>
    <tr v-for="meeting in meetings" :key="meeting.id">
      <td>{{ meeting.name }}</td>
      <td style="min-width: 120px;">
        <span v-if="meeting.date">{{meeting.date}}</span>
        <span v-else>Brak</span>
      </td>
      <td>{{ meeting.description }}</td>
      <td>
        <ul v-if="meeting.participants" class="participant-list">
          <li v-for="participant in meeting.participants" :key="participant.login">
            {{ participant.login }}
          </li>
        </ul>
      </td>
      <td class="controls">
        <button v-if="meeting.participants.find(o => o.login === username) == null" class="button-outline"
                @click="$emit('attend', meeting)">
          Zapisz się
        </button>
        <button v-else class="button-outline" @click="$emit('unattend', meeting)">Wypisz się</button>
        <button v-if="meeting.participants.length === 0" class="button" @click="$emit('delete', meeting)">
          Usuń puste spotkanie
        </button>
      </td>
    </tr>
    </tbody>
  </table>
</template>

<script>
    export default {
        props: ['meetings', 'username']
    }
</script>

<style>
  .controls {
    text-align: right;
    min-width: 400px;
  }

  td > * {
    vertical-align: middle;
  }

.participant-list {
  margin: 0;
}

  .participant-list li {
    margin: 0;
  }
</style>
