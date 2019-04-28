<template>
  <div id="app">
    <h1>
      <img src="./assets/logo.svg" alt="Enroller" class="logo">
      System do zapisów na zajęcia
    </h1>
    <div>
      <button class="button" v-bind:class="{ 'button-outline': !isLogin}" @click="showLoginForm()">Loguję się</button>
      <button class="button" v-bind:class="{ 'button-outline': isLogin}" @click="showRegisterForm()">Rejestruję się</button>
    </div>
    <div v-if="authenticatedUsername">
      <h2>Witaj {{ authenticatedUsername }}!
        <a @click="logout()" class="float-right  button-outline button">Wyloguj</a>
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
 
    export default {
        components: {LoginForm, MeetingsPage},
        data() {
            return {
                isLogin: true,
                authenticatedUsername: ""
            };
        },
        methods: {
            login(user) {
                this.authenticatedUsername = user.login;
            },
            logout() {
                this.authenticatedUsername = '';
            },
            register(user) {
              if(user.login != '' && user.password != '')
                // rejestruj uzytkownika
                this.$http.post('participants', user)
                  .then(response => {
                    // udało się
                    alert('Pomyślnie utworzono użytkownika!');
                  })
                  .catch(response => {
                    // nie udało się
                    alert('Ten użytkownik już istnieje');
                  })
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
    margin: 0 auto;
  }

  .logo {
    vertical-align: middle;
  }
</style>

