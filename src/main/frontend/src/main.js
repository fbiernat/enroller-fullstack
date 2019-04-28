import Vue from 'vue';
import App from './App.vue';
import VueResource from "vue-resource";
import Notifications from 'vue-notification';

Vue.use(VueResource);
Vue.use(Notifications);
Vue.http.options.root = '/api';

new Vue({
    el: '#app',
    render: h => h(App)
});