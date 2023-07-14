from java.io import FileInputStream
import sys
 
propInputStream = FileInputStream("datasource.properties")
configProps = Properties()
configProps.load(propInputStream)
 
domainName=configProps.get("domain.name")
adminURL=configProps.get("admin.url")
adminUserName=configProps.get("admin.username")
adminPassword=configProps.get("admin.password")

if len(sys.argv) == 1:
   dsName=configProps.get("datasource.name")
   dsJNDIName=configProps.get("datasource.jndiname")
   dsDatabaseName=configProps.get("datasource.database.name")
   dsFileName=configProps.get("datasource.filename")
else:
   dsName=sys.argv[1]
   dsJNDIName=configProps.get("app.name") + '/' + dsName
   dsDatabaseName=dsName
   dsFileName=configProps.get("datasource.filename") + dsName
datasourceTarget=configProps.get("datasource.target")
dsDriverName=configProps.get("datasource.driver.class")
dsURL=configProps.get("datasource.url") + dsDatabaseName
dsUserName=configProps.get("datasource.username")
dsPassword=configProps.get("datasource.password")
dsTestQuery=configProps.get("datasource.test.query")
 
connect(adminUserName, adminPassword, adminURL)
edit()
startEdit()
cd('/')
cmo.createJDBCSystemResource(dsName)
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName)
cmo.setName(dsName)
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDataSourceParams/' + dsName )
set('JNDINames',jarray.array([String(dsJNDIName)], String))
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDriverParams/' + dsName )
cmo.setUrl(dsURL)
cmo.setDriverName( dsDriverName )
cmo.setPassword(dsPassword)
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCConnectionPoolParams/' + dsName )
cmo.setTestTableName(dsTestQuery)
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDriverParams/' + dsName + '/Properties/' + dsName )
cmo.createProperty('user')
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDriverParams/' + dsName + '/Properties/' + dsName + '/Properties/user')
cmo.setValue(dsUserName)
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDriverParams/' + dsName + '/Properties/' + dsName )
cmo.createProperty('databaseName')
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDriverParams/' + dsName + '/Properties/' + dsName + '/Properties/databaseName')
cmo.setValue(dsDatabaseName)
 
cd('/JDBCSystemResources/' + dsName + '/JDBCResource/' + dsName + '/JDBCDataSourceParams/' + dsName )
cmo.setGlobalTransactionsProtocol('OnePhaseCommit')
 
cd('/SystemResources/' + dsName )
set('Targets',jarray.array([ObjectName('com.bea:Name=' + datasourceTarget + ',Type=Server')], ObjectName))
 
save()
activate()
