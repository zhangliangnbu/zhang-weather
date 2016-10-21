# zhang-weather
《app研发录》的笔记代码

项目结构

·activity： 我们 按照 模块 继续 拆分， 将不 同 模块 的 Activity 划分 到 不同 的 包 下。
·adapter： 所有 适配器 都 放在 一起。
·entity： 将 所有 的 实体 都 放在 一起。

网络底层框架
底层封装线程池：

- url.xml统一存放url和相关参数；
- 以DefaultThreadPool统一管理网络请求和响应的线程池。

优化：

- 统一处理响应的部分回调，如失败的回调弹窗等；
- 优化读取url数据的缓存等。

缓存（网络响应的数据是否缓存, 仅get请求）：

- 项目启动初始化缓存目录；
- 修改url.xml中url的参数Expires，请求时判断是否从缓存中拿，响应后判断是否存入缓存。
- 重难点：缓存管理类、url的parameter排序保证URL唯一。

MockService(App端mock数据)：

- url.xml中增加MockClass字段，RemoteService在invoke时检查该字段，有则mock（反射出mock类）无则正常请求；
- MockService用于MockClass的父类，用于组装、获取成功的或失败的response。

登录和注册

- 保存全局登录状态实例。序列化本地，需要验证登录状态时用来判断；
- 登录场景。正常登录并进入APP、页面内操作判断登录、页面跳转判断登录；
- cookie机制。保存登录信息状态；过期统一处理。

http header使用

- 前后端规定的请求必传的关键字。如app-id， platform等；(???没能添加)
- 统一时间。统一使用服务端的head里的时间，与本地比较并保存差值，之后使用本地时间则加上这个差值。

书籍的问题
xml.url冲突问题， URL配置的位置问题

Native与H5交互 H5与Android Native的交互

- native操作H5页面
- H5操作native代码
- 统一的页面跳转（Native和H5）, 分发器。h5使用一个key表示跳转哪个页面， Android端通过设置一个字典如xml文件（key对应具体的activity类路径value）， 通过反射value获取类名并进行跳转。
