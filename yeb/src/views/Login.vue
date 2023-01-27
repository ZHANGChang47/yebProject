<template>
    <div>
        <el-form :rules="rules" 
                    v-loading="loading"
                    element-loading-text="正在登录..."
                    element-loading-spinner="el-icon-loading"
                    element-loading-background="rgba(0, 0, 0, 0.8)"
                    ref="loginForm" 
                    :model="loginForm" 
                    class="loginContainer">
            <h3 class="loginTitle">系统登录</h3>
            <el-form-item label="姓名" prop="username">
                <el-input type="text" auto-complete="false" placeholder="请输入姓名" v-model="loginForm.username"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input type="password" auto-complete="false" placeholder="请输入密码" v-model="loginForm.password"></el-input>
            </el-form-item>
            <el-form-item label="验证码" prop="code">
                <br>
                <div class="loginCode">
                    <el-input type="text" auto-complete="false" placeholder="点击图片更新验证码" v-model="loginForm.code" style="width:250px;margin-right:5px"></el-input>
                    <img :src="captchaUrl" @click="updateCaptcha"/>
                </div>
            </el-form-item>
            <el-checkbox v-model="checked" class="loginRemember">记住我</el-checkbox>
            <el-button type="primary" style="width:100%" @click="submitLogin">登录</el-button>
        </el-form>
    </div>
</template>

<script>
// import { postRequest } from '@/utils/api';

export default {
    name: 'Login',
    data(){
        return{
            captchaUrl:'/captcha?time='+new Date(),
            loginForm:{
                username:'admin',
                password:'123',
                code:''             //验证码，可以先空着
            },
            //登录动画，正在加载中是否开启，false为没有开启，需要在登录方法中开启
            loading:false,      
            checked:true,
            //校验规则
            rules:{
                username:[{required:true,message:'请输入用户名',trigger:'blur'}],
                password:[{required:true,message:'请输入用密码',trigger:'blur'}],
                code:[{required:true,message:'请输入验证码',trigger:'blur'}]
            }
        }
    },
    methods:{
        //更新验证码
        updateCaptcha(){
            this.captchaUrl='/captcha?time='+new Date();
        },
        //用户提交表单
        submitLogin(){
            //先通过表单中的ref属性找到对应的表单
            this.$refs.loginForm.validate((valid) => {
                // console.log(valid, 'valid')
                // 如果通过了校验，valid为true，否则为false
                if (valid) {
                    this.loading = true;    //正在加载开启
                    this.postRequest('/login',this.loginForm).then(resp=>{
                        if(resp){
                            // 正在加载关闭
                            this.loading = false;

                            // 存储用户token到session中方便在请求拦截器中获取使用
                            const tokenStr = resp.obj.tokenHead + resp.obj.token;
                            window.sessionStorage.setItem('tokenStr',tokenStr);

                            // 使用this.$router路由跳转有两种方式，使用replace没有后退按钮，使用push有后退按钮
                            // 跳转首页或者重定向到某个路径
                            // 获取重定向路径
                            let path = this.$route.query.redirect;
                            this.$router.replace((path=='/'||path==undefined)?'/home':path);
                        }
                    })
                }else{
                    //使用ElementUI中的弹出信息
                    this.$message.error('请输入所有字段')
                    return false
                }
            });
        }
    }
};
</script>

<style>
.loginContainer{
    border-radius: 15px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 15px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
}
.loginTitle{
    margin: 0px auto 40px auto;
    text-align: center;
}
.loginRemember{
    text-align: left;
    margin: 0px 0px 15px 0px;
}
.loginCode{
    display: flex;
    align-items: center;
}

</style>