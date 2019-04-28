<template>
  <div id="app">
    <notifications
      group="main-notifications"
      position="top center"
      classes="my-notification"
      width="400px"
    />
    <h1>
      <img src="./assets/logo.svg" alt="Enroller" class="logo">
      System do zapisów na zajęcia
    </h1>
    <div>
      <button
        class="button"
        v-bind:class="{ 'button-outline': !isLogin}"
        @click="showLoginForm()"
      >Loguję się</button>
      <button
        class="button"
        v-bind:class="{ 'button-outline': isLogin}"
        @click="showRegisterForm()"
      >Rejestruję się</button>
    </div>
    <div v-if="authenticatedUsername">
      <h2>
        Witaj {{ authenticatedUsername }}!
        <a
          @click="logout()"
          class="float-right button-outline button"
        >Wyloguj</a>
      </h2>
      <meetings-page :username="authenticatedUsername"></meetings-page>
    </div>
    <div v-else>
      <login-form v-if="isLogin" @login="login($event)"></login-form>
      <login-form v-else @login="register($event)" button-label="Zarejestruj się"></login-form>
    </div>
  </div>
</template>

<script>
import "milligram";
import LoginForm from "./LoginForm";
import MeetingsPage from "./meetings/MeetingsPage";
import Utils from './utils';


export default {
  components: { LoginForm, MeetingsPage },
  data() {
    return {
      isLogin: true,
      authenticatedUsername: ""
    };
  },
  methods: {
    login(user) {
      if (user.login == '' || user.password == '') {
        Utils.notify(this, 'error', 'Wprowadź dane', 'Wypełnij login i hasło');
      }
      this.authenticatedUsername = user.login;
    },
    logout() {
      this.authenticatedUsername = "";
    },
    register(user) {
      if (
        user.login == "" ||
        user.password == "" ||
        user.login == undefined ||
        user.password == undefined
      ) {
        // notify(this, 'success', 'Test', 'test test');
        // this.$notify({
        //   group: "main-notifications",
        //   title: "Brak danych",
        //   text: "Wprowadź login i hasło użytkownika!",
        //   type: "error",
        //   classes: "notifications"
        // });
        Utils.notify(this, 'error', 'Brak danych', 'Wprowadź login i hasło użytkownika');
      } else {
        // rejestruj uzytkownika
        this.$http
          .post("participants", user)
          .then(response => {
            // this.$notify({
            //   group: "main-notifications",
            //   title: "Zarejestrowano użytkownika",
            //   text:
            //     "Pomyślnie zarejestrowano użytkownika <b>" +
            //     user.login +
            //     "</b>.",
            //   type: "success"
            // });
            Utils.notify(this, 'success', 'Zarejestrowano użytkownika', 'Pomyślnie zarejestrowano użytkownika <b>' + user.login + '</b>.');
          })
          .catch(response => {
            // nie udało się
            console.log(response);
            if (response.status == 504) {
              // server down
              // this.$notify({
              //   group: "main-notifications",
              //   title: "Nie można połączyć z usługą",
              //   text: "Aktualnie usługa jest niedostępna, spróbuj później.",
              //   type: "warn"
              // });
              Utils.notify(this, 'warn', 'Nie można połączyć z usługą', 'Aktualnie usługa jest niedostępna, spróbuj ponownie później');
            } else {
              // this.$notify({
              //   group: "main-notifications",
              //   title: "Nie można zarejestrować użytkownika",
              //   text: "Użytkownik <b>" + user.login + "</b> już istnieje.",
              //   type: "error"
              // });
              Utils.notify(this, 'error', 'Nie można zarejestrować użytkownika', 'Użytkownik <b>' + user.login + '</b> już istnieje.');
            }
          });
      }
    },
    showLoginForm() {
      this.isLogin = true;
    },
    showRegisterForm() {
      this.isLogin = false;
    }
  }
};
</script>

<style>
#app {
  max-width: 1000px;
  margin: 30px auto;
}

.logo {
  vertical-align: middle;
}

.my-notification {
  padding: 10px;
  margin: 5px 5px 0;

  font-size: 12px;

  color: #ffffff;
  background: #44a4fc;
  border-bottom: 5px solid #187fe7;
}

.my-notification.warn {
  background: #ffb648;
  border-bottom-color: #f48a06;
}

.my-notification.error {
  background: #e54d42;
  border-bottom-color: #b82e24;
}

.my-notification.success {
  background: #68cd86;
  border-bottom-color: #42a85f;
}
</style>

