//封装菜单请求工具类
//后端返回的component是一个字符串，而前端则是导入的对象

//获取下get请求，去调用后端接口，由于现在是js文件，而不是vue文件，所以需要导入
import { getRequest } from "./api";


//获取数据后放在router和store中
export const initMenu = (router,store) => {
    //如果routes的长度大于0，说明不用初始化
    if(store.state.routes.length>0){
        return;
    }

    getRequest('/system/config/menu').then(data=>{
        if(data){
            //如果能获取到数据那么就要进行格式化
            let fmtRoutes = formatRoutes(data);
            //添加到router
            // router.addRoutes(fmtRoutes);
            for(let i=0,length=fmtRoutes.length;i<length;i+=1){
                const element = fmtRoutes[i];
                router.addRoute(element);
            }
            //将数据存入vuex，调用store的路由初始化方法
            store.commit('initRoutes',fmtRoutes);
        }
    })
}

//格式化路由函数
export const formatRoutes = (routes) => {
    let fmtRoutes = [];
    routes.forEach(router => {
        let {
            path,
            component,
            name,
            iconCls,
            children
        } = router;
        if(children && children instanceof Array){
            //递归
            children = formatRoutes(children);
        }
        let fmtRoute = {
            path:path,
            name:name,
            iconCls:iconCls,
            children:children,
            component(resolve){
                if(component.startsWith('Home')){
                    require(['../views/'+component+'.vue'],resolve);
                }else if(component.startsWith('Emp')){
                    require(['../views/emp/'+component+'.vue'],resolve);
                }else if(component.startsWith('Per')){
                    require(['../views/per/'+component+'.vue'],resolve);
                }else if(component.startsWith('Sal')){
                    require(['../views/sal/'+component+'.vue'],resolve);
                }else if(component.startsWith('Sta')){
                    require(['../views/sta/'+component+'.vue'],resolve);
                }else if(component.startsWith('Sys')){
                    require(['../views/sys/'+component+'.vue'],resolve);
                }
                
            }
        }
        fmtRoutes.push(fmtRoute)
    });
    return fmtRoutes;
}