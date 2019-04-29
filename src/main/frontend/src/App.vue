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
    <div v-if="!authenticatedUsername">
      <button
        class="button"
        v-bind:class="{ 'button-outline': !isNotLoggedIn}"
        @click="showLoginForm()"
      >Loguję się</button>
      <button
        class="button"
        v-bind:class="{ 'button-outline': isNotLoggedIn}"
        @click="showRegisterForm()"
      >Rejestruję się</button>
      <div>
      <login-form v-if="isNotLoggedIn" @login="login($event)"></login-form>
      <login-form v-else @login="register($event)" button-label="Zarejestruj się"></login-form>
    </div>
    </div>
    <div v-else>
      <h2>
        Witaj {{ authenticatedUsername }}!
        <a
          @click="logout()"
          class="float-right button-outline button"
        >Wyloguj</a>
      </h2>
      <meetings-page :username="authenticatedUsername"></meetings-page>
    </div>
    
  </div>
</template>

<script>
import "milligram";
import LoginForm from "./LoginForm";
import MeetingsPage from "./meetings/MeetingsPage";
import Vue from "vue";
import Utils from "./utils";

let failedLoginCount = 0;
let timeoutHandle;

export default {
  components: { LoginForm, MeetingsPage },
  data() {
    return {
      isNotLoggedIn: true,
      authenticatedUsername: ""
    };
  },
  methods: {
    register(user) {
      if (
        user.login == "" ||
        user.password == "" ||
        user.login == undefined ||
        user.password == undefined
      ) {
        Utils.notify(
          this,
          "error",
          "Brak danych",
          "Wprowadź login i hasło użytkownika"
        );
      } else {
        // Register user
        this.$http
          .post("participants", user)
          .then(response => {
            Utils.notify(
              this,
              "success",
              "Zarejestrowano użytkownika",
              "Pomyślnie zarejestrowano użytkownika <b>" + user.login + "</b>."
            );
          })
          .catch(response => {
            // Error
            if (response.status == 504) {
              Utils.notify(
                this,
                "warn",
                "Nie można połączyć z usługą",
                "Aktualnie usługa jest niedostępna, spróbuj ponownie później"
              );
            } else {
              Utils.notify(
                this,
                "error",
                "Nie można zarejestrować użytkownika",
                "Użytkownik <b>" + user.login + "</b> już istnieje."
              );
            }
          });
      }
    },
    login(user) {
      if (user.login == "" || user.password == "") {
        Utils.notify(
          this,
          "error",
          "Wprowadź dane",
          "Wypełnij pola login i hasło"
        );
      } else {
        if (failedLoginCount > 15) {
          if (!timeoutHandle) {
            timeoutHandle = setTimeout(() => {
              // alert('Login available again');
              failedLoginCount = 0;
              clearTimeout(timeoutHandle);
              timeoutHandle = undefined;
            }, 60000);
          }
          Utils.notify(
            this,
            "error",
            "Błąd",
            "Zbyt wiele nieudanych prób logowania, spróboj za minutę"
          );
        } else {
          this.$http
            .post("tokens", user)
            .then(response => {
              const token = response.body.token;
              this.storeAuth(user.login, token);
              
              failedLoginCount = 0;
            })
            .catch(response => {
              if (response.status == 401) {
                Utils.notify(
                  this,
                  "error",
                  "Błędne dane",
                  "Błędny login lub hasło" +
                    ((failedLoginCount > 10) ? ", pozostało <b>" +
                    (5 - (failedLoginCount - 10)) + "</b> prób": "")
                );
                failedLoginCount++;
              } else {
                Utils.notify(this, "error", "Błąd", "Ups, coś poszło nie tak");
              }
            });
        }
      }
    },
    storeAuth(username, token) {
      this.authenticatedUsername = username;
      Vue.http.headers.common.Authorization = 'Bearer ' + token;
      localStorage.setItem('username', username);
      localStorage.setItem('token', token);
    },
    logout() {
      delete Vue.http.headers.Authorization;
      localStorage.removeItem('username');
      this.authenticatedUsername = "";
    },
    showLoginForm() {
      this.isNotLoggedIn = true;
    },
    showRegisterForm() {
      this.isNotLoggedIn = false;
    }
  },
  mounted() {
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    if (username && token) {
      this.storeAuth(username, token);
      // if token expired or user has been deleted - logout
      this.$http.get(`participants/${username}`).catch(() => this.logout());
    }
  }
};

setInterval(() => {
  // clear failed login count every 3 minutes
  if (!timeoutHandle)
    failedLoginCount = 0;
}, 180000);
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

  font-size: 14px;

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

