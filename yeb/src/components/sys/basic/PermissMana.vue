<template>
    <div>
        <div class="permissManaTool">
            <el-input size="small" placeholder="请输入角色英文名" v-model="role.name">
                <template slot="prepend">ROLE_</template>
            </el-input>
            <el-input size="small" placeholder="请输入角色中文名" v-model="role.nameZh" @keydown.enter.native="doAddRole">
            </el-input>
            <el-button size="small" type="primary" icon="el-icon-plus" @click="doAddRole">添加角色</el-button>
        </div>
        <div class="permissManaMain">
            <el-collapse v-model="activeName" accordion @change="change">
                <el-collapse-item :title="r.nameZh" :name="r.id" v-for="(r, index) in roles" :key="index">
                    <el-card class="box-card">
                        <div slot="header" class="clearfix">
                            <span>可访问资源</span>
                            <el-button style="float: right; padding: 3px 0;color: #ff0000;" type="text" icon="el-icon-delete" @click="doDeleteRole(r)"></el-button>
                        </div>
                        <div>
                            <el-tree :data="allMenus" 
                                     :props="defaultProps" 
                                     show-checkbox
                                     ref="tree"
                                     node-key="id"
                                     :key="index"
                                     :default-checked-keys="selectedMenus">
                            </el-tree>
                            <div style="display:flex;justify-content:flex-end">
                                <el-button size="mini" @click="cancelUpdate">取消修改</el-button>
                                <el-button size="mini" type="primary" @click="doUpdate(r.id,index)">确认修改</el-button>
                            </div>
                        </div>
                    </el-card>
                </el-collapse-item>
            </el-collapse>
        </div>
    </div>
</template>

<script>
import { getRequest } from '@/utils/api';

export default {
    name: 'PermissMana',
    data() {
        return {
            //添加的角色对象
            role: {
                name: '',
                nameZh: '',
            },
            //展示后端查询到的角色数组
            roles: [],
            //树形控件要展示的所有菜单
            allMenus:[],
            defaultProps:{
                children:'children',
                label:'name',
            },
            //选择的菜单id
            selectedMenus:[],
            //默认打开的手风琴是哪个
            activeName:-1,
        }
    },
    mounted() {
        this.initRoles();
    },
    methods: {
        //删除角色
        doDeleteRole(role){
            this.$confirm('此操作将永久删除[' + role.nameZh + ']角色, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteRequest('/system/basic/permission/role/' + role.id).then(resp => {
                    if (resp) {
                        this.initRoles();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //添加角色
        doAddRole(){
            if(this.role.name && this.role.nameZh){
                this.postRequest('/system/basic/permission/role',this.role).then(resp=>{
                    if(resp){
                        this.initRoles();
                        this.role.name='';
                        this.role.nameZh='';
                    }
                })
            }else{
                this.$message.error("所有字段不能为空")
            }
        },
        //取消更新
        cancelUpdate(){
            this.activeName=-1;
        },
        //更新某个角色下的所有菜单
        doUpdate(rid,index){
            //获取树
            let tree=this.$refs.tree[index]
            //获取叶子节点的id，如果没有加true，那么会获取所有的id
            let selectedKeys = tree.getCheckedKeys(true);
            // console.log(selectedKeys)
            //循环添加至url中
            let url='/system/basic/permission/?rid='+rid;
            selectedKeys.forEach(key=>{
                url += '&mids=' + key;
            });
            // console.log(url);
            this.putRequest(url).then(resp=>{
                if(resp){
                    this.initRoles();
                    this.activeName=-1;
                }
            })
        },
        //改变手风琴面板选择时的方法，参数是每个手风琴的“:name="r.id"”
        change(rid){
            if(rid){
                // alert(rid);
                this.initAllMenus();
                this.initSelectedMenus(rid);
            }
        },
        //调用后端接口，根据角色id查询菜单id
        initSelectedMenus(rid){
            getRequest('/system/basic/permission/mid/'+rid).then(resp=>{
                if(resp){
                    this.selectedMenus = resp;
                }
            })
        },
        //获取所有菜单
        initAllMenus(){
            this.getRequest('/system/basic/permission/menus').then(resp=>{
                if(resp){
                    this.allMenus=resp;
                }
            })
        },
        //获取所有角色
        initRoles() {
            this.getRequest('/system/basic/permission/').then(resp => {
                if (resp) {
                    this.roles = resp;
                }
            })
        }
    }
};
</script>

<style scoped>
.permissManaTool {
    display: flex;
    justify-content: flex-start;
}

.permissManaTool .el-input {
    width: 300px;
    margin-right: 6px;
}

.permissManaMain {
    margin-top: 10px;
    width: 700px;
}
</style>