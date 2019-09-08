# SQL分类

## DDL数据定义语言

关于databases、table本身的操作：定义了不同的数据段、数据库、表、列、索引等数据哭对象的定义。比如：create、drop和alter

## DML数据操纵语句

关于table内部的数据的操作：用于添加、删除、更新和查询数据库记录，并检查数据的完整性。

比如：insert、delete、update和select

## DCL数据控制语句

用于控制不同的数据段直接许可和访问级别相关的语句。

使用这个语句进入mysql： **/usr/local/MySQL/bin/mysql -u root -p**

# DDL语句

## 数据库的操作

### 创建数据库

使用语句

```sql
create database test1;
```

来创建一个数据库。

### 选择数据库

使用语句

```sql
use test1;
```

### 显示数据库中的表

使用语句

```sql
show tables
```

在选中了一个database之后，显示这个database中的所有table

### 删除数据库

使用语句

```sql
drop database test1;
```

来删除一个database

## 表的操作

### 创建表

```sql
create table emp(
	ename varchar(8),
	age int,
	birthday date);
```

### 查看表

使用语句

```sql
desc emp;
```

查看一个名字为emp的表

### 删除表

使用语句

```sql
drop table emp ;
```

删除一个名字为emp的表

### 修改表

#### 修改列的类型

```sql
alter table emp modify ename varchar(20);
```

将table emp中的ename列的数据类型变为长度为20位的字符串型

#### 增加列

```sql
alter table emp add column r varchar(20);
```

在emp这个表中添加一列，列名为school，数据类型为20个长度的字符串

#### 删除列

```sql
alter table emp drop column age;
```

删除table emp中的age列

#### 列改名

```sql
alter table emp change school schools int(13);
```

将table emp中的的school列改名为schools 并且将其数据类型变为长度为13的int型

#### 修改列顺序

```sql
alter table emp modify schools int(13) after ename;
```

将名称为schools 类型为int(13) 的列放在ename的后面

#### 表改名

```sql
alter table emp rename empl;
```

将table emp改名为empl

# DML语句

DML语句是针对table中的数据的所进行的操作语句。例如：insert、update、select、delete等方法。

## 测试所用数据库

```sql
mysql> select * from table1;
+--------+------+
| ename1 | age1 |
+--------+------+
| a      |    1 |
| b      |    2 |
| c      |    3 |
| d      |    4 |
+--------+------+
4 rows in set (0.00 sec)

mysql> select * from table2;
+--------+------+
| ename2 | age2 |
+--------+------+
| e      |    1 |
| f      |    2 |
| g      |    3 |
| h      |    4 |
+--------+------+
4 rows in set (0.00 sec)
```

## 插入方法

```sql
insert into empl (ename,schools,birthdayss) values('xzj','23','2018-7-8');
```

## 更新

### 单表更新

```sql
update empl set schools = '100' where ename = 'xzj';
```

将ename为 xzj的那一行的schools 变为 100 

### 多表更新

```sql
update table1 a,table2 b set a.age = b.age where a.age = b.age - 4;
```

## 删除行

```sql
delete table1 where ename = 'xxx';
```

## 查询方法

### 所有列

```sql
select * from table1; 
```

查询这个表中的所有的行

### 部分列

```sql
select age from table1 where ename = 'yyy';
```

查询这个table中的部分列，并且要求`ename`为 `yyy`。

### 不重复查询

```sql
select distinct age from table1;
```

使用`distinct`将这个列中的不重复元素查询出来，但是没有对应的行的信息

### 条件查询

```sql
select * from table1 where age <= 20;
```

使用这个语句将`table1`中的age项`<=`20的挑选出来，可以使用`or`和`and`作为`|` 、`&`。

```sql
select * from table1 where age > 22 or ename = 'lmn';
```

### 结果排序

```sql
select * from table1 order by age;
```

如果存在多个需要排序的项，那么以前后的顺序表明排序的优先级

如果某项要求按降序排列，那么在列名后加上`desc`标记

```sql
select * from table1 where age > 22 or ename = 'lmn' order by ename desc,age;
```

### 聚合

#### 聚合筛选

使用`group by` 将表中的数据分类在一起，然后使用having count(1) > 1 将其中的统计数`>1`的查询出来。

```sql
 select age,count(1) from table1 group by age having count(1) > 1;
```

#### 使用函数 

使用函数，在规定的列中选择特定项目

```sql
select max(age),min(age) from table1;
```

### 表连接

```sql
select ename1,ename2 from table1,table2 where table1.age1 = table2.age2;
```

两个表中的不同的列中的行，可以通过上述语句连接起来。

### 子查询

```sql
select * from table1 where age1 in (select age2 from table2 where ename2 = 'f');
```

选择在`table2` 中的`f`对应的ename的age，再使用这个age选择出在`table1`中的行

*可以使用 ’=’ 代替 ’in’*

# DCL语句

通常是用来管理系统的权限的操作。一般的开发人员基本不会用到。

