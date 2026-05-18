# ooplab

这是一个 Java 面向对象程序设计课程实验仓库。

仓库只保存代码和可以公开的实验材料，不上传带有个人信息的实验报告、提交压缩包、运行日志等私有文件。这些内容统一放在 `_private/` 中，并通过 `.gitignore` 忽略。

## 目录结构

- `lab1/`：Lab 1 历史快照
- `lab2/`：Lab 2 历史快照 / 参考实现
- `lab3/`：Lab 3 历史快照 / 参考实现
- `lab4/`：Lab 4 历史快照 / 参考实现
- `lab5/`：Lab 5 已完成项目
- `_private/`：私有实验报告、提交压缩包、日志等，不纳入 Git
- `SPEC.md`：仓库级开发规范
- `.gitignore`：忽略私有文件和构建产物

## 当前实验进度

### Lab 1

Lab 1 是基于 Java 17、Maven 和 Lanterna 的黑白棋初版程序，作为历史快照保留。

### Lab 2

Lab 2 在 Lab 1 基础上实现完整 Othello / Reversi 规则和多棋盘能力，作为后续实验参考实现保留。

### Lab 3

Lab 3 将项目演进为多游戏平台，支持 `peace` 和 `reversi` 两种模式、动态新增游戏和多 session 切换，作为后续实验参考实现保留。

### Lab 4

Lab 4 在 Lab 3 平台基础上新增 `minesweeper`，并保留 `peace`、`reversi`、多 session 管理、动态新增、切换保状态和左 / 中 / 右布局。Lab 4 已完成，作为 Lab 5 的参考基础保留。

### Lab 5

Lab 5 代码实现已完成，位于 `lab5/`。

已实现内容：

- 启动默认四局：
  - Game 1 = `peace`
  - Game 2 = `reversi`
  - Game 3 = `minesweeper`
  - Game 4 = `chess`
- 默认进入 Game 1
- 保留 Lab 4 的 `peace`、`reversi`、`minesweeper` 行为
- 支持运行时新增 `peace`、`reversi`、`minesweeper`、`chess`
- 新增游戏后不自动切换
- 支持裸数字、`switch N`、`s N` 切换游戏
- 切换后保留每个 session 的状态
- 已结束游戏仍可查看，但不可继续执行局内操作
- 保持左 / 中 / 右相对布局，并显示当前模式 Commands
- 新增 `chess` 国际象棋模式：
  - 基本棋子行棋
  - 吃子
  - 王车易位
  - 吃过路兵
  - 兵升变
  - 吃掉王即结束
- 新增 demo 模式，自动演示各游戏
- 采用插件式结构，各游戏通过统一 `GamePlugin` 接口接入游戏大厅

## 运行方式

环境要求：

- JDK 17
- Maven

运行 Lab 5：

```bash
cd lab5
mvn clean compile exec:java
```

仅编译检查：

```bash
cd lab5
mvn -q -DskipTests compile
```

直接进入 demo 模式：

```bash
cd lab5
mvn clean compile exec:java -Dexec.args=demo
```

## Lab 5 输入说明

通用命令：

- `quit`：退出整个程序
- `demo`：启动自动演示
- `peace`：新增一局 peace
- `reversi`：新增一局 reversi
- `minesweeper`：新增一局 minesweeper
- `chess`：新增一局 chess
- `2`、`3`、`4`、`switch 2`、`s 2`：切换游戏

`peace` / `reversi`：

- `1a`、`a1`、`8h`、`h8`：落子
- `pass`：仅在 reversi 当前玩家无合法落子时有效

`minesweeper`：

- `1a`、`a1`、`8h`、`h8`：翻开一个格子
- `f 1a`、`flag a1`：插旗 / 取消旗

`chess`：

- `m 7a 5a`、`move 7a 5a`：移动棋子
- 兵升变可附加 `q`、`r`、`b`、`n`，未指定时默认升变为后
- 不实现和棋规则

## 文件管理约定

以下内容保留在仓库中：

- 源代码
- `pom.xml` 等构建配置
- README / SPEC 等说明文档
- 可以公开的实验说明 PDF

以下内容放入 `_private/`，不上传到 GitHub：

- 带姓名、学号等个人信息的实验报告
- 提交用的 zip 压缩包
- 本地运行日志
- 其他不适合公开的材料

Lab PDF 作为只读需求参考，不作为源码变更修改或提交，除非用户明确要求或课程提交格式需要。
