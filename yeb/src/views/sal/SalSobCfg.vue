<template>
    <div>
        <el-table :data="emps" border stripe>
            <el-table-column type="selection" align="left" width="55">
            </el-table-column>
            <el-table-column prop="name" label="姓名" align="left" fixed width="120">
            </el-table-column>
            <el-table-column prop="workID" label="工号" align="left" width="120">
            </el-table-column>
            <el-table-column prop="email" label="电子邮箱" align="left" width="200">
            </el-table-column>
            <el-table-column prop="phone" label="电话号码" align="left" width="120">
            </el-table-column>
            <el-table-column prop="department.name" label="所属部门" align="left" width="120">
            </el-table-column>
            <el-table-column label="工资账套" align="center" width="150">
                <template slot-scope="scope">
                    <el-tooltip placement="right" v-if="scope.row.salary">
                        <div slot="content">
                            <table>
                                <tr>
                                    <td>基本工资</td>
                                    <td>{{ scope.row.salary.basicSalary }}</td>
                                </tr>
                                <tr>
                                    <td>交通补助</td>
                                    <td>{{ scope.row.salary.trafficSalary }}</td>
                                </tr>
                                <tr>
                                    <td>午餐补助</td>
                                    <td>{{ scope.row.salary.lunchSalary }}</td>
                                </tr>
                                <tr>
                                    <td>养老金比率</td>
                                    <td>{{ scope.row.salary.pensionPer }}</td>
                                </tr>
                                <tr>
                                    <td>养老金基数</td>
                                    <td>{{ scope.row.salary.pensionBase }}</td>
                                </tr>
                                <tr>
                                    <td>医疗保险比率</td>
                                    <td>{{ scope.row.salary.medicalPer }}</td>
                                </tr>
                                <tr>
                                    <td>医疗保险基数</td>
                                    <td>{{ scope.row.salary.medicalBase }}</td>
                                </tr>
                                <tr>
                                    <td>公积金比率</td>
                                    <td>{{ scope.row.salary.accumulationFundPer }}</td>
                                </tr>
                                <tr>
                                    <td>公积金基数</td>
                                    <td>{{ scope.row.salary.accumulationFundBase }}</td>
                                </tr>
                            </table>
                        </div>
                        <el-tag >{{ scope.row.salary.name }}</el-tag>
                    </el-tooltip>
                    <el-tag v-else>暂未设置</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="150">
                <template slot-scope="scope">
                    <el-popover
                        placement="left"
                        title="编辑工资账套"
                        @show="showPop(scope.row.salary)"
                        @hide="hidePop(scope.row)"
                        width="200"
                        trigger="click">
                        <div>
                            <el-select v-model="currentSalary" placeholder="请选择">
                                <el-option
                                    v-for="item in salaries"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </div>
                        <el-button slot="reference" type="danger">修改工资账套</el-button>
                    </el-popover>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页插件 -->
        <div style="display: flex;justify-content: flex-end;margin-top: 10px;">
            <el-pagination
                background
                @current-change="currentChange"
                @size-change="sizeChange"
                layout="sizes, prev, pager, next, jumper, ->, total, slot"
                :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
export default {
    name: 'SalSobCfg',
    data(){
        return{
            //当前工资账套
            currentSalary:null,
            //查询所有工资账套
            salaries:[],
            //初始化查询员工账套信息
            emps:[],
            //默认分页的页码
            currentPage:1,
            //默认分页的行数
            size:10,
            //分页查询的总数据量
            total:0,
        }
    },
    mounted(){
        this.initEmps();
        this.initSalaries();
    },
    methods:{
        //隐藏当行工资账套
        hidePop(data){
            if(this.currentSalary && this.currentSalary!=data.salary.id){
                this.putRequest('/salary/sobcfg/?eid='+data.id+'&sid='+this.currentSalary).then(resp=>{
                    if(resp){
                        this.initEmps();
                    }
                })
            }
        },
        //点击展示当行的工资账套
        showPop(data){
            if(data){
                this.currentSalary=data.id;
            }
            this.currentSalary=null;
        },
        //查询获取所有的工资账套
        initSalaries(){
            this.getRequest('/salary/sobcfg/salaries').then(resp=>{
                if(resp){
                    this.salaries=resp;
                }
            })
        },
        //页码改变方法
        currentChange(page){
            this.currentPage=page;
            this.initEmps();
        },
        //页码显示数据量改变函数
        sizeChange(size){
            this.size=size;
            this.initEmps();
        },
        //初始化员工账套数据
        initEmps(){
            this.getRequest('/salary/sobcfg/?currentPage='+this.currentPage+'&size='+this.size).then(resp=>{
                if(resp){
                    this.emps=resp.data;
                    this.total=resp.total;
                }
            })
        },
    }
};
</script>

<style scoped>

</style>