## 基于SpringBoot版文件上传Web服务器
- 配置参数保存在application.yml文件

## 使用
- 上传单个文件
- 上传方式：Http表单文件上传（multipart/form-data）
- 上传地址（默认，可自行修改application.yml配置文件）: http://[ip]:8080/upload
## 接口说明
### 请求参数
|字段名称 |类型|是否必传项|            说明      |
|--------|----------|------|-------|
filePart|文件域|是|上传的文件|
overwrite|string|否|是否可以覆盖已存在文件，默认为false，不允许覆盖
dirPath|string|否|上传文件存放的目录，不传时默认存放在default目录下

### 响应
- JSON返回

|字段名称 |类型      |说明  |
|--------|----------|------|
resCode|string|1：成功处理，否则出错
resMsg|string|错误响应时，返回详细错误信息
filePath|string|成功时，返回保存文件路径
