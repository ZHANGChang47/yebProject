<template>
    <div>
        <div>
            <el-input v-model="jl.name" placeholder="添加职称等级..." prefix-icon="el-icon-plus" style="width:300px"
                size="small"></el-input>
            <el-select size="small" v-model="jl.titleLevel" placeholder="职称等级"
                style="margin-left: 6px;margin-right: 6px;">
                <el-option v-for="item in titleLevels" :key="item" :label="item" :value="item">
                </el-option>
            </el-select>
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addJoblevel">添加</el-button>
        </div>
        <div style="margin-top:10px">
            <el-table :data="jls" stripe border style="width: 70%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55">
                </el-table-column>
                <el-table-column prop="id" label="编号" width="55">
                </el-table-column>
                <el-table-column prop="name" label="职称名称" width="150">
                </el-table-column>
                <el-table-column prop="titleLevel" label="职称等级" width="150">
                </el-table-column>
                <el-table-column prop="createDate" label="创建日期" width="150">
                </el-table-column>
                <el-table-column prop="enabled" label="是否启用" width="150">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.enabled" type="success">已启用</el-tag>
                        <el-tag v-else type="danger">未启用</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="showEditView(scope.row)">
                            编辑
                        </el-button>
                        <el-button size="mini" type="danger" @click="deleteHandler(scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-button type="danger" size="small" style="margin-top:10px" :disabled="this.multipleSelection.length==0" @click="deleteMany">批量删除</el-button>
        </div>
        <el-dialog title="编辑职称" :visible.sync="dialogVisible" width="30%">
            <table>
                <tr>
                    <td>
                        <el-tag>职称名称</el-tag>
                    </td>
                    <td>
                        <el-input v-model="updateJl.name" size="small" style="margin-left: 6px;"></el-input>
                    </td>
                </tr>
                <tr>
                    <td>
                        <el-tag>职称等级</el-tag>
                    </td>
                    <td>
                        <el-select size="small" v-model="updateJl.titleLevel" placeholder="职称等级"
                            style="margin-left: 6px;margin-right: 6px;">
                            <el-option v-for="item in titleLevels" :key="item" :label="item" :value="item">
                            </el-option>
                        </el-select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <el-tag>是否启用</el-tag>
                    </td>
                    <td>
                        <el-switch  v-model="updateJl.enabled"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949"
                                    active-text="已启用"
                                    inactive-text="未启用"
                                    style="margin-left:6px">
                        </el-switch>
                    </td>
                </tr>
            </table>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" @click="dialogVisible = false">取 消</el-button>
                <el-button size="small" type="primary" @click="doUpdate">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'JoblevelMana',
    data() {
        return {
            //添加数据的jl对象
            jl: {
                name: '',
                titleLevel: ''
            },
            //可选职称
            titleLevels: [
                '正高级',
                '副高级',
                '中级',
                '初级',
                '员级'
            ],
            //后端返回的职称对象列表
            jls: [],
            //弹出框是否展开
            dialogVisible: false,
            //更新职称对象
            updateJl: {
                name: '',
                titleLevel: '',
                enabled:false,
            },
            //表格多选内容
            multipleSelection:[],
        }
    },
    mounted() {
        this.initJls();
    },
    methods: {
        //批量删除
        deleteMany(){
            this.$confirm('此操作将永久删除[' + this.multipleSelection.length + ']条职称, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //制作批量删除数组，例如：?ids=20&ids=21&
                let ids = '?';
                this.multipleSelection.forEach(item=>{
                    ids+='ids='+item.id+'&';
                });
                this.deleteRequest('/system/basic/joblevel/' + ids).then(resp => {
                    if (resp) {
                        this.initJls();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //处理选中事件
        handleSelectionChange(val){
            this.multipleSelection=val;
        },
        //更新数据
        doUpdate(){
            this.putRequest('/system/basic/joblevel/',this.updateJl).then(resp=>{
                if(resp){
                    this.initJls();
                    this.dialogVisible=false;
                }
            })  
        },
        //打开弹出框函数
        showEditView(data) {
            Object.assign(this.updateJl,data);
            this.updateJl.createDate='';
            this.dialogVisible = true;
        },
        //删除职称
        deleteHandler(data) {
            this.$confirm('此操作将永久删除[' + data.name + ']职称, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteRequest('/system/basic/joblevel/' + data.id).then(resp => {
                    if (resp) {
                        this.initJls();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //添加职称
        addJoblevel() {
            if (this.jl.name && this.jl.titleLevel) {
                this.postRequest('/system/basic/joblevel/', this.jl).then(resp => {
                    if (resp) {
                        this.initJls();
                    }
                })
            } else {
                this.$message.error('字段不能为空')
            }
        },
        //获取表格数据
        initJls() {
            this.getRequest('/system/basic/joblevel/').then(resp => {
                if (resp) {
                    this.jls = resp;
                    this.jl.name = '';
                    this.jl.titleLevel = '';
                }
            })
        }
    }
};
</script>

<style scoped>

</style>