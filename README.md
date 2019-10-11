# 数据库比对修改
这个程序采用了SpringBoot，MyBatis，和FastJSON实现数据库结构的对比和生成SQL语句。
该程序支持多数据库，目前支持MySQL和PostgreSQL。访问协议采取RESTful风格。
同时，此程序支持实时数据库切换。多数据库支持参考了GitHUb上的Spring Boot Dynamic and Multiple DataSource project / 
Spring Boot 动态数据源、多数据源切换项目。
在使用程序的时候，应当链接目标数据库，并在配置文件中指定输入数据库的JSON结构文件位置。
通过POST模式访问/dbComparator/MySQL/mergeDb，返回值为数据库需要更改的表和字段列表以及对应的SQL文件路径。


## Author

* **Fengyuan Wu** - [FWU](mailto:fwu@ucsd.edu)


## Acknowledgments

Spring Boot Dynamic and Multiple DataSource project / Spring Boot 动态数据源、多数据源切换
https://github.com/helloworlde/SpringBoot-DynamicDataSource
