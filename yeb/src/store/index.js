import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

export default new Vuex.Store({
    //state可以理解为一个全局的对象，用来保存组件的一些公共数据
    state:{
        routes:[]
    },

    //同步执行，可以改变state里面值的一些方法
    mutations:{
        //初始化路由
        initRoutes(state,data){
            state.routes=data;
        }
    }
})