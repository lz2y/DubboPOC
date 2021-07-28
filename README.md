# DubboPOC
Apache Dubbo 漏洞POC

* 持续更新中
  - [ ] CVE-2019-17564
  - [ ] CVE-2020-1948
  - [x] CVE-2020-1948绕过
  - [ ] CVE-2021-25641
  - [x] CVE-2021-30179
  - [ ] others
* 免责声明
  * 项目仅供学习使用，任何未授权检测造成的直接或者间接的后果及损失，均由使用者本人负责
* 注意事项
  * 需要用到JNDI注入的POC请于低版本的JDK版本测试（项目在8u71中测试）
    > * 基于RMI的利用方式，JDK版本限制于6u132、7u131、8u121之前，在8u122及之后的版本中，加入了反序列化白名单的机制，关闭了RMI远程加载代码
    > * 基于LDAP的利用方式，JDK版本限制于6u211、7u201、8u191、11.0.1之前，在8u191版本中，Oracle对LDAP向量设置限制，发布了CVE-2018-3149，关闭JNDI远程类加载
    > * From：https://www.freebuf.com/vuls/279465.html

* 参考链接

  * [GHSL-2021-034_043: Multiple pre-auth RCEs in Apache Dubbo](https://securitylab.github.com/advisories/GHSL-2021-034_043-apache-dubbo/)
  * [dubbo源码浅析：默认反序列化利用之hessian2](https://www.anquanke.com/post/id/197658)
