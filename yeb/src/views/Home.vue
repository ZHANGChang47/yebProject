<template>
    <div>
        <el-container>
            <el-header class="homeHeader">
                <div class="title">
                    云E办
                </div>
                <el-dropdown class="userInfo" @command="commandHandler">
                    <span class="el-dropdown-link">
                        {{ user.name }}<i><img :src="user.userFace" /></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="userinfo">个人中心</el-dropdown-item>
                        <el-dropdown-item command="setting">设置</el-dropdown-item>
                        <el-dropdown-item command="logout">注销登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </el-header>
            <el-container>
                <el-aside width="200px">
                    <el-menu router unique-opened>
                        <el-submenu :index="index + ''" v-for="(item, index) in routes" :key="index"
                            v-if="!item.hidden">
                            <template slot="title">
                                <i :class="item.iconCls" style="color:#1accff;margin-right:5px"></i>
                                <span>{{ item.name }}</span>
                            </template>
                            <el-menu-item :index="children.path" v-for="(children, index) in item.children"
                                :key="index">
                                {{ children.name }}
                            </el-menu-item>
                        </el-submenu>
                    </el-menu>
                </el-aside>
                <el-main>
                    <el-breadcrumb separator-class="el-icon-arrow-right" v-if="this.$router.currentRoute.path!='/home'">
                        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>{{ this.$router.currentRoute.name }}</el-breadcrumb-item>
                    </el-breadcrumb>
                    <div class="homeWelcome" v-if="this.$router.currentRoute.path=='/home'">
                        欢迎来到云E办系统！
                    </div>
                    <router-view class="homeRouterView"/>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>

export default {
    name: "Home",
    data() {
        return {
            //从session中获取到用户信息，然后去填写用户头像等信息
            user: JSON.parse(window.sessionStorage.getItem('user'))
        }
    },
    computed: {
        // 使用计算属性从vuex中获取数据
        routes() {
            return this.$store.state.routes;
        }
    },
    methods: {
        commandHandler(command) {
            if (command == 'logout') {
                //this.$confirm提供一个弹出框，用来确定是否退出登录
                this.$confirm('此操作将注销登录, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    //注销登录
                    this.postRequest('/logout');
                    //移除session中的user用户信息以及Token
                    window.sessionStorage.removeItem('user');
                    window.sessionStorage.removeItem('tokenStr');
                    //清除vuex中存放的菜单数据
                    this.$store.commit('initRoutes', []);
                    //页面跳转至登录页面
                    this.$router.replace('/');
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消操作'
                    });
                });
            }
        }
    }
}

</script>

<style>
.homeHeader {
    background: #409eff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 15px;
    box-sizing: border-box;
}

.homeHeader .title {
    font-size: 30px;
    font-family: 华文楷体;
    color: white;
}

.homeHeader .userInfo {
    cursor: pointer;
}

.el-dropdown-link {
    display: flex;
    align-items: center;
    justify-content: center;
}

.el-dropdown-link img {
    width: 48px;
    height: 48px;
    border-radius: 24px;
    margin-left: 8px;
}

.homeRouterView{
    margin-top: 10px;
}

.homeWelcome{
    text-align: center;
    font-size: 30px;
    font-family: 华文楷体;
    color: #409eff;
    padding-top: 50px;
}
</style>