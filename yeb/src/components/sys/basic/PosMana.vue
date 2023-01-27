<template>
    <div>
        <div>
            <el-input size="small" class="addPosInput" placeholder="添加职位..." suffix-icon="el-icon-plus"
                @keydown.enter.native="addPosition" v-model="pos.name">
            </el-input>
            <el-button size="small" icon="el-icon-plus" type="primary" @click="addPosition">添加</el-button>
        </div>
        <div class="posManaMain">
            <el-table stripe border :data="positions" 
                      @selection-change="handleSelectionChange"
                      style="width: 70%">
                <el-table-column type="selection" width="55">
                </el-table-column>
                <el-table-column prop="id" label="编号" width="55">
                </el-table-column>
                <el-table-column prop="name" label="职位" width="120">
                </el-table-column>
                <el-table-column prop="createDate" label="创建时间" width="200">
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="showEditView(scope.$index, scope.row)">
                            编辑
                        </el-button>
                        <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <el-button type="danger" size="small" style="margin-top:8px" :disabled="this.multipleSelection.length==0" @click="deleteMany">批量删除</el-button>
        <el-dialog title="编辑职位" 
                    :visible.sync="dialogVisible" 
                    width="30%">
            <div>
                <el-tag>职位名称</el-tag>
                <el-input v-model="updatePos.name" size="small" class="updatePosInput"></el-input>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" @click="dialogVisible = false">取 消</el-button>
                <el-button size="small" type="primary" @click="doUpdate">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'PosMana',
    data() {
        return {
            //添加对象
            pos: {
                name: ''
            },
            //表格数据对象
            positions: [],
            //是否展开弹出框
            dialogVisible:false,
            //更新对象
            updatePos:{
                name:''
            },
            //表格多选内容
            multipleSelection:[],
        }
    },
    mounted() {
        this.initPositions();
    },
    methods: {
        //批量删除方法
        deleteMany(){
            this.$confirm('此操作将永久删除[' + this.multipleSelection.length + ']条职位, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //制作批量删除数组，例如：?ids=20&ids=21&
                let ids = '?';
                this.multipleSelection.forEach(item=>{
                    ids+='ids='+item.id+'&';
                });
                // console.log(ids)
                this.deleteRequest('/system/basic/pos/' + ids).then(resp => {
                    if (resp) {
                        this.initPositions();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //获得多选内容，并根据多选内容数组的长度判断受否禁用批量删除
        handleSelectionChange(val){
            this.multipleSelection=val;
        },
        //添加方法
        addPosition() {
            if (this.pos.name) {
                this.postRequest('/system/basic/pos/', this.pos).then(resp => {
                    //成功调用接口后，再次刷新表格
                    this.initPositions();
                    //调用成功后，将输入框中的内容删除
                    this.pos.name = '';
                })
            } else {
                this.$message.error("职位名称为空")
            }
        },
        //显示编辑框方法
        showEditView(index, data) {
            //回显：拷贝之前的数据
            Object.assign(this.updatePos,data);
            this.updatePos.createDate='';
            //打开编辑框
            this.dialogVisible=true;
        },
        //更新方法
        doUpdate(){
            this.putRequest('/system/basic/pos/',this.updatePos).then(resp=>{
                if(resp){
                    this.initPositions();
                    this.dialogVisible=false;
                }
            })
        },
        //删除方法
        handleDelete(index, data) {
            this.$confirm('此操作将永久删除[' + data.name + ']职位, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteRequest('/system/basic/pos/' + data.id).then(resp => {
                    if (resp) {
                        this.initPositions();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //初始化表格方法，在mounted中调用
        initPositions() {
            this.getRequest('/system/basic/pos/').then(resp => {
                if (resp) {
                    this.positions = resp;
                }
            })
        }
    }
};
</script>

<style scoped>
.addPosInput {
    width: 300px;
    margin-right: 10px;
}

.posManaMain {
    margin-top: 10px;
}

.updatePosInput{
    width: 200px;
    margin-left: 8px;
}
</style>