# 数值类型

| 类型         | 大小                                     | 范围（有符号）                                               | 范围（无符号）                                               | 用途            |
| :----------- | :--------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- | :-------------- |
| TINYINT      | 1 字节                                   | (-128，127)                                                  | (0，255)                                                     | 小整数值        |
| SMALLINT     | 2 字节                                   | (-32 768，32 767)                                            | (0，65 535)                                                  | 大整数值        |
| MEDIUMINT    | 3 字节                                   | (-8 388 608，8 388 607)                                      | (0，16 777 215)                                              | 大整数值        |
| INT或INTEGER | 4 字节                                   | (-2 147 483 648，2 147 483 647)                              | (0，4 294 967 295)                                           | 大整数值        |
| BIGINT       | 8 字节                                   | (-9,223,372,036,854,775,808，9 223 372 036 854 775 807)      | (0，18 446 744 073 709 551 615)                              | 极大整数值      |
| FLOAT        | 4 字节                                   | (-3.402 823 466 E+38，-1.175 494 351 E-38)，0，(1.175 494 351 E-38，3.402 823 466 351 E+38) | 0，(1.175 494 351 E-38，3.402 823 466 E+38)                  | 单精度 浮点数值 |
| DOUBLE       | 8 字节                                   | (-1.797 693 134 862 315 7 E+308，-2.225 073 858 507 201 4 E-308)，0，(2.225 073 858 507 201 4 E-308，1.797 693 134 862 315 7 E+308) | 0，(2.225 073 858 507 201 4 E-308，1.797 693 134 862 315 7 E+308) | 双精度 浮点数值 |
| DECIMAL      | 对DECIMAL(M,D) ，如果M>D，为M+2否则为D+2 | 依赖于M和D的值                                               | 依赖于M和D的值                                               | 小数值          |

# 日期和时间类型

| 类型      | 大小 (字节) | 范围                                                         | 格式                | 用途                     |
| :-------- | :---------- | :----------------------------------------------------------- | :------------------ | :----------------------- |
| DATE      | 3           | 1000-01-01/9999-12-31                                        | YYYY-MM-DD          | 日期值                   |
| TIME      | 3           | '-838:59:59'/'838:59:59'                                     | HH:MM:SS            | 时间值或持续时间         |
| YEAR      | 1           | 1901/2155                                                    | YYYY                | 年份值                   |
| DATETIME  | 8           | 1000-01-01 00:00:00/9999-12-31 23:59:59                      | YYYY-MM-DD HH:MM:SS | 混合日期和时间值         |
| TIMESTAMP | 4           | 1970-01-01 00:00:00/2038 结束时间是第 **2147483647** 秒，北京时间 **2038-1-19 11:14:07**，格林尼治时间 2038年1月19日 凌晨 03:14:07 | YYYYMMDD HHMMSS     | 混合日期和时间值，时间戳 |

# 字符串类型

| 类型       | 大小                | 用途                            |
| :--------- | :------------------ | :------------------------------ |
| CHAR       | 0-255字节           | 定长字符串                      |
| VARCHAR    | 0-65535 字节        | 变长字符串                      |
| TINYBLOB   | 0-255字节           | 不超过 255 个字符的二进制字符串 |
| TINYTEXT   | 0-255字节           | 短文本字符串                    |
| BLOB       | 0-65 535字节        | 二进制形式的长文本数据          |
| TEXT       | 0-65 535字节        | 长文本数据                      |
| MEDIUMBLOB | 0-16 777 215字节    | 二进制形式的中等长度文本数据    |
| MEDIUMTEXT | 0-16 777 215字节    | 中等长度文本数据                |
| LONGBLOB   | 0-4 294 967 295字节 | 二进制形式的极大文本数据        |
| LONGTEXT   | 0-4 294 967 295字节 | 极大文本数据                    |

# 循环体的写法

```sql
DROP PROCEDURE IF EXISTS `test`;
delimiter ;;
CREATE DEFINER=`dtstack`@`%` PROCEDURE `test`()
begin
    declare i int;                      #申明变量
    set i = 4668;                          #变量赋值
    repeat
        insert into ads_table (table_id) values (i);    #往test表添加数据
        set i = i + 1;                  #循环一次,i加一
    until i > 4725 end repeat;            #结束循环的条件: 当i大于10时跳出repeat循环
end;
;;
delimiter ;


DROP PROCEDURE IF EXISTS `test1`;
delimiter ;;
CREATE DEFINER=`dtstack`@`%` PROCEDURE `test1`( )
BEGIN
	DECLARE
		i long;#申明变量
	
	SET i = 4668;#变量赋值
	WHILE
			i < 5972 DO#结束循环的条件: 当i大于10时跳出while循环
			INSERT INTO ads_table (table_id)
		VALUES
			( i );#往test表添加数据
		
		SET i = i + 1;#循环一次,i加一
		
	END WHILE;#结束while循环
END;
;;
delimiter ;

```

