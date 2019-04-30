<template>
  <div>
    <form @submit.prevent="addNewMeeting()" v-if="adding">
      <h3>Dodaj nowe spotkanie</h3>
      <label>Nazwa</label>
      <input type="text" v-model="newMeeting.name">
      <label>Opis</label>
      <textarea v-model="newMeeting.description"></textarea>
      <label>Data</label>
      <input type="date" v-model="newMeeting.date" id="date-picker">
      <button>Dodaj</button>
      <span class="error" v-if="error">Spotkanie musi mieć nazwę!</span>
    </form>
    <button @click="adding = true" v-else>Dodaj nowe spotkanie</button>
  </div>
</template>

<script>
    export default {
        data() {
            return {
                newMeeting: {participants: []},
                adding: false,
                error: false
            };
        },
        methods: {
            addNewMeeting() {
                this.error = false;
                if (this.newMeeting.name) {
                    this.$emit('added', this.newMeeting);
                    this.newMeeting = {participants: []};
                    this.adding = false;
                } else {
                    this.error = true;
                }
            }
        }
    }

    function getTodaysDate() {
      let date = new Date();
      return date.getFullYear() + '-' + (date.getMonth() < 10 ? '0' : '') + date.getMonth() + '-' + (date.getDay() < 10 ? '0' : '') + date.getDay();
    }

    // Doesn't work :/
    document.addEventListener('DOMElementsLoaded', function() {
      document.getElementById('date-picker').value = getTodaysDate();
    });
</script>

<style scoped>
  .error {
    color: #F00;
  }

  #date-picker {
    display: block;
    background-color: transparent;
    border: 0.1rem solid #d1d1d1;
    border-radius: .4rem;
    box-shadow: none;
    box-sizing: inherit;
    height: 3.8rem;
    padding: .6rem 1.0rem;
    width: 100%;
  }
</style>
