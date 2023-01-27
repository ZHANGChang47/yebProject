import axios from 'axios'
//单独引用Message
import { Message } from 'element-ui'
//导入跳转路由，如果没有登录，跳转至登录页面
import router from '../router';


//添加请求拦截器
axios.interceptors.request.use(config=>{
    //如果存在token，请求会携带这个token
    if(window.sessionStorage.getItem('tokenStr')){
        config.headers['Authorization']=window.sessionStorage.getItem('tokenStr');
    }
    return config;
},error=>{
    console.log(error);
})


//添加响应拦截器
axios.interceptors.response.use(success=>{
    //上一行的success表示成功调到接口

    //业务逻辑错误
    // 外层的if是对浏览器给的状态进行判断，内层的if是对后端自定义的状态进行判断
    if(success.status && success.status===200){
        if(success.data.code==500 || success.data.code==401 || success.data.code==403){
            Message.error({message:success.data.message});
            return;
        }
        if(success.data.message){
            Message.success({message:success.data.message});
        }
    }
    return success.data;
},error=>{
    //接口访问失败，例如服务器挂机

    if(error.response.code==504 || error.response==code==404){
        Message.error({message:'服务器运行有问题'});
    }else if(error.response.code==403){
        Message.error({message:'权限不足，请联系管理员！'});
    }else if(error.response.code==401){
        Message.error({message:'尚未登录'});
        router.replace('/');    //跳转至根目录
    }else{
        if(error.response.data.message){
            Message.error({message:error.response.data.message});
        }else{
            Message.error({message:'未知错误'});
        }
    }
    return;
});

//通用请求的地址前缀，可以替换为后端的地址
let base = '';

//传送json格式的post请求
export const postRequest = (url,params) => {
    return axios({
        method:'post',
        url:`${base}${url}`,
        data:params
    })
}

//传送json的Put请求
export const putRequest = (url,params) => {
    return axios({
        method:'put',
        url:`${base}${url}`,
        data:params
    })
}

//传送json的Get请求
export const getRequest = (url,params) => {
    return axios({
        method:'get',
        url:`${base}${url}`,
        data:params
    })
}

//传送json的Delete请求
export const deleteRequest = (url,params) => {
    return axios({
        method:'delete',
        url:`${base}${url}`,
        data:params
    })
}