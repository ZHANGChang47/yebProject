<template>
    <div>
        <div>
            <!--工具类-->
            <div style="display:flex;justify-content: space-between;margin-top: 10px;">
                <div>
                    <el-input style="width:300px;margin-right:10px" v-model="empName" @keydown-enter-native="initEmps"
                        prefix-icon="el-icon-search" clearable @clear="initEmps"
                        placeholder="请输入员工姓名进行搜索..." :disabled="showAdvanceSearchVisible  ? true : false"></el-input>
                    <el-button type="primary" icon="el-icon-search" @click="initEmps" :disabled="showAdvanceSearchVisible  ? true : false">搜索</el-button>
                    <el-button type="primary" @click="showAdvanceSearchVisible=!showAdvanceSearchVisible">
                        <i :class="showAdvanceSearchVisible ? 'fa fa-angle-double-up' : 'fa fa-angle-double-down'" aria-hidden="true"></i>
                        高级搜索
                    </el-button>
                </div>
                <div>
                    <el-upload style="display:inline-flex;margin-right:8px" :show-file-list=false
                        :before-upload="beforeUpload" :on-success="onSuccess" :on-Error="onError"
                        :disabled="importDataDisabled" :headers="headers" action="/employee/basic/import">
                        <el-button type="success" :icon="importDataBtnIcon" :disabled="importDataDisabled">
                            {{ importDataBtnText }}
                        </el-button>
                    </el-upload>
                    <el-button @click="exportData" type="success" icon="el-icon-download">
                        导出数据
                    </el-button><el-button type="primary" icon="el-icon-plus" @click="showAddEmpView">
                        添加员工
                    </el-button>
                </div>
            </div>
        </div>
        <!-- 高级搜索框 -->
        <div v-show="showAdvanceSearchVisible" style="border:1px solid #409eff;border-radius:5px;box-sizing: border-box;padding: 5px;margin: 10px 0px;">
            <el-row>
                <el-col :span="5">
                    政治面貌:
                    <el-select v-model="searchValue.politicId" placeholder="政治面貌" size="mini" style="width:150px">
                        <el-option v-for="item in politicsstatus" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="4">
                    民族:
                    <el-select v-model="searchValue.nationId" placeholder="民族" size="mini" style="width:150px">
                        <el-option v-for="item in nations" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="4">
                    职位:
                    <el-select v-model="searchValue.posId" placeholder="职位" size="mini" style="width:150px">
                        <el-option v-for="item in positions" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="4">
                    职称:
                    <el-select v-model="searchValue.jobLevelId" placeholder="职称" size="mini" style="width:150px">
                        <el-option v-for="item in joblevels" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="7">
                    聘用形式:
                    <el-radio-group v-model="searchValue.engageForm">
                        <el-radio label="劳动合同">劳动合同</el-radio>
                        <el-radio label="劳务合同">劳务合同</el-radio>
                    </el-radio-group>
                </el-col>
            </el-row>
            <el-row style="margin-top:10px">
                <el-col :span="5">
                    所属部门:
                    <el-popover placement="right" title="请选择部门" width="200" trigger="manual" v-model="visible2">
                        <el-tree default-expand-all :data="allDeps" :props="defaultProps"
                            @node-click="searchHandleNodeClick"></el-tree>
                        <div style="width:140px;display:inline-flex;border:1px solid #dedede;height:25px;border-radius: 5px;cursor:pointer;font-size: 13px;padding-left: 8px; box-sizing=border-box"
                            slot="reference" @click="showDepView2">{{ inputDepName }}
                        </div>
                    </el-popover>
                </el-col>
                <el-col :span="10">
                    入职日期:
                    <el-date-picker v-model="searchValue.beginDateScope" value-format="yyyy-MM-dd" unlink-panels type="daterange" size="mini" range-separator="至" start-placeholder="开始日期"
                        end-placeholder="结束日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="5" :offset="4">
                    <el-button size="mini" @click="clearSearch">取消</el-button>
                    <el-button size="mini" icon="el-icon-search" type="primary" @click="initEmps('advanced')">搜索</el-button>
                </el-col>
            </el-row>
        </div>
        <!--表格-->
        <div style="margin-top: 10px;">
            <el-table :data="emps" stripe border style="width: 100%" v-loading="loading"
                element-loading-text="正在加载员工数据..." element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)">
                <el-table-column type="selection" width="55">
                </el-table-column>
                <el-table-column prop="name" label="姓名" align="left" width="70" fixed>
                </el-table-column>
                <el-table-column prop="workID" label="工号" align="left" width="85">
                </el-table-column>
                <el-table-column prop="gender" label="性别" align="left" width="50">
                </el-table-column>
                <el-table-column prop="birthday" label="出生日期" align="left" width="95">
                </el-table-column>
                <el-table-column prop="idCard" label="身份证号码" align="left" width="150">
                </el-table-column>
                <el-table-column prop="wedlock" label="婚姻状况" align="left" width="70">
                </el-table-column>
                <el-table-column prop="nation.name" label="民族" align="left" width="50">
                </el-table-column>
                <el-table-column prop="nativePlace" label="籍贯" align="left" width="80">
                </el-table-column>
                <el-table-column prop="politicsStatus.name" label="政治面貌" align="left" width="100">
                </el-table-column>
                <el-table-column prop="email" label="电子邮件" align="left" width="160">
                </el-table-column>
                <el-table-column prop="phone" label="电话号码" align="left" width="100">
                </el-table-column>
                <el-table-column prop="address" label="联系地址" align="left" width="220">
                </el-table-column>
                <el-table-column prop="email" label="电子邮件" align="left" width="180">
                </el-table-column>
                <el-table-column prop="department.name" label="所属部门" align="left" width="100">
                </el-table-column>
                <el-table-column prop="joblevel.name" label="职称" align="left" width="100">
                </el-table-column>
                <el-table-column prop="position.name" label="职位" align="left" width="100">
                </el-table-column>
                <el-table-column prop="engageForm" label="聘用形式" align="left" width="100">
                </el-table-column>
                <el-table-column prop="tiptopDegree" label="最高学历" align="left" width="80">
                </el-table-column>
                <el-table-column prop="school" label="学校" align="left" width="150">
                </el-table-column>
                <el-table-column prop="specialty" label="专业" align="left" width="150">
                </el-table-column>
                <el-table-column prop="beginDate" label="入职日期" align="left" width="95">
                </el-table-column>
                <el-table-column prop="conversionTime" label="转正日期" align="left" width="95">
                </el-table-column>
                <el-table-column prop="beginContract" label="合同起始日期" align="left" width="95">
                </el-table-column>
                <el-table-column prop="endContract" label="合同截至日期" align="left" width="95">
                </el-table-column>
                <el-table-column label="合同期限" align="left" width="80">
                    <template slot-scope="scope">
                        <el-tag>{{ scope.row.contractTerm }}</el-tag>年
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                    <template slot-scope="scope">
                        <el-button style="padding:1px" size="mini" @click="showEditEmpView(scope.row)">编辑</el-button>
                        <el-button style="padding:1px" size="mini" type="primary">查看高级资料</el-button>
                        <el-button style="padding:1px" size="mini" type="danger"
                            @click="deleteEmp(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页插件 -->
            <div style="display:flex;justify-content:flex-end;margin-top: 10px;">
                <el-pagination background @current-change="currentChange" @size-change="sizeChange"
                    layout="sizes, prev, pager, next, jumper, ->, total" :total="total">
                </el-pagination>
            </div>
        </div>
        <!-- 添加或者更新对话框 -->
        <el-dialog :title="title" :visible.sync="dialogVisible" width="80%">
            <div>
                <el-form ref="empForm" :model="emp" :rules="rules">
                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="姓名:" prop="name">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-edit" v-model="emp.name"
                                    placeholder="请输入员工姓名..."></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="性别:" prop="gender">
                                <el-radio-group v-model="emp.gender">
                                    <el-radio label="男">男</el-radio>
                                    <el-radio label="女">女</el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="出生日期:" prop="birthday">
                                <el-date-picker v-model="emp.birthday" type="date" size="mini" value-format="yyyy-MM-dd"
                                    placeholder="出生日期" style="width:150px">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="政治面貌:" prop="politicId">
                                <el-select v-model="emp.politicId" placeholder="政治面貌" size="mini" style="width:150px">
                                    <el-option v-for="item in politicsstatus" :key="item.id" :label="item.name"
                                        :value="item.id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="民族:" prop="nationId">
                                <el-select v-model="emp.nationId" placeholder="民族" size="mini" style="width:150px">
                                    <el-option v-for="item in nations" :key="item.id" :label="item.name"
                                        :value="item.id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="籍贯:" prop="nativePlace">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-edit"
                                    v-model="emp.nativePlace" placeholder="请输入员工籍贯..."></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="电子邮箱:" prop="email">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-message"
                                    v-model="emp.email" placeholder="请输入电子邮箱..."></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="联系地址:" prop="address">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-edit"
                                    v-model="emp.address" placeholder="请输入联系地址..."></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="职位:" prop="posId">
                                <el-select v-model="emp.posId" placeholder="职位" size="mini" style="width:150px">
                                    <el-option v-for="item in positions" :key="item.id" :label="item.name"
                                        :value="item.id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="职称:" prop="jobLevelId">
                                <el-select v-model="emp.jobLevelId" placeholder="职称" size="mini" style="width:150px">
                                    <el-option v-for="item in joblevels" :key="item.id" :label="item.name"
                                        :value="item.id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="所属部门:" prop="departmentId">
                                <el-popover placement="right" title="请选择部门" width="200" trigger="manual"
                                    v-model="visible">
                                    <el-tree default-expand-all :data="allDeps" :props="defaultProps"
                                        @node-click="handleNodeClick"></el-tree>
                                    <div style="width:140px;display:inline-flex;border:1px solid #dedede;height:27px;border-radius: 5px;margin-top: 2px;cursor:pointer;align-items: center;font-size: 13px;padding-left: 8px; box-sizing=border-box"
                                        slot="reference" @click="showDepView">{{ inputDepName }}
                                    </div>
                                </el-popover>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="电话号码:" prop="phone">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-phone"
                                    v-model="emp.phone" placeholder="请输入电话号码..."></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="工号:" prop="workID">
                                <el-input disabled size="mini" style="width:150px" prefix-icon="el-icon-edit"
                                    v-model="emp.workID" placeholder="请输入工号..."></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="学历:" prop="tiptopDegree">
                                <el-select v-model="emp.tiptopDegree" placeholder="学历" size="mini" style="width:150px">
                                    <el-option v-for="item in tiptopDegrees" :key="item" :label="item" :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="毕业院校:" prop="school">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-edit"
                                    v-model="emp.school" placeholder="请输入毕业院校..."></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="专业名称:" prop="specialty">
                                <el-input size="mini" style="width:150px" prefix-icon="el-icon-edit"
                                    v-model="emp.specialty" placeholder="请输入专业名称..."></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="入职日期:" prop="beginDate">
                                <el-date-picker v-model="emp.beginDate" type="date" size="mini"
                                    value-format="yyyy-MM-dd" placeholder="入职日期" style="width:150px">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="转正日期:" prop="conversionTime">
                                <el-date-picker v-model="emp.conversionTime" type="date" size="mini"
                                    value-format="yyyy-MM-dd" placeholder="转正日期" style="width:150px">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="合同起始日期:" prop="beginContract">
                                <el-date-picker v-model="emp.beginContract" type="date" size="mini"
                                    value-format="yyyy-MM-dd" placeholder="合同起始日期" style="width:150px">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="合同截止日期:" prop="endContract">
                                <el-date-picker v-model="emp.endContract" type="date" size="mini"
                                    value-format="yyyy-MM-dd" placeholder="合同截止日期" style="width:150px">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item label="身份证号码:" prop="idCard">
                                <el-input size="mini" style="width:180px" prefix-icon="el-icon-edit"
                                    v-model="emp.idCard" placeholder="请输入身份证号码..."></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="聘用形式:" prop="engageForm">
                                <el-radio-group v-model="emp.engageForm">
                                    <el-radio label="劳动合同">劳动合同</el-radio>
                                    <el-radio label="劳务合同">劳务合同</el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="婚姻状况:" prop="wedlock">
                                <el-radio-group v-model="emp.wedlock">
                                    <el-radio label="已婚">已婚</el-radio>
                                    <el-radio label="未婚">未婚</el-radio>
                                    <el-radio label="离异">离异</el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="doAddEmp">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>

