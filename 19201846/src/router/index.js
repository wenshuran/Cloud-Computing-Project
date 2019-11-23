import Vue from 'vue'
import Router from 'vue-router'
import main from "../components/main";
import Login from "../components/Login";
import SignUp from "../components/SignUp";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      name: 'login',
      component: Login
    },
    {
      path: '/signUp',
      name: 'signUp',
      component: SignUp
    },
    {
      path: '/event_organiser',
      name: 'main',
      component: main
    }
  ]
})
