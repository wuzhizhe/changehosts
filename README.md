  
  NodeJs写的小工具用来更新hosts文件

  需要Node npm

  配置config.json文件，配置项说明：

  proxy: 如果本地网络不能直接上网，可以设置代理。

  sourceList: 要获取的hosts源，支持多个hosts源，以数组形式罗列就可以。

  hostsPath: hosts文件的完整绝对路径，这里用的是windows的默认配置。

  npm install之后执行node index.js即可（windows下需要管理员权限）

  在windows 10以及centos6下测试成功，其他平台没有条件未做测试。