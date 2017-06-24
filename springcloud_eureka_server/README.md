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