export default {
    name: 'EmpBasic',
    data() {
        return {
            //后端查询的员工对象数据列表
            emps: [],
            //加载动画开启开关
            loading: false,
            //总页数
            total: 0,
            //页码
            currentPage: 1,
            //每页数据量
            size: 10,
            //搜索用员工名
            empName: '',
            //dialog对话框是否弹出
            dialogVisible: false,
            //存放所有民族对象
            nations: [],
            //存放所有职称对象
            joblevels: [],
            //存放所有政治面貌对象
            politicsstatus: [],
            //存放所有职位对象
            positions: [],
            //学历数组对象
            tiptopDegrees: ['博士', '硕士', '本科', '大专', '高中', '初中', '小学', '其他'],
            //所属部门对话框是否弹出
            visible: false,
            //高级搜索中所属部门对话框是否弹出
            visible2: false,
            //所属部门树形控件展示的属性
            defaultProps: {
                children: 'children',
                label: 'name'
            },
            //所属部门树形控件展示数据
            allDeps: [],
            //所属部门回显数据
            inputDepName: '',
            //导入数据按钮显示文字
            importDataBtnText: '导入数据',
            //导入数据按钮显示图标
            importDataBtnIcon: 'el-icon-upload2',
            //导入数据禁用
            importDataDisabled: false,
            //导入数据请求头
            headers: {
                Authorization: window.sessionStorage.getItem('tokenStr')
            },
            //高级搜索框是否展开
            showAdvanceSearchVisible:false,
            //高级搜索对象
            searchValue:{
                politicId: null,
                nationId: null,
                jobLevelId: null,
                posId: null,
                engageForm: '',
                departmentId: null,
                beginDateScope: null,
            },
            //表单对象
            emp: {
                id: null,
                name: '',
                gender: '',
                birthday: '',
                idCard: '',
                wedlock: '',
                nationId: null,
                nativePlace: '',
                politicId: null,
                email: '',
                phone: '',
                address: '',
                departmentId: null,
                jobLevelId: null,
                posId: null,
                engageForm: '',
                tiptopDegree: '',
                specialty: '',
                school: '',
                beginDate: '',
                workState: '在职',
                workID: '',
                contractTerm: null,
                conversionTime: '',
                notWorkDate: null,
                beginContract: '',
                endContract: '',
                workAge: null,
                salaryId: null,
                salary: null
            },
            //表达名称
            title: '',
            //数据校验规则
            rules: {
                name: [{ required: true, message: '请输入员工名', trigger: 'blur' }],
                gender: [{ required: true, message: '请输入员工性别', trigger: 'blur' }],
                birthday: [{ required: true, message: '请输入出生日期', trigger: 'blur' }],
                idCard: [{ required: true, message: '请输入身份证号码', trigger: 'blur' }, { pattern: /(^([1-6][1-9]|50)\d{4}(18|19|20)\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^([1-6][1-9]|50)\d{4}\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}$)/, message: '身份证号码不正确', trigger: 'blur' }],
                wedlock: [{ required: true, message: '请输入婚姻状况', trigger: 'blur' }],
                nationId: [{ required: true, message: '请输入民族', trigger: 'blur' }],
                nativePlace: [{ required: true, message: '请输入籍贯', trigger: 'blur' }],
                politicId: [{ required: true, message: '请输入政治面貌', trigger: 'blur' }],
                email: [{ required: true, message: '请输入邮箱地址', trigger: 'blur' }, { type: "email", message: '邮箱地址格式不正确', trigger: 'blur' }],
                phone: [{ required: true, message: '请输入电话号码', trigger: 'blur' }],
                address: [{ required: true, message: '请输入通讯地址', trigger: 'blur' }],
                departmentId: [{ required: true, message: '请输入所属部门', trigger: 'blur' }],
                jobLevelId: [{ required: true, message: '请输入职称', trigger: 'blur' }],
                posId: [{ required: true, message: '请输入职位', trigger: 'blur' }],
                engageForm: [{ required: true, message: '请输入聘用形式', trigger: 'blur' }],
                tiptopDegree: [{ required: true, message: '请输学历', trigger: 'blur' }],
                specialty: [{ required: true, message: '请输入专业', trigger: 'blur' }],
                school: [{ required: true, message: '请输入毕业院校', trigger: 'blur' }],
                beginDate: [{ required: true, message: '请输入入职日期', trigger: 'blur' }],
                workState: [{ required: true, message: '请输入工作状态', trigger: 'blur' }],
                workID: [{ required: true, message: '请输入工号', trigger: 'blur' }],
                contractTerm: [{ required: true, message: '请输入合同期限', trigger: 'blur' }],
                conversionTime: [{ required: true, message: '请输入转正日期', trigger: 'blur' }],
                notWorkDate: [{ required: true, message: '请输入离职日期', trigger: 'blur' }],
                beginContract: [{ required: true, message: '请输入合同起始日期', trigger: 'blur' }],
                endContract: [{ required: true, message: '请输入合同结束日期', trigger: 'blur' }],
                // workAge:[{required:true,message:'请输入工龄',trigger:'blur'}],
            }
        }
    },
    mounted() {
        this.initEmps();
        this.initData();
        this.initPositions();
    },
    methods: {
        //清空高级搜索
        clearSearch(){
            this.searchValue.politicId= null,
            this.searchValue.nationId= null,
            this.searchValue.jobLevelId= null,
            this.searchValue.posId= null,
            this.searchValue.engageForm= '',
            this.searchValue.departmentId= null,
            this.searchValue.beginDateScope= null
        },
        //弹出高级搜索部门菜单
        showDepView2(){
            this.visible2 = !this.visible2;
        },
        //高级搜索中点击选择部门
        searchHandleNodeClick(data){
            this.inputDepName = data.name;
            this.searchValue.departmentId = data.id;
            this.visible2 = !this.visible2;
        },
        //导入数据成功回调函数
        onSuccess() {
            this.importDataBtnIcon = 'el-icon-upload2';
            this.importDataBtnText = '导入数据';
            this.importDataDisabled = false;
            this.initEmps();
        },
        //导入数据失败回调函数
        onError() {
            this.importDataBtnIcon = 'el-icon-upload2';
            this.importDataBtnText = '导入数据';
            this.importDataDisabled = false;
        },
        //导入数据图标变化函数
        beforeUpload() {
            this.importDataBtnIcon = 'el-icon-loading';
            this.importDataBtnText = '正在导入';
            this.importDataDisabled = true;
        },
        //导出员工数据
        exportData() {
            this.downloadRequest('/employee/basic/export');
        },
        //弹出编辑对话框
        showEditEmpView(data) {
            this.title = "编辑员工";
            this.emp = data;
            this.inputDepName = data.department.name;
            this.initPositions();
            this.dialogVisible = true;
        },
        //根据id删除员工
        deleteEmp(data) {
            this.$confirm('此操作将永久删除[' + data.name + '], 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteRequest('/employee/basic/' + data.id).then(resp => {
                    if (resp) {
                        this.initEmps();
                    }
                });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        //点击添加员工
        doAddEmp() {
            if (this.emp.id) {
                this.$refs['empForm'].validate(valid => {
                    if (valid) {
                        this.putRequest('/employee/basic/', this.emp).then(resp => {
                            if (resp) {
                                this.dialogVisible = false;
                                this.initEmps();
                            }
                        })
                    }
                })
            } else {
                this.$refs['empForm'].validate(valid => {
                    if (valid) {
                        this.postRequest('/employee/basic/', this.emp).then(resp => {
                            if (resp) {
                                this.dialogVisible = false;
                                this.initEmps();
                            }
                        })
                    }
                })
            }

        },
        //节点点击事件，参数data是节点数据
        handleNodeClick(data) {
            this.emp.departmentId = data.id;
            this.inputDepName = data.name;
            this.visible = !this.visible;
        },
        //点击显示所属部门
        showDepView() {
            this.visible = !this.visible;
        },
        //获取最大工号，打开添加员工对话框时调用
        getMaxWorkID() {
            this.getRequest('/employee/basic/maxWorkID').then(resp => {
                if (resp) {
                    this.emp.workID = resp.obj;
                }
            })
        },
        //初始化获取所有职位数据信息，打开添加员工对话框时调用
        initPositions() {
            this.getRequest('/employee/basic/positions').then(resp => {
                if (resp) {
                    this.positions = resp;
                }
            })
        },
        //初始化不怎么变动的数据（政治面貌、民族、职称、所属部门），并放在sessionStorage中
        initData() {
            if (!window.sessionStorage.getItem('nations')) {
                this.getRequest('/employee/basic/nations').then(resp => {
                    if (resp) {
                        this.nations = resp;
                        window.sessionStorage.setItem('nations', JSON.stringify(resp));
                    }
                })
            } else {
                this.nations = JSON.parse(window.sessionStorage.getItem('nations'));
            }
            if (!window.sessionStorage.getItem('joblevels')) {
                this.getRequest('/employee/basic/joblevels').then(resp => {
                    if (resp) {
                        this.joblevels = resp;
                        window.sessionStorage.setItem('joblevels', JSON.stringify(resp));
                    }
                })
            } else {
                this.joblevels = JSON.parse(window.sessionStorage.getItem('joblevels'));
            }
            if (!window.sessionStorage.getItem('politicsstatus')) {
                this.getRequest('/employee/basic/politicsstatus').then(resp => {
                    if (resp) {
                        this.politicsstatus = resp;
                        window.sessionStorage.setItem('politicsstatus', JSON.stringify(resp));
                    }
                })
            } else {
                this.politicsstatus = JSON.parse(window.sessionStorage.getItem('politicsstatus'));
            }
            if (!window.sessionStorage.getItem('allDeps')) {
                this.getRequest('/employee/basic/deps').then(resp => {
                    if (resp) {
                        this.allDeps = resp;
                        window.sessionStorage.setItem('allDeps', JSON.stringify(resp));
                    }
                });
            } else {
                this.allDeps = JSON.parse(window.sessionStorage.getItem('allDeps'));
            }
        },
        //打开添加员工的对话框
        showAddEmpView() {
            this.title = '添加员工';
            this.emp = {
                id: null,
                name: '',
                gender: '',
                birthday: '',
                idCard: '',
                wedlock: '',
                nationId: null,
                nativePlace: '',
                politicId: null,
                email: '',
                phone: '',
                address: '',
                departmentId: null,
                jobLevelId: null,
                posId: null,
                engageForm: '',
                tiptopDegree: '',
                specialty: '',
                school: '',
                beginDate: '',
                workState: '在职',
                workID: '',
                contractTerm: null,
                conversionTime: '',
                notWorkDate: null,
                beginContract: '',
                endContract: '',
                workAge: null,
                salaryId: null,
                salary: null
            };
            this.inputDepName = '';
            this.getMaxWorkID();
            this.initPositions();
            this.dialogVisible = true;
        },
        //页码改变函数，参数currentPage（页码）在点击时会自动带过来
        currentChange(currentPage) {
            this.currentPage = currentPage;
            this.initEmps();
        },
        //每页数据量改变函数，参数size（每页数据量）在点击时会自动带过来
        sizeChange(size) {
            this.size = size;
            this.initEmps();
        },
        //初始化查询所有员工数据，获取分页的总条数以及第一页的所有数据
        initEmps(type) {
            this.loading = true;
            //定义url
            let url = '/employee/basic/?currentPage=' + this.currentPage + '&size=' + this.size;
            if(type && type=='advanced'){
                if(this.searchValue.politicId){
                    url += '&politicId='+this.searchValue.politicId;
                }
                if(this.searchValue.nationId){
                    url += '&nationId='+this.searchValue.nationId;
                }
                if(this.searchValue.posId){
                    url += '&posId='+this.searchValue.posId;
                }
                if(this.searchValue.jobLevelId){
                    url += '&jobLevelId='+this.searchValue.jobLevelId;
                }
                if(this.searchValue.engageForm){
                    url += '&engageForm='+this.searchValue.engageForm;
                }
                if(this.searchValue.departmentId){
                    url += '&departmentId='+this.searchValue.departmentId;
                }
                if(this.searchValue.beginDateScope){
                    url += '&beginDateScope='+this.searchValue.beginDateScope;
                }
            }else{
                url += '&name=' + this.empName;
            }


            this.getRequest(url).then(resp => {
                this.loading = false;
                if (resp) {
                    this.emps = resp.data;
                    this.total = resp.total;
                }
            })
        }
    }
};
</script>

<style scoped>

</style>