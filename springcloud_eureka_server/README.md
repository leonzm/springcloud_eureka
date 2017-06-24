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


