<template>
    <div>
        <div style="display:flex;justify-content:center;margin-top: 10px;">
            <el-input placeholder="通过用户名搜索用户" prefix-icon="el-icon-search" style="width:400px;margin-right: 10px;"
                v-model="keywords"></el-input>
            <el-button type="primary" icon="el-icon-search" @click="doSearch">搜索</el-button>
        </div>
        <div class="admin-container">
            <el-card class="admin-card" v-for="(admin, index) in admins" :key="index">
                <div slot="header" class="clearfix">
                    <span>{{ admin.name }}</span>
                    <el-button style="float: right; padding: 3px 0;color: red;" type="text" icon="el-icon-delete"
                        @click="deleteAdmin(admin)"></el-button>
                </div>
                <div>
                    <div class="img-container">
                        <img :src="admin.userFace" :alt="admin.name" :title="admin.name" class="userFace-img" />
                    </div>
                </div>
                <div class="userinfo-container">
                    <div>用户名：{{ admin.name }}</div>
                    <div>手机号码：{{ admin.phone }}</div>
                    <div>电话号码：{{ admin.telephone }}</div>
                    <div>地址：{{ admin.address }}</div>
                    <div>用户状态：
                        <el-switch v-model="admin.enabled" active-color="#13ce66" inactive-color="#ff4949"
                            @change="enabledChange(admin)" active-text="启用" inactive-text="紧用" style="margin-left:6px">
                        </el-switch>
                    </div>
                </div>
                <div>
                    用户角色：
                    <el-tag style="margin-right: 4px;" type="success" v-for="(role, index) in admin.roles" :key="index">
                        {{ role.nameZh }}
                    </el-tag>
                    <el-popover placement="right" title="角色列表" width="200" trigger="click" @show="showPop(admin)" @hide="hidePop(admin)">
                        <el-select v-model="selectedRoles" placeholder="请选择" multiple>
                            <el-option v-for="(r,index) in allRoles" 
                                        :key="index" 
                                        :label="r.nameZh" 
                                        :value="r.id">
                            </el-option>
                        </el-select>
                        <el-button type="text" slot="reference" icon="el-icon-more"></el-button>
                    </el-popover>
                </div>
                <div>
                    备注：{{ admin.remark }}
                </div>
            </el-card>
        </div>
    </div>
</template>

<script>
export default {
    name: 'SysAdmin',
    data() {
        return {
            //查询出来的操作员
            admins: [],
            //搜索关键词
            keywords: '',
            //所有可选角色
            allRoles:[],
            //角色弹出框中选中的数组
            selectedRoles:[],
        }
    },
    mounted() {
        this.initAdmins();
    },
    methods: {
        //弹出框隐藏方法
        hidePop(admin){
            //获得角色对象列表
            let roles=[];
            Object.assign(roles,admin.roles);

            //根据长度是否相同，确定数据是否发生变化，进入决定是否要执行接下来的操作
            //如果个数相同，进一步通过双重循环判断内容是否发生变化
            let flag=false;
            if(roles.length != this.selectedRoles.length){
                flag=true
            }else{
                for(let i=0;i<roles.length;i++){
                    let role = roles[i];
                    for(let j=0;j<this.selectedRoles.length;j++){
                        let sr=this.selectedRoles[j];
                        if(role.id == sr){
                            roles.splice(i,1);
                            i--
                            break;
                        }
                    }
                }
                if(roles.length!=0){
                    flag=true;
                }
            }

            if(flag){
                //拼接url
                let url='/system/admin/role?adminId='+admin.id;
                this.selectedRoles.forEach(sr=>{
                    url += '&rids=' + sr;
                })
                this.putRequest(url).then(resp=>{
                    if(resp){
                        this.initAdmins();
                    }
                })
            }
        },
        //弹出框点击方法
        showPop(admin){
            this.initAllRoles();
            let roles = admin.roles;
            this.selectedRoles = [];
            roles.forEach(r=>{
                this.selectedRoles.push(r.id);
            });
        },
        //初始化获取所有角色
        initAllRoles(){
            this.getRequest('/system/admin/roles').then(resp=>{
                this.allRoles=resp;
            })
        },
        //修改启用状态
        enabledChange(admin) {
            this.putRequest('/system/admin/', admin).then(resp => {
                if (resp) {
                    this.initAdmins();
                }
            })
        },
        //删除操作员
        deleteAdmin(admin) {
            this.$confirm('此操作将删除操作员[' + admin.name + '], 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteRequest('/system/admin/' + admin.id).then(resp => {
                    if (resp) {
                        this.initAdmins();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //根据关键词查询操作员
        doSearch() {
            this.initAdmins();
        },
        //初始化查询操作员
        initAdmins() {
            this.getRequest('/system/admin/?keywords=' + this.keywords).then(resp => {
                if (resp) {
                    this.admins = resp;
                }
            })
        }
    }
};
</script>

<style scoped>
.admin-container {
    margin-top: 10px;
    display: flex;
    justify-content: space-around;
    /* 自动换行 */
    flex-wrap: wrap;
}

.admin-card {
    width: 350px;
    margin-bottom: 20px;
}

.userFace-img {
    width: 72px;
    height: 72px;
    border-radius: 72px;
}

.img-container {
    width: 100%;
    display: flex;
    justify-content: center;
}

.userinfo-container {
    font-size: 14px;
    color: #409eff;
}
</style>