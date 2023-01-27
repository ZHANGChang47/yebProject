import Vue from 'vue'
import App from './App.vue'
import router from './router'

import store from './store'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

import 'font-awesome/css/font-awesome.css';

import { postRequest } from '@/utils/api';
import { putRequest } from '@/utils/api';
import { getRequest } from '@/utils/api';
import { deleteRequest } from '@/utils/api';
import { initMenu } from './utils/menus';
import { downloadRequest } from './utils/download';

// 关闭浏览器对于环境的提示
Vue.config.productionTip = false

Vue.use(ElementUI,{size:'small'})

Vue.prototype.postRequest = postRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.deleteRequest = deleteRequest;
Vue.prototype.downloadRequest = downloadRequest;

//设置全局的前置路由首位
//to是即将要进入的目标路由对象
//from是要离开的路由对象
//next是用来跳入到to路由的方法，没有next()则无法跳转
router.beforeEach((to,from,next)=>{
  //首先根据能否获取到token去判断是否已经登录，获取不到就去判断是不是从登录页过来
  if(window.sessionStorage.getItem('tokenStr')){
    initMenu(router,store);
    //判断用户信息是否存在，如果不存在则调用后端接口获取用户信息
    if(!window.sessionStorage.getItem('user')){
      return getRequest('/admin/info').then(resp=>{
        if(resp){
          //存入用户信息，只能存放字符串
          window.sessionStorage.setItem('user',JSON.stringify(resp));
          next(to);
        }
      })
    }
    next();
  }else{
    if(to.path=='/'){
      // 如果是登录页，next()直接放行
      next();
    }else{
      next('/?redirect='+to.path);
    }
  }
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
