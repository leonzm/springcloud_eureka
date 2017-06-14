# Spring Cloud Eureka 笔记

## 简介
> Spring Cloud Eureka 是 Spring Cloud Netfix 微服务套件中的一部分，它基于 Netfix Eureka 做了二次封装，主要负责完成微服务架构中的服务治理功能。按使用分：1.Eureka 服务端，也称为服务注册中心，支持高可用，依托于强一致性提供良好的服务实例可用性，可以应对多种不同的故障场景；2。Eureka 客户端，主要处理服务的注册与发现。

## 搭建服务注册中心（springcloud_eureka_server）
* pom 中引入 spring-cloud-starter-eureka-server 依赖
* Spring Boot 的启动类中，添加 @EnableEurekaServer 注解启动一个服务注册中心，提供给其它应用进行对话
* 启动 Eureka Server 后，就访问 http://localhost:1111/ Eureka 信息面板

## 注册服务提供者（springcloud_eureka_client）
* pom 中引入 spring-cloud-starter-eureka 依赖
* Spring Boot 的启动类中，添加 @EnableDiscoveryClient 注解激活 Eureka 中的 DiscoveryClient 实现（自动化配置，创建 DiscoveryClient 接口针对 Eureka 端的 EurekaDiscoveryClient 实例）
* 配置文件中通过 spring.application.name 属性来为服务命名，通过 eureka.client.service-url.defaultZone 属性指定服务注册中心的地址
* 通过自动注入进来的 DiscoveryClient 实例，就可以发现服务了

## 高可用注册中心


## 服务发现与消费


