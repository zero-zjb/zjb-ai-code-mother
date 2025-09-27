/**
 * 配置对象，用于定义API文档生成和请求库的相关路径配置
 */
export default {
  /**
   * 请求库的导入路径配置
   * 指定在生成的代码中如何导入request模块
   */
  requestLibPath: "import request from '@/request'",

  /**
   * API文档的Schema路径
   * 指向OpenAPI规范的JSON文件地址，用于生成类型定义和接口代码
   */
  schemaPath: 'http://localhost:8123/api/v3/api-docs',

  /**
   * 生成代码的输出路径
   * 指定生成的API客户端代码存放的目录位置
   */
  serversPath: './src',
}
