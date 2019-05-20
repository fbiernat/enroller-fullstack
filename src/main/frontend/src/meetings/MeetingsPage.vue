<template>
  <div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length === 0">
               Brak zaplanowanych spotkań.
           </span>
    <h3 v-else>
      Zaplanowane zajęcia ({{ meetings.length }})
    </h3>

    <meetings-list :meetings="meetings"
                   :username="username"
                   @attend="addMeetingParticipant($event)"
                   @unattend="removeMeetingParticipant($event)"
                   @delete="deleteMeeting($event)"></meetings-list>
  </div>
</template>

<script>
    import NewMeetingForm from "./NewMeetingForm";
    import MeetingsList from "./MeetingsList";
    import Utils from "./../utils";

    export default {
        components: {NewMeetingForm, MeetingsList},
        props: ['username'],
        data() {
            return {
                meetings: []
            };
        },
        methods: {
            loadMeetings() {
                this.$http.get('meetings').then(response => {
                    this.meetings = response.data;
                })
                .catch(response => {
                    Utils.notify(this, 'error', 'Błąd', 'Nie udało się załadować listy zajęc');
                });
            },
            addNewMeeting(meeting) {
                this.$http.post('meetings', meeting)
                .then(response => {
                    this.meetings.push(response.data);
                    Utils.notify(this, 'success', 'Dodano zajęcia', 'Pomyślnie dodano zajęcia');
                })
                .catch(response => {
                    Utils.notify(this, 'error', 'Bład dodawania zajęć', 'Wystąpił błąd w trakcie dodawania zajęć');
                });
            },
            addMeetingParticipant(meeting) {
                let url = 'meetings/' + meeting.id + '/participants';
                this.$http.post(url)
                .then(response => {
                    this.meetings.find(m => m.id === meeting.id).participants = response.data.participants;
                    Utils.notify(this, 'success', 'Zapisano na zajęcia', 'Zapis na zajęcia przebiegł pomyślnie');
                })
                .catch(response => {
                    Utils.notify(this, 'error', 'Błąd podczas zapisu na zajęcia', 'Nie udało się dopisać Cię do zajęć, spróbuj później');
                });
            },
            removeMeetingParticipant(meeting) {
                let url = 'meetings/' + meeting.id + '/participants/' + this.username;
                this.$http.delete(url)
                .then(response => {
                    // Modify state with response object, instead of new request for all meetings
                    this.meetings.find(m => m.id === meeting.id).participants = response.data.participants;
                    Utils.notify(this, 'success', 'Wypisano z zajęć', 'Pomyślnie wypisano z zajęć');
                })
                .catch(response => {
                    console.log(response);
                    Utils.notify(this, 'error', 'Błąd podczas wypisywania z zajęć', 'Nieudana próba wypisania z zajęc');
                });
            },
            deleteMeeting(meeting) {
                let url = 'meetings/' + meeting.id;
                this.$http.delete(url)
                .then(response => {
                    this.meetings = this.meetings.filter(m => m.id !== meeting.id);
                    Utils.notify(this, 'success', 'Usunięto zajęcia', 'Pomyślnie usunięto zajęcia');
                })
                .catch(response => {
                    console.log(response);
                    Utils.notify(this, 'error', 'Błąd', 'Błąd podczas usuwania zajęć');
                });
            }
        },
        mounted() {
            this.loadMeetings();
        }
    }
</script>
