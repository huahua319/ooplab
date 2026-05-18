# Lab5

本目录是 OOPLab 的 Lab5 独立项目，基于 Lab4 的多游戏终端平台继续迭代。

## 实现内容

Lab5 已实现以下功能：

- 保留 Lab4 的 `peace`、`reversi`、`minesweeper` 行为。
- 新增 `chess` 国际象棋模式。
- 程序启动时默认创建 4 局游戏：
  - Game 1: `peace`
  - Game 2: `reversi`
  - Game 3: `minesweeper`
  - Game 4: `chess`
- 支持运行时新增 `peace`、`reversi`、`minesweeper`、`chess`。
- 支持通过编号、`switch N`、`s N` 切换游戏，并保留各局状态。
- 保留左 / 中 / 右控制台布局。
- 新增 demo 模式，自动演示各游戏。
- 采用插件式结构，各游戏通过统一 `GamePlugin` 接口接入游戏大厅。

`chess` 支持基本棋子行棋、吃子、王车易位、吃过路兵、兵升变，以及 Lab5 PDF 要求的“吃掉王即结束”规则。不实现和棋规则。

## 环境要求

- JDK 17
- Maven

## 运行方式

在 `lab5/` 目录下运行：

```bash
mvn clean compile exec:java
```

只编译：

```bash
mvn -q -DskipTests compile
```

直接进入 demo 模式：

```bash
mvn clean compile exec:java -Dexec.args=demo
```

## 常用命令

全局命令：

- `quit`：退出程序
- `demo`：在普通模式中启动自动演示
- `peace` / `reversi` / `minesweeper` / `chess`：新增对应游戏
- `2`、`3`、`4`：按编号切换游戏
- `switch 2`、`s 2`：按命令切换游戏

坐标支持 `1a` 和 `a1` 两种形式，大小写不敏感。

Chess 命令：

- `m 7a 5a`
- `move 7a 5a`

兵升变可附加 `q`、`r`、`b`、`n`，未指定时默认升变为后。

## 说明

已结束的游戏仍可切换查看，但不再接受局内操作。
