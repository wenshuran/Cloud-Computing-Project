// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuex from 'vuex'
import App from './App'
import router from './router'
import axios from 'axios'
import ElementUI from 'element-ui';
import locale from 'element-ui/lib/locale/lang/en'
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false;
Vue.prototype.$http = axios;
Vue.config.productionTip = false;
Vue.use(ElementUI, { locale });
Vue.use(Vuex);

export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';

var store = new Vuex.Store({
  state:{
    username:'',
  },
  mutations:{
    [LOGIN](state, username){
      state.username = username;
      sessionStorage.setItem("username", username);
    },
    [LOGOUT](state){
      state.username = '';
    },
  },
  getters:{
    username:state => {
      return state.username;
    }
  }
});

export default store;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  store,
  router,
  components: { App },
  template: '<App/>'
})

