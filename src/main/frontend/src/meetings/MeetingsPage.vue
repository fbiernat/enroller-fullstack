<template>
  <div>
    <new-meeting-form @added="addNewMeeting($event)"></new-meeting-form>

    <span v-if="meetings.length == 0">
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
                // this.meetings.push(meeting);
                this.$http.post('meetings', meeting)
                .then(response => {
                    Utils.notify(this, 'success', 'Dodano zajęcia', 'Pomyślnie dodano zajęcia');
                    this.loadMeetings();
                })
                .catch(response => {
                    Utils.notify(this, 'error', 'Bład dodawania zajęć', 'Wystąpił błąd w trakcie dodawania zajęć');
                });
            },
            addMeetingParticipant(meeting) {
                let url = 'meetings/' + meeting.id;
                this.$http.post(url, this.username)
                .then(response => {
                    Utils.notify(this, 'success', 'Zapisano na zajęcia', 'Zapis na zajęcia przebiegł pomyślnie');
                    this.loadMeetings();
                })
                .catch(response => {
                    Utils.notify(this, 'error', 'Błąd podczas zapisu na zajęcia', 'Nie udało się dopisać Cię do zajęć, spróbój później');
                });
            },
            removeMeetingParticipant(meeting) {
                meeting.participants.splice(meeting.participants.indexOf(this.username), 1);
            },
            deleteMeeting(meeting) {
                this.meetings.splice(this.meetings.indexOf(meeting), 1);
            }
        },
        mounted() {
            this.loadMeetings();
        }
    }
</script>
