# Spring Cloud Eureka 笔记

## 简介
> Spring Cloud Eureka 是 Spring Cloud Netfix 微服务套件中的一部分，它基于 Netfix Eureka 做了二次封装，主要负责完成微服务架构中的
服务治理功能。按使用分：
1.Eureka 服务端，也称为服务注册中心，支持高可用，依托于强一致性提供良好的服务实例可用性，可以应对多种不同的故障
场景；
2.Eureka 客户端，主要处理服务的注册与发现。

## 搭建服务注册中心（springcloud_eureka_server）
* pom 中引入 spring-cloud-starter-eureka-server 依赖
* Spring Boot 的启动类中，添加 @EnableEurekaServer 注解启动一个服务注册中心，提供给其它应用进行对话
* 启动 Eureka Server 后，就访问 http://localhost:1111/ Eureka 信息面板

## 注册服务提供者（springcloud_eureka_client）
* pom 中引入 spring-cloud-starter-eureka 依赖
* Spring Boot 的启动类中，添加 @EnableDiscoveryClient 注解激活 Eureka 中的 DiscoveryClient 实现（自动化配置，创建 DiscoveryClient 
接口针对 Eureka 端的 EurekaDiscoveryClient 实例）
* 配置文件中通过 spring.application.name 属性来为服务命名，通过 eureka.client.service-url.defaultZone 属性指定服务注册中心的地址
* 通过自动注入进来的 DiscoveryClient 实例，就可以发现服务了

## 高可用注册中心
* Eureka Server 的高可用实际上就是将自己作为服务向其他服务注册中心注册自己，这样就形成了一组相互注册的服务注册中心，以实现服务清单的相互同步，达到高可用的效果
> 如：1.分别配置 application-peer1.properties 作为 peer1 服务中心的配置，并将 serviceUrl 指向 pee2，同理，创建 application-peer2.properties 
作为 peer2 服务中心的配置，并将 serviceUrl 指向 peer1;
2.配置 peer1 和 peer2 的 host；
3.通过 spring.profiles.active 属性来分别启动 peer1 和 peer2;
4.服务方修改 eureka.client.service-url.defaultZone 配置指向注册中心集群
* 通过 eureka.instance.prefer-ip-address=true 的配置，强制使用 IP 地址的形式来注册服务的地址，而不是主机名，该值默认为 false

## 服务发现与消费
* 服务发现的任务由 Eureka 的客户端完成，而服务消费的任务由 Ribbon 完成。
Ribbon 是一个基于 HTTP 和 TCP 的客户端负载均衡器，通过客户端配置的 ribbonServerList 服务端列表去轮询访问以达到均衡负载的作用。
当 Ribbo 和 Eureka 联合使用时，Ribbon 的服务实例清单 RibbonServerList 会被 DiscoveryEnabledNIWSServerList 重写，扩展成从 Eureka
注册中心中获取服务等列表。同时它也会用 NIWSDiscoveryPing 来取代 IPing，将职责委托给 Eureka 来确定服务端是否已经启动
* 实现步骤 
> 1.启动服务注册中心 eureka-server 以及 hello-service 服务（1到N个）
2.创建一个 Spring Boot 的基础工程来实现服务消费者，如：springcloud_eureka_client_ribbon，并在 pom.xml 中，除 eureka 外，引入 Ribbon
模块的依赖 spring-cloud-starter-ribbon
3.在主类添加 @EnableDiscoveryClient 让该应用注册为 Eureka 客户端应用以获取服务发现的能力。同时，在主类中创建 RestTemplate 的 SpringBean
实例，并通过 @LoadBalanced 注解开启客户端负载均衡，如：SpringcloudEurekaClientRibbonApplication
4.在调用第三方服务的地方，通过自动注入 RestTemplate 来调用第三方服务，注意访问地址是服务名，而不是一个具体的地址，如：ConsumeController
5.在 application.properties 中配置 Eureka 服务注册中心的位置，启动应用后，就可以通过该 ribbon 应用访问第三方服务了

## 常用概念及配置
* Eureka 中有 Region 和 Zone 的概念，一个 Region 中可以包含多个 Zone，每个服务客户端需要被注册到一个 Zone 中，所以每个客户端对应一个
Region 和一个 Zone。在进行服务调用的时候，优先访问同处一个 Zone 中的服务提供方，若访问不到，就访问其他的 Zone
* eureka.instance.lease-renewal-interval-in-seconds 参数用于定义服务续约任务的调用间隔时间，默认为30秒
* eureka.instance.lease-expiration-duratioin-in-seconds 参数用于定义服务失效的时间，默认为90秒
* eureka.server.enable-self-preservation 参数控制是否开启注册中心的保护机制，默认为 true 开启状态，建议设置为 false 关闭，
以确保注册中心可以将不可用的实例正确剔除
> Eureka Server 在运行期间，会统计心跳失败的比例在15分钟之内是否低于85%，如果出现低于的情况（通常是由于网络不稳定导致），Eureka Server
会将当前的实例注册信息保护起来，让这些实例不会过期，尽可能保护这些信息。但这时候很可能会出现服务调用失败的情况，所以客户端需要有容错机制，
比如可以使用请求重试、断路器等机制。
* eureka.client.enabled 启用 eureka 客户端，默认值为 true
* eureka.client.eurekaServerReadTimeoutSeconds 读取 Eureka Server 信息的超时时间，单位为秒，默认为8
* eureka.client.eurekaServerConnectTimeoutSeconds 连接 Eureka Server 的超时时间，单位为秒，默认为5
* eureka.instance.instanceId 实例名配置，用于区别同一服务中不同实例的标识，默认规则：${spring.cloud.client.hostname}:${spring.application.name}:${spring.aaplication.instance_id:${server.port}}，
所以，如果同一主机上启动多个实例会产生端口冲突，可同过设置实例名为 eureka.instance.instanceId=${spring.application.name}:${random.int} 来解决

* 端点配置
> 状态页和健康检查的 URL 在 Spring Cloud Eureka 中默认使用了 spring-boot-actuator 模块提供的/info端点和/health端点，分别用于服务注册中心根据应用
健康来更改状态 和 在 Eureka 面板中单击实例时，无法访问到服务实例提供的信息接口。一般情况下不需要修改，但在一些特殊情况下，如为应用设置了 context-path后，
会在 actuator 模块的监控端点增加一个前缀，这时，需要做类似的配置，为/info和/health端点也加上类似的配置：
> management.context-path=/hello
  eureka.instance.statusPageUrlPath=${management.context-path}/info
  eureka.instance.healthCheckUrlPath=${management.context-path}/health
* 监控检测 
> 默认情况下，Eureka 各服务实例的健康检测是通过心跳来实现的，有可能会出现僵尸程序的问题，最好的方法是把 Eureka 客户端的健康检查交给 spring-boot-actuator
模块的/health端点，步骤如下：
1.pom.xml 中加入 spring-boot-starter-actuator 模块的依赖；
2.在 application.properties 中增加参数配置 eureka.client.healthcheck.enabled=true；
3.如果客户端的/health端点做了特殊处理，则也需为/health加上相应的配置

* eureka.instance.preferIpAddress 是否优先使用 IP 地址作为主机名的标识，默认值为 false
* eureka.instance.appname 服务名，默认取 spring.application.name 的配置值，如果没有则为 unknown
* eureka.instance.hostname 主机名，不配置的时候将根据操作系统的主机名来获取


