# SimpleHttp

一个简易的http服务器，用于处理并返回指定尺寸的图片。当然其他静态资源也是支持的。这个小工具编写的初衷是解决图像尺寸大于所需要的尺寸（div的尺寸）造成图像加载较慢的问题。



## 配置

config.yml （在第一次启动工具时生成）

```yaml
# 运行访问的文件类型
AccessType:
  'js': 'application/x-javascript'
  'css': 'text/css'
  'jpg': 'image/jpeg'
  'png': 'image/png'
  'gif': 'image/gif'
  'wolf': 'font/woff'
  'svg': 'image/svg+xml'
  'eot': 'application/vnd.ms-fontobject'
  'ttf': 'application/octet-stream'
  'txt': 'text/plain'
# 网站根目录
WebRoot: './'
# 监听的端口
ListenPort: 8080
# 处理的文件大小限制，单位 Bytes, 默认值为1M.
MaxFileSize: 1048576
# 线程池大小
ThreadPoolSize: 10
# 图像处理限制
ImageProcess:
  MaxWidth: 1024
  MaxHeight: 1024
# 是否打印输出
Debug: true
```

同时建议配合nginx反向代理使用，在nginx.conf server{}中加入

```nginx
	location ~ \.(gif|jpg|jpeg|png)$ {
		sendfile off;
		proxy_pass         http://localhost:8080;
		proxy_redirect     default;
		proxy_http_version 1.1;

		proxy_set_header   Host              $host;
		proxy_set_header   X-Real-IP         $remote_addr;
		proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
		proxy_set_header   X-Forwarded-Proto $scheme;
		proxy_max_temp_file_size 0;
	}
```

这样就可用使用SimpleHttp处理图片缩放请求。



## 需求

由于使用了sun.net包，所以需要Oracle Java的rt包才能运行。

由于使用了Stream API和lambda，需要使用Java8或者以上版本运行。

目前请求url指定图片尺寸目前仅支持腾讯云cdn的格式，例如

>  test.jpg?imageMogr2/scrop/128x128

本工具并不提供图片url改写功能，这个功能实现可能需要借助一些插件。



## 下载

https://github.com/ColorsWind/SimpleHttp/releases



## 如何编译

SimpleHttp 使用了maven管理依赖

maven来构建这个插件

```bash
git clone https://github.com/ColorsWind/VirtualMenu.git
mvn clean install
```



## 如何使用

开箱即用，使用java -jar SimpleHttp.jar 即可启动。

对于Windows系统，需要添加启动参数 -Dfile.encoding=utf8 指定使用utf8编码

对于Linux系统，你可能需要screen来确保jvm进程不能会在会挂关闭后被终止，或者使用service。

具体方案可以搜索解决，启动的脚本的模板可以在这个项目的git仓库找到。