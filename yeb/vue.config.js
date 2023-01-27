// const { defineConfig } = require('@vue/cli-service')
// module.exports = defineConfig({
//   transpileDependencies: true
// })

//新建一个代理对象
let proxyObj = {}

//代理路径是'/'
proxyObj['/']={
  //websocket关闭，先只做一个普通的代理
  ws:false,
  //代理到哪里去
  target:'http://localhost:8081',
  //发送请求头host会被设置为target
  changeOrigin:true,
  //不重写请求地址
  pathReWrite:{
    '^/':'/'
  }
}

module.exports={
  devServer:{
    host:'localhost',
    //前端访问8080,后端转发到8081
    port:8080,
    proxy:proxyObj
  }
}