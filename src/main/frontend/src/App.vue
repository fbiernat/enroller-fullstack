<template>
  <div id="container" v-cloak>
    <div id="app">
      <notifications group="main-notifications" position="top center" classes="my-notification" width="400px"/>
      <h1 id="app-title">
        <img src="./assets/logo.svg" alt="Enroller" class="logo">
        System do zapisów na zajęcia
      </h1>
      <div v-if="!authenticatedUsername">
        <button class="button" :class="{ 'button-outline': registering}" @click="registering = false">Loguję się</button>
        <button class="button" :class="{ 'button-outline': !registering}" @click="registering = true">Rejestruję się</button>
        <div>
        <login-form @login="registering ? register($event) : login($event)" :button-label="loginButtonLabel"></login-form>
      </div>
      </div>
      <div v-else>
        <h2>
          Witaj {{ authenticatedUsername }}!
          <a @click="logout()" class="float-right button-outline button">Wyloguj</a>
        </h2>
        <meetings-page :username="authenticatedUsername"></meetings-page>
      </div>
    </div>

    <footer>
      enroller 2019
    </footer>
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
          registering: false,
          authenticatedUsername: ""
        };
      },
      methods: {
        register(user) {
          if (user.login == "" || user.password == "" || user.login == undefined || user.password == undefined) {
            Utils.notify(this, "error", "Brak danych", "Wprowadź login i hasło użytkownika");
          } else {
            // Register user
            this.$http
              .post("participants", user)
              .then(response => {
                Utils.notify(this, "success", "Zarejestrowano użytkownika", "Pomyślnie zarejestrowano użytkownika <b>" + user.login + "</b>.");
              })
              .catch(response => {
                // Error
                if (response.status == 504) {
                  Utils.notify(this, "warn", "Nie można połączyć z usługą", "Aktualnie usługa jest niedostępna, spróbuj ponownie później");
                } else {
                  Utils.notify(this, "error", "Nie można zarejestrować użytkownika", "Użytkownik <b>" + user.login + "</b> już istnieje.");
                }
              });
          }
        },
        login(user) {
          if (user.login == "" || user.password == "") {
            Utils.notify(this, "error", "Wprowadź dane", "Wypełnij pola login i hasło");
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
              Utils.notify(this, "error", "Błąd", "Zbyt wiele nieudanych prób logowania, spróboj za minutę");
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
                    Utils.notify(this, "error", "Błędne dane", "Błędny login lub hasło" +
                        ((failedLoginCount > 10) ? ", pozostało <b>" +
                        (5 - (failedLoginCount - 10)) + "</b> prób": ""));
                    failedLoginCount++;
                  } else {
                    Utils.notify(this, "warn", "Błąd", "Ups, coś poszło nie tak");
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
      },
      computed: {
        loginButtonLabel: function() {
          return this.registering ? 'Zarejestruj się' : 'Zaloguj się';
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

body, html {
  margin: 0;
  height: 100vh;
}

#container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

#app {
  max-width: 1000px;
  width: 100%;
  min-height: calc(100vh - 90px);
  margin: 0 auto 20px;
  flex: 1 0 auto;
}

#app-title {
  margin-top: 25px;
}

footer {
  flex-shrink: 0;
  height: 30px;
  text-align: center;
  font-size: 9pt;
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

  opacity: 0.95;
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

