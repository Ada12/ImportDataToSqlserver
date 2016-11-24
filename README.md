# ImportDataToSqlserver
Import different data source data into the sqlserver
***
### 一、源数据要求

##### 1.文件编码
所有倒入数据库的数据编码都应该是UTF-8编码
##### 2.文件格式
源数据的格式应为标准的csv或txt格式
其中csv应为如下格式：

```
data1,data2,data3,data4
```
其中txt格式应该是以\t为分隔符的文件，应为如下格式：
 
```
data1	data2	data3	data4
```
##### 2.setting文件
在导入数据之前，需要将所有数据的基础信息写入到setting文件，具体格式如下所示：

```
[
	{
		"DATABASE":"Environment",
		"INDEX":"AirPollution",
		"TABLE":"AQI",
		"FILEPATH":"\2015data\空气质量状况\环保aqi(1407-1504)\aqi数据.csv",
		"DESCRIBE":"该数据是定量描述空气质量状况的无量纲指数",
		"DATE_FORMAT":"yyyy-MM-dd hh:mm:ss",
		"FIELDS":[
					{"NAME":"TIME","TYPE":"DATETIME"},
					{"NAME":"SITEID","TYPE":"INT"},
					{"NAME":"AQI","TYPE":"INT"},
					{"NAME":"GRADE","TYPE":"INT"},
					{"NAME":"AIRQUALITY","TYPE":"VARCHAR"},
					{"NAME":"PPITEMID","TYPE":"INT"}
				]
	}
］
```
其中，DESCRIBE可以是中文，除了DESCRIBE都必须要求是英文或其与数字的组合，FILEPATH必须为真实有效的。在FIELDS中，名称为ID的字段为保留字，若为该字段，则该列的数据将被掉。同时，数据类型为DATETIME的日期类型，要求为标准的日期类型，为YYYY/MM/DD hh:mm:ss或者YYYY-MM-DD hh:mm:ss或者不带时间的日期。若所在列的内容与数据类型不符合，则被设置为空值。